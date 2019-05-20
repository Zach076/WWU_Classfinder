package com.csci412.classfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SchedViewFragment extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        CustomItems.ScheduleItem Sched = getParent().selectedSchedule;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        // GET THE MATRIX DIMENSIONS
        int columns=6;
        int rows=12;

        // INITIALISE YOUR GRID
        GridView grid=(GridView)findViewById(R.id.grid);
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
        String theTime;
        int theIndex;

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


    }
}
