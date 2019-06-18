package com.csci412.classfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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
        public int color;

        public MatrixItem(int row, int col, String text) {
            this.id = (row*10) + col;
            this.row = row;
            this.col = col;
            this.text = text;
            this.color = 0;
        }
    }

    public static final List<ScheduleItem> SCHEDULES = new ArrayList<>();

    public static final Map<String, ScheduleItem> SCHEDULE_MAP = new HashMap<String, ScheduleItem>();

    public static ScheduleItem selectedSchedule;

    public static RecyclerView rv;

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
        rv.getAdapter().notifyDataSetChanged();
    }

    public static void addSchedule(ScheduleItem x) {
        SCHEDULES.add(x);
        SCHEDULE_MAP.put(x.name, x);
    }

    public static void removeSchedule(ScheduleItem item) {
        SCHEDULES.remove(item);
        SCHEDULE_MAP.remove(item.name);
        rv.getAdapter().notifyDataSetChanged();
    }

    public static void saveToSharedPrefs(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        JSONArray json = new JSONArray();
        for(ScheduleItem schedItem : SCHEDULES) {
            JSONObject obj = new JSONObject();
            try {
                JSONArray ja = new JSONArray();
                for (Course c: schedItem.classes) {
                    ja.put(new JSONObject(putCourseIntoJSON(c)));
                }
                obj.put("classes", ja);
                obj.put("name", schedItem.name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            json.put(obj);
        }
        prefsEditor.putString("json", json.toString());
        prefsEditor.commit();
    }

    public static List<ScheduleItem> getFromSharedPrefs(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        List<ScheduleItem> x = new ArrayList<>();

        String json = appSharedPrefs.getString("json", "");
        try {
            JSONArray ja = new JSONArray(json);
            int length = ja.length();
            JSONObject jo;
            String name;
            JSONArray classes;
            JSONObject classO;
            for (int i = 0; i < length; i++) {
                jo = ja.getJSONObject(i);
                classes = jo.getJSONArray("classes");
                name = jo.getString("name");
                ScheduleItem item = new ScheduleItem(name);
                for (int y = 0; y < classes.length(); y++) {
                    classO = classes.getJSONObject(y);
                    Course course = getCourseFromJSON(classO);
                    item.classes.add(course);
                }
                x.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return x;
    }

    public static Course getCourseFromJSON(JSONObject jo) {
        Course course = new Course();
        JSONArray ja;
        try {
            course.course = jo.getString("course");
            course.title = jo.getString("title");
            course.dept = jo.getString("dept");
            course.crn = jo.getString("crn");
            course.location = new ArrayList<>();
            ja = jo.getJSONArray("location");
            for(int i = 0; i < ja.length(); i++) {
                course.location.add(ja.get(i).toString());
            }
            course.instructor = jo.getString("instructor");
            course.dates = jo.getString("dates");
            course.chrgs = jo.getString("chrgs");
            course.credits = jo.getString("credits");
            course.attrs = jo.getString("attrs");
            course.times = new ArrayList<>();
            ja = jo.getJSONArray("times");
            for(int i = 0; i < ja.length(); i++) {
                course.times.add(ja.get(i).toString());
            }
            course.prereq = jo.getString("prereq");
            course.restrictions = jo.getString("restrictions");
            course.additional = new ArrayList<>();
            ja = jo.getJSONArray("additional");
            for(int i = 0; i < ja.length(); i++) {
                course.additional.add(ja.get(i).toString());
            }
            course.waitlist = jo.getBoolean("waitlist");
            course.cap = jo.getString("cap");
            course.enrl = jo.getString("enrl");
            course.avail = jo.getString("avail");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return course;
    }

    public static String putCourseIntoJSON(Course c) {
        String json;
        JSONObject jo = new JSONObject();
        try {
            jo.put("course", c.course);
            jo.put("title", c.title);
            jo.put("dept", c.dept);
            jo.put("crn", c.crn);
            JSONArray location = new JSONArray();
            for (int i = 0; i < c.location.size(); i++) {
                location.put(c.location.get(i));
            }
            jo.put("location", location);
            jo.put("instructor", c.instructor);
            jo.put("dates", c.dates);
            jo.put("chrgs", c.chrgs);
            jo.put("credits", c.credits);
            jo.put("attrs", c.attrs);
            JSONArray times = new JSONArray();
            for (int i = 0; i < c.times.size(); i++) {
                times.put(c.times.get(i));
            }
            jo.put("times", times);
            jo.put("prereq", c.prereq);
            jo.put("restrictions", c.restrictions);
            JSONArray additional = new JSONArray();
            for (int i = 0; i < c.additional.size(); i++) {
                additional.put(c.additional.get(i));
            }
            jo.put("additional", additional);
            jo.put("waitlist", c.waitlist);
            jo.put("cap", c.cap);
            jo.put("enrl", c.enrl);
            jo.put("avail", c.avail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        json = jo.toString();
        return json;
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
                    view.setBackgroundColor(view.getResources().getColor(R.color.white));
                }else if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENTERED){
                    view.setBackgroundColor(view.getResources().getColor(R.color.deparmentHighlight));
                }else if(dragEvent.getAction() == DragEvent.ACTION_DRAG_EXITED){
                    view.setBackgroundColor(view.getResources().getColor(R.color.white));
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

