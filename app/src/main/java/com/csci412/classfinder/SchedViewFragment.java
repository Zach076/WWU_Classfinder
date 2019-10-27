package com.csci412.classfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SchedViewFragment extends Fragment {

    public class MatrixItem {
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

    public static final String ARG_ITEM_ID = "name";
    public CustomItems.ScheduleItem Sched;

    public static SimpleItemRecyclerViewAdapter classAdapt;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Sched = CustomItems.SCHEDULE_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.schedule_detail, container, false);

        CustomItems.avail.clear();

        //button logic is through xml and SchedViewActivity

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.item_list);
        assert recyclerView != null;
        classAdapt = new SimpleItemRecyclerViewAdapter(rootView, Sched.classes);
        recyclerView.setAdapter(classAdapt);
        classAdapt.notifyDataSetChanged();

        TextView tv = (TextView)rootView.findViewById(R.id.item_detail);
        tv.setText(Sched.name);

        int columns=6;
        int rows=12;

        GridView grid=(GridView)rootView.findViewById(R.id.grid);
        grid.setNumColumns(columns);

        List<String> irregularTimes = new ArrayList<>();
        //parsing start and end times
        String startTime;
        String endTime;
        for (Course course : Sched.classes) {
            for (int i = 0; i < course.times.size(); i++) {
                if (!course.times.get(i).equals("")) {
                    startTime = course.times.get(i);
                    startTime = startTime.split(" ")[1];
                    endTime = startTime.split("-")[1];
                    startTime = startTime.split("-")[0];
                    if (startTime.charAt(0) == '0') {
                        startTime = startTime.substring(1);
                    }
                /*
                //If we dont want rows showing times ending at X:50
                if(endTime.charAt(3) == '5') {
                    endTime = endTime.substring(0,3)+"00";
                }
                */
                    if (endTime.charAt(0) == '0') {
                        endTime = endTime.substring(1);
                    }

                    //checking for irregular start times
                    int length = startTime.length();
                    if (startTime.toCharArray()[length - 1] != '0' || startTime.toCharArray()[length - 2] != '0') {
                        int count = 0;
                        for (String time : irregularTimes) {
                            if (startTime.equals(time)) {
                                count++;
                            }
                        }
                        if (count == 0) {
                            irregularTimes.add(startTime);
                            rows++;
                        }
                    }

                    //checking for irregular end times
                    length = endTime.length();
                    if (endTime.toCharArray()[length - 1] != '0' || endTime.toCharArray()[length - 2] != '0') {
                        int count = 0;
                        for (String time : irregularTimes) {
                            if (endTime.equals(time)) {
                                count++;
                            }
                        }
                        if (count == 0) {
                            irregularTimes.add(endTime);
                            rows++;
                        }
                    }
                }
            }
        }

        //create adapter and it's respective list
        List<MatrixItem> matrixList=new ArrayList<>();
        MatrixAdapter matrixAdapter = new MatrixAdapter(getContext(), matrixList);

        //populate days
        matrixList.add(new MatrixItem(0,0,null));
        matrixList.add(new MatrixItem(0,1,"Monday"));
        matrixList.add(new MatrixItem(0,2,"Tuesday"));
        matrixList.add(new MatrixItem(0,3,"Wednesday"));
        matrixList.add(new MatrixItem(0,4,"Thursday"));
        matrixList.add(new MatrixItem(0,5,"Friday"));

        int currentTime = 7;
        String[] timeSplit;

        boolean specialTime = false;
        String theTime = null;
        int theIndex = 0;
        String days;
        boolean skip = false;

        //populate times
        for (int i=1;i<rows;i++)
        {
            skip = false;
            for (int index=0; index < irregularTimes.size(); index++) {
                timeSplit = irregularTimes.get(index).split(":");
                if(Integer.parseInt(timeSplit[0]) == currentTime - 1) {
                    if(theTime != null && Integer.parseInt(theTime.split(":")[0]) == currentTime - 1 ) {
                        if(Integer.parseInt(theTime.split(":")[1]) > Integer.parseInt(timeSplit[1])) {
                            theTime = irregularTimes.get(index);
                            theIndex = index;
                            specialTime = true;
                        }
                    } else {
                        theTime = irregularTimes.get(index);
                        theIndex = index;
                        specialTime = true;
                    }
                }
            }
            if(specialTime){
                matrixList.add(new MatrixItem(i,0,theTime));
                irregularTimes.remove(theIndex);
                specialTime = false;
                theTime = null;
            } else if(currentTime == 13) {
                currentTime = currentTime % 12;
                skip = true;
            } else {
                matrixList.add(new MatrixItem(i,0,Integer.toString(currentTime) + ":00"));
                currentTime++;
            }
            if(!skip) {
                for (int j = 1; j < columns; j++) {
                    matrixList.add(new MatrixItem(i, j, null));
                }
            }
        }

        //fill timetable with courses
        for (Course course : Sched.classes) {
            int timeIndex = columns;
            for (int i = 0; i < course.times.size(); i++) {
                if (!course.times.get(i).equals("")) {
                    startTime = course.times.get(i);
                    timeIndex = columns;
                    days = startTime.split(" ")[0];
                    startTime = startTime.split(" ")[1];
                    endTime = startTime.split("-")[1];
                    startTime = startTime.split("-")[0];
                    if (startTime.charAt(0) == '0') {
                        startTime = startTime.substring(1);
                    }
                /*
                //If we dont want rows showing times ending at X:50
                if(endTime.charAt(3) == '5') {
                    endTime = endTime.substring(0,3)+"00";
                }
                */
                    if (endTime.charAt(0) == '0') {
                        endTime = endTime.substring(1);
                    }
                    while (!startTime.equals(matrixList.get(timeIndex).text) && timeIndex < matrixList.size()) {
                        timeIndex = timeIndex + columns;
                    }
                    while (!endTime.equals(matrixList.get(timeIndex - columns).text) && timeIndex < matrixList.size()) {
                        int x = 0;
                        while (x < days.length()) {
                            switch (days.charAt(x)) {
                                case 'M':
                                    if (matrixList.get(timeIndex + 1).text != null) {
                                        matrixList.get(timeIndex + 1).color = 1;
                                    }
                                    matrixList.get(timeIndex + 1).text = course.course;
                                    break;
                                case 'T':
                                    if (matrixList.get(timeIndex + 2).text != null) {
                                        matrixList.get(timeIndex + 2).color = 1;
                                    }
                                    matrixList.get(timeIndex + 2).text = course.course;
                                    break;
                                case 'W':
                                    if (matrixList.get(timeIndex + 3).text != null) {
                                        matrixList.get(timeIndex + 3).color = 1;
                                    }
                                    matrixList.get(timeIndex + 3).text = course.course;
                                    break;
                                case 'R':
                                    if (matrixList.get(timeIndex + 4).text != null) {
                                        matrixList.get(timeIndex + 4).color = 1;
                                    }
                                    matrixList.get(timeIndex + 4).text = course.course;
                                    break;
                                case 'F':
                                    if (matrixList.get(timeIndex + 5).text != null) {
                                        matrixList.get(timeIndex + 5).color = 1;
                                    }
                                    matrixList.get(timeIndex + 5).text = course.course;
                                    break;
                            }
                            x++;
                        }
                        timeIndex = timeIndex + columns;
                    }
                }
            }
        }

        grid.setNumColumns(columns);
        grid.setAdapter(matrixAdapter);
        matrixAdapter.notifyDataSetChanged();

        return rootView;
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SchedViewFragment.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Course> mValues;
        private final View rootView;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course item = (Course) view.getTag();
                mValues.remove(item);
                notifyDataSetChanged();
            }
        };

        SimpleItemRecyclerViewAdapter(View parent,
                                      List<Course> items) {
            mValues = items;
            rootView = parent;
        }

        @Override
        public SchedViewFragment.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schedule_list_content, parent, false);
            return new SchedViewFragment.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SchedViewFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).course);
            holder.mContentView.setText(mValues.get(position).crn);
            if(CustomItems.avail.size() > 0) {
                holder.mAvailView.setText(CustomItems.avail.get(position).avail);
            }
            Button Delete = holder.itemView.findViewById(R.id.classDelete);
            Delete.setVisibility(View.VISIBLE);
            Delete.setTag(mValues.get(position));
            Delete.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mAvailView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mAvailView = (TextView) view.findViewById(R.id.avail);
            }
        }
    }
}
