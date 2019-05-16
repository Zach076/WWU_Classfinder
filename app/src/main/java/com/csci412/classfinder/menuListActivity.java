package com.csci412.classfinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.csci412.classfinder.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class menuListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    static ArrayList<item> content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        content = new ArrayList<>();
        Intent intent = getIntent();
        int l = 0;
        boolean b = true;
        int length = intent.getIntExtra("length",l);
        for(int i = 0; i < length; i++){
            content.add(new item(intent.getStringExtra("" + i),intent.getBooleanExtra("" + i + length,b)));
        }

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }




        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        for(int i = 0; i < content.size(); i++){
            intent.putExtra("" + i,content.get(i).selected);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new menuRecyclerViewAdapter(this, content, mTwoPane));
    }

    public static class menuRecyclerViewAdapter
            extends RecyclerView.Adapter<menuRecyclerViewAdapter.ViewHolder> {

        private final menuListActivity mParentActivity;
        private final boolean mTwoPane;
        private final ArrayList<item> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item item = (item) view.getTag();
                System.out.println(item.text);
            }
        };

        menuRecyclerViewAdapter(menuListActivity parent,
                                ArrayList<item> items,
                                boolean twoPane) {
            mParentActivity = parent;
            mTwoPane = twoPane;
            mValues = items;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.text.setText(mValues.get(position).text);
            holder.radioButton.setSelected(mValues.get(position).selected);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView text;
            final CheckBox radioButton;

            ViewHolder(View view) {
                super(view);
                radioButton = (CheckBox) view.findViewById(R.id.radioButton);
                text = (TextView) view.findViewById(R.id.radioButton);
            }
        }
    }
    private class item{
        String text;
        boolean selected;

        item(String t, boolean s) {
            selected = s;
            text = t;
        }
    }
}
