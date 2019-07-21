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
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;


public class menuListActivity extends AppCompatActivity{

    private static boolean oneSelectMode; //only allows for one item to be selected at a time
    static ArrayList<item> content;
    static ArrayList<item> contentCopy;
    private item defaultValue;
    private static menuRecyclerViewAdapter adapter;
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        search = findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        content = new ArrayList<>();
        contentCopy = new ArrayList<>();
        Intent intent = getIntent();
        int l = 0;
        oneSelectMode = intent.getBooleanExtra("oneSelectMode",false);
        int length = intent.getIntExtra("length",l);
        String defaultName = intent.getStringExtra("default");
        boolean isSelected = intent.getBooleanExtra("defaultSel",true);
        item item = new item(defaultName ,isSelected);
        content.add(item);
        contentCopy.add(item);
        defaultValue = item;
        for(int i = 0; i < length; i++){
            String name = intent.getStringExtra("0" + i);
            isSelected = false;
            String s = intent.getStringExtra(name);
            if(s != null && s.equals("s")){
                isSelected = true;
            }
            item = new item(name ,isSelected);
            content.add(item);
            contentCopy.add(item);
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
        adapter = new menuRecyclerViewAdapter(this, content);
        recyclerView.setAdapter(adapter);
    }

    public void resetAttributes(View view) {
        for(int i = 0; i < content.size(); i++){
            content.get(i).setSelected(false);
        }
        defaultValue.setSelected(true);
        search.setQuery("", false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static class menuRecyclerViewAdapter
            extends RecyclerView.Adapter<menuRecyclerViewAdapter.ViewHolder> {

        private final menuListActivity mParentActivity;
        private ArrayList<item> mValues;

        menuRecyclerViewAdapter(menuListActivity parent, ArrayList<item> items) {
            mParentActivity = parent;
            mValues = items;

        }

        public void filter(String query) {
            mValues.clear();
            if(query.isEmpty()){
                mValues.addAll(contentCopy);
            } else{
                query = query.toLowerCase();
                for(int i = 0; i < contentCopy.size(); i++){
                    if(contentCopy.get(i).text.toLowerCase().contains(query)){
                        mValues.add(contentCopy.get(i));
                    }
                }
            }
            notifyDataSetChanged();
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
