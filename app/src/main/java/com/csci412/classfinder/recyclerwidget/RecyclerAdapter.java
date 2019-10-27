package com.csci412.classfinder.recyclerwidget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csci412.classfinder.Course;
import com.csci412.classfinder.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerItemViewHolder> {

    //need the items here
    //as well as the id of the layout for each item to be used in the recycler view
    ArrayList<Course> courses;

    RecyclerAdapter(ArrayList<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //inflate the item layout and return in in a new holder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_layout, parent, false);
        return new RecyclerItemViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerItemViewHolder holder, int position) {
        //finish setting up item and holder
        Course course = courses.get(position);
        View courseView = holder.itemView;

        ((TextView)courseView.findViewById(R.id.course_number)).setText(course.course);

        ((TextView)courseView.findViewById(R.id.course_attributes)).setText(course.attrs);

        ((TextView)courseView.findViewById(R.id.restrictions)).setText(course.restrictions);

        ((TextView)courseView.findViewById(R.id.prerequisites)).setText(course.prereq);

        ((TextView)courseView.findViewById(R.id.course_title)).setText(course.title);

        StringBuilder times = new StringBuilder();
        for(String time : course.times){
            times.append(time);
            times.append("\n");
        }
        if(times.length() > 0)
            times.deleteCharAt(times.length()-1);
        ((TextView)courseView.findViewById(R.id.course_times)).setText(times.toString());

        StringBuilder details = new StringBuilder();
        for(String detail : course.additional){
            details.append(detail);
            details.append("\n");
        }
        if(details.length() > 0)
            details.deleteCharAt(details.length()-1);
        ((TextView)courseView.findViewById(R.id.additional_details)).setText(details.toString());

        ((TextView)courseView.findViewById(R.id.course_crn)).setText(course.crn);

        ((TextView)courseView.findViewById(R.id.course_credits)).setText(course.credits);

        StringBuilder locs = new StringBuilder();
        for(String loc : course.location) {
            locs.append(loc);
            locs.append("\n");
        }
        if(locs.length() > 0)
            locs.deleteCharAt(locs.length()-1);
        ((TextView) courseView.findViewById(R.id.location)).setText(locs.toString());

        ((TextView)courseView.findViewById(R.id.dates)).setText(course.dates);

        ((TextView)courseView.findViewById(R.id.instructor)).setText(course.instructor);

        ((TextView)courseView.findViewById(R.id.available)).setText(course.avail);

        ((TextView)courseView.findViewById(R.id.charges)).setText(course.chrgs);
    }
}