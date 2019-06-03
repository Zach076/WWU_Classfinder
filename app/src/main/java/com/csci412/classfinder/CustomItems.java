package com.csci412.classfinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CustomItems {

    public static class MatrixItem {
        public int id;
        public int row;
        public int col;
        public String text;

        public MatrixItem(int row, int col, String text) {
            this.id = (row*10) + col;
            this.row = row;
            this.col = col;
            this.text = text;
        }
    }

    public static final List<ScheduleItem> SCHEDULES = new ArrayList<>();

    public static final Map<String, ScheduleItem> SCHEDULE_MAP = new HashMap<String, ScheduleItem>();

    public static ScheduleItem selectedSchedule;

    public static class ScheduleItem {
        public List<Course> classes;
        public String name;

        public ScheduleItem(String name) {
            this.name = name;
            this.classes = new ArrayList<>();
        }
    }

    public static void addSchedule(String name) {
        ScheduleItem item = new ScheduleItem(name);
        SCHEDULES.add(item);
        SCHEDULE_MAP.put(name, item);
    }

    public static void removeSchedule(ScheduleItem item) {
        SCHEDULES.remove(item);
        SCHEDULE_MAP.remove(item.name);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<CustomItems.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final MainActivity mParentActivity;
        private final List<CustomItems.ScheduleItem> mValues;
        private final View.OnDragListener mOnDragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                CustomItems.ScheduleItem item = (CustomItems.ScheduleItem) view.getTag();
                if(dragEvent.getAction() == DragEvent.ACTION_DROP){
                    Course x = (Course)dragEvent.getClipData().getItemAt(0).getIntent().getSerializableExtra("course");
                    item.classes.add(x);
                }
                return true;
            }
        };
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomItems.ScheduleItem item = (CustomItems.ScheduleItem) view.getTag();
                selectedSchedule = item;

                Context context = view.getContext();
                Intent intent = new Intent(context, SchedViewActivity.class);
                intent.putExtra(SchedViewFragment.ARG_ITEM_ID, item.name);

                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(MainActivity parent,
                                      List<CustomItems.ScheduleItem> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public CustomItems.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schedule_list_content, parent, false);
            return new CustomItems.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CustomItems.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).name);
            holder.mContentView.setText("");

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setOnDragListener(mOnDragListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}

