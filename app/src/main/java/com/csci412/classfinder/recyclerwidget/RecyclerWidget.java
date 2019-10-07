package com.csci412.classfinder.recyclerwidget;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.csci412.classfinder.Course;
import com.csci412.classfinder.R;

import java.util.ArrayList;

public class RecyclerWidget {

    private RecyclerView rv;
    private RecyclerAdapter adapter;
    private ArrayList<Course> courses;

    public RecyclerWidget(View parent, ArrayList<Course> courses) {
        this.courses = courses;
        this.rv = parent.findViewById(R.id.course_recycler_view);
        init();
    }

    //init the layout manager and adapter
    private void init() {
        // Setting layout manager
        LinearLayoutManager lManager = new LinearLayoutManager(rv.getContext());
        rv.setLayoutManager(lManager);

        // Setting Adapter
        adapter = new RecyclerAdapter(courses);
        rv.setAdapter(adapter);
    }

    public void updateClasses(ArrayList<Course> courses){
        this.courses = courses;
        adapter.courses = courses;
        adapter.notifyDataSetChanged();
        rv.scrollTo(0, 0);
    }
}
