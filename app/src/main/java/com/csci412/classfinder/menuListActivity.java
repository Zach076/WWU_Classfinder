package com.csci412.classfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

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
    private static boolean oneSelectMode; //only allows for one item to be selected at a time
    static ArrayList<item> content;
    private item defaultValue;
    private static menuRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        content = new ArrayList<>();
        Intent intent = getIntent();
        int l = 0;
        oneSelectMode = intent.getBooleanExtra("oneSelectMode",false);
        int length = intent.getIntExtra("length",l);
        String defaultName = intent.getStringExtra("default");
        for(int i = 0; i < length; i++){
            String name = intent.getStringExtra("0" + i);
            boolean isSelected = false;
            String s = intent.getStringExtra(name);
            if(s != null && s.equals("s")){
                isSelected = true;
            }
            item item = new item(name ,isSelected);
            content.add(item);
            if(name.equals(defaultName)){
                System.out.println("assigned");
                defaultValue = item;
            }
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        int size = 0;
        for(int i = 0; i < content.size(); i++){
            item item = content.get(i);
            if(item.selected) {
                intent.putExtra("" + size, item.text);
                size++;
            }
        }
        intent.putExtra("length",size);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new menuRecyclerViewAdapter(this, content, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    public void resetAttributes(View view) {
        for(int i = 0; i < content.size(); i++){
            content.get(i).setSelected(false);
        }
        defaultValue.setSelected(true);
        adapter.notifyDataSetChanged();
    }

    public static class menuRecyclerViewAdapter
            extends RecyclerView.Adapter<menuRecyclerViewAdapter.ViewHolder> {

        private final menuListActivity mParentActivity;
        private final boolean mTwoPane;
        private ArrayList<item> mValues;;

        menuRecyclerViewAdapter(menuListActivity parent, ArrayList<item> items, boolean twoPane) {
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
        public void onBindViewHolder(ViewHolder holder, int position) {
            final item i = mValues.get(position);
            holder.checkBox.setText(i.text);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(i.selected);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(oneSelectMode){
                        if(isChecked == true) {
                            for (int i = 0; i < mValues.size(); i++) {
                                mValues.get(i).setSelected(false);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    i.setSelected(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CheckBox checkBox;

            ViewHolder(View view) {
                super(view);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
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

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
