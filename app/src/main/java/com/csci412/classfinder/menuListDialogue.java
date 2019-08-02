package com.csci412.classfinder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;


public class menuListDialogue extends DialogFragment {

    private static int REQUEST_CODE;

    private static boolean oneSelectMode; //only allows for one item to be selected at a time
    private static boolean isSubjects;
    static ArrayList<item> content;
    static ArrayList<item> contentCopy;
    private static item defaultValue;
    private static menuRecyclerViewAdapter adapter;
    private SearchView search;
    private static filter_layout parentFragment;
    private static AlertDialog dialog;

    public menuListDialogue() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static menuListDialogue newInstance(String title, String[] names, ArrayList<String> isSelected, String defaultVal, Boolean oneSelect, Boolean isSubeject, filter_layout parent, int requestcode) {
        REQUEST_CODE = requestcode;
        parentFragment = parent;
        oneSelectMode = oneSelect;
        isSubjects = isSubeject;
        menuListDialogue frag = new menuListDialogue();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArray("names",names);
        args.putStringArrayList("isSelected",isSelected);
        args.putString("default",defaultVal);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.menu_list, container);
        view.findViewById(R.id.resetMenuAttribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAttributes(null);
            }
        });
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView title = view.findViewById(R.id.menuTitle);
        title.setText(getArguments().getString("title"));
        search = view.findViewById(R.id.searchView);
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
        View recyclerView = view.findViewById(R.id.item_list);
        if(recyclerView != null) {
            setupRecyclerView((RecyclerView) recyclerView);
        }
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Bundle resultData = new Bundle();
        int size = 0;
        for(int i = 0; i < content.size(); i++){
            item item = content.get(i);
            if(item.selected) {
                resultData.putString("" + size, item.text);
                size++;
            }
        }
        resultData.putInt("length",size);
        parentFragment.onDialogDismiss(REQUEST_CODE, resultData);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        String title = args.getString("title");
        String[] names = args.getStringArray("names");
        ArrayList<String> isSelected = args.getStringArrayList("isSelected");
        String defaultName = args.getString("default");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        item item = new item(defaultName ,isSelected.contains(defaultName));
        defaultValue = item;
        content = new ArrayList<>();
        contentCopy = new ArrayList<>();
        content.add(item);
        contentCopy.add(item);
        for(int i = 0; i < names.length; i++){
            String name = names[i];
            item = new item(name, isSelected.contains(name));
            content.add(item);
            contentCopy.add(item);
        }

        dialog = alertDialogBuilder.create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
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


    public static class menuRecyclerViewAdapter
            extends RecyclerView.Adapter<menuRecyclerViewAdapter.ViewHolder> {

        private final menuListDialogue mParentActivity;
        private ArrayList<item> mValues;

        menuRecyclerViewAdapter(menuListDialogue parent, ArrayList<item> items) {
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
                    }else if(isSubjects){
                         if(defaultValue.selected){
                             defaultValue.setSelected(false);
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