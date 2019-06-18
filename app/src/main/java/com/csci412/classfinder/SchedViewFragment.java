package com.csci412.classfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SchedViewFragment extends Fragment {

    public static final String ARG_ITEM_ID = "name";
    private CustomItems.ScheduleItem mItem;
    public CustomItems.ScheduleItem Sched;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = CustomItems.SCHEDULE_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.schedule_detail, container, false);

        Sched = CustomItems.selectedSchedule;

        Button deleteBtn =(Button) rootView.findViewById(R.id.deleteBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("delete");
                getActivity().onBackPressed();
                CustomItems.removeSchedule(Sched);
            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.item_list);
        assert recyclerView != null;
        SchedViewFragment.SimpleItemRecyclerViewAdapter classAdapt = new SchedViewFragment.SimpleItemRecyclerViewAdapter(rootView, Sched.classes);
        recyclerView.setAdapter(classAdapt);
        classAdapt.notifyDataSetChanged();

        TextView tv = (TextView)rootView.findViewById(R.id.item_detail);
        tv.setText(Sched.name);

        // GET THE MATRIX DIMENSIONS
        int columns=6;
        int rows=12;

        // INITIALISE YOUR GRID
        GridView grid=(GridView)rootView.findViewById(R.id.grid);
        grid.setNumColumns(columns);

        List<String> irregularTimes = new ArrayList<>();

        //parsing start and end times
        String startTime;
        String endTime;
        for (Course course : Sched.classes) {
            for (int i = 0; i < course.times.size(); i++) {
                startTime = course.times.get(i);
                startTime = startTime.split(" ")[1];
                endTime = startTime.split("-")[1];
                startTime = startTime.split("-")[0];
                if(startTime.charAt(0) == '0') {
                    startTime = startTime.substring(1);
                }
                if(endTime.charAt(3) == '5') {
                    endTime = endTime.substring(0,3)+"00";
                }
                if(endTime.charAt(0) == '0') {
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

        //create adapter and it's respective list
        List<CustomItems.MatrixItem> matrixList=new ArrayList<>();
        MatrixAdapter matrixAdapter = new MatrixAdapter(getContext(), matrixList);

        //populate days
        matrixList.add(new CustomItems.MatrixItem(0,0,null));
        matrixList.add(new CustomItems.MatrixItem(0,1,"Monday"));
        matrixList.add(new CustomItems.MatrixItem(0,2,"Tuesday"));
        matrixList.add(new CustomItems.MatrixItem(0,3,"Wednesday"));
        matrixList.add(new CustomItems.MatrixItem(0,4,"Thursday"));
        matrixList.add(new CustomItems.MatrixItem(0,5,"Friday"));

        int currentTime = 7;
        String[] timeSplit;
        boolean specialTime = false;
        String theTime = null;
        int theIndex = 0;
        String days;

        //populate times
        for (int i=1;i<rows;i++)
        {
            for (int index=0; index < irregularTimes.size(); index++) {
                timeSplit = irregularTimes.get(index).split(":");
                if(Integer.parseInt(timeSplit[0]) == currentTime - 1) {
                    theTime = irregularTimes.get(index);
                    theIndex = index;
                    specialTime = true;
                }
            }
            if(specialTime){
                matrixList.add(new CustomItems.MatrixItem(i,0,theTime));
                irregularTimes.remove(theIndex);
                specialTime = false;
            } else {
                matrixList.add(new CustomItems.MatrixItem(i,0,Integer.toString(currentTime) + ":00"));
                currentTime = currentTime % 12;
                currentTime++;
            }
            for (int j=1;j<columns;j++)
            {
                matrixList.add(new CustomItems.MatrixItem(i,j,null));
            }
        }

        //fill timetable with courses
        for (Course course : Sched.classes) {
            int timeIndex = columns;
            for (int i = 0; i < course.times.size(); i++) {
                startTime = course.times.get(i);
                timeIndex = columns;
                days = startTime.split(" ")[0];
                startTime = startTime.split(" ")[1];
                endTime = startTime.split("-")[1];
                startTime = startTime.split("-")[0];
                if (startTime.charAt(0) == '0') {
                    startTime = startTime.substring(1);
                }
                if(endTime.charAt(3) == '5') {
                    endTime = endTime.substring(0,3)+"00";
                }
                if (endTime.charAt(0) == '0') {
                    endTime = endTime.substring(1);
                }
                while (!startTime.equals(matrixList.get(timeIndex).text) && timeIndex < matrixList.size()) {
                    timeIndex = timeIndex + columns;
                }
                timeIndex = timeIndex - columns;
                while (!endTime.equals(matrixList.get(timeIndex).text) && timeIndex < matrixList.size()) {
                    timeIndex = timeIndex + columns;
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
                }
            }
        }

        grid.setNumColumns(columns);
        //MatrixAdapter matrixAdapter = new MatrixAdapter(getContext(), matrixList);
        grid.setAdapter(matrixAdapter);
        matrixAdapter.notifyDataSetChanged();

        return rootView;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SchedViewFragment.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final View mParentActivity;
        private final List<Course> mValues;
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
            mParentActivity = parent;
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

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
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
