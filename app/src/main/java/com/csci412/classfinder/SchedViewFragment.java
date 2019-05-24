package com.csci412.classfinder;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = CustomItems.SCHEDULE_MAP.get(getArguments().getString(ARG_ITEM_ID));

            /*
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Schedules");
            }
            */
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.schedule_detail, container, false);

        CustomItems.ScheduleItem Sched = SchedulesActivity.selectedSchedule;

        TextView tv = (TextView)rootView.findViewById(R.id.item_detail);
        tv.setText(Sched.name);

        // GET THE MATRIX DIMENSIONS
        int columns=6;
        int rows=12;

        // INITIALISE YOUR GRID
        GridView grid=(GridView)rootView.findViewById(R.id.grid);
        grid.setNumColumns(columns);

        List<String> irregularTimes = new ArrayList<>();

        String startTime;
        for (Course course : Sched.classes) {
            startTime = course.times.get(0);
            int length = startTime.length();
            if(startTime.toCharArray()[length-1] != 0 || startTime.toCharArray()[length-2] != 0) {
                int count = 0;
                for (String time : irregularTimes) {
                    if(startTime.equals(time)) {
                        count++;
                    }
                }
                if(count == 0) {
                    irregularTimes.add(startTime);
                    rows++;
                }
            }
        }

        // CREATE A LIST OF MATRIX OBJECT
        List<CustomItems.MatrixItem> matrixList=new ArrayList<>();

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

        // ADD SOME CONTENTS TO EACH ITEM
        for (int i=1;i<rows;i++)
        {
            for (int index=0; index < irregularTimes.size(); index++) {
                timeSplit = irregularTimes.get(index).split(":");
                if(Integer.parseInt(timeSplit[0].trim()) == currentTime - 1) {
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

        for (Course course : Sched.classes) {
            int timeIndex = columns;
            startTime = course.times.get(0);
            while(!startTime.equals(matrixList.get(timeIndex))) {
                timeIndex = timeIndex + columns;
            }
            int x = 0;
            while(x < course.dates.length()) {
                switch(course.dates.charAt(x)) {
                    case 'M':
                        matrixList.get(timeIndex + 1).text = course.course;
                        break;
                    case 'T':
                        matrixList.get(timeIndex + 2).text = course.course;
                        break;
                    case 'W':
                        matrixList.get(timeIndex + 3).text = course.course;
                        break;
                    case 'R':
                        matrixList.get(timeIndex + 4).text = course.course;
                        break;
                    case 'F':
                        matrixList.get(timeIndex + 5).text = course.course;
                        break;
                }
                x++;
            }
        }

        grid.setNumColumns(columns);
        MatrixAdapter matrixAdapter = new MatrixAdapter(getContext(), matrixList);
        grid.setAdapter(matrixAdapter);
        matrixAdapter.notifyDataSetChanged();

        Button deleteBtn = rootView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(view -> {
            CustomItems.removeSchedule(Sched);
            tv.setTextColor(getResources().getColor(R.color.red));
            tv.setText("deleted");
        });

        return rootView;
    }
}
