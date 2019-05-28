package com.csci412.classfinder.classviewwidget;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.View;

import com.csci412.classfinder.Course;
import com.csci412.classfinder.MainActivity;
import com.csci412.classfinder.R;
import com.csci412.classfinder.Utilities;

import java.util.ArrayList;

public class ClassViewWidget {

    private RecyclerView rv;
    public SwipeRefreshLayout refresh;
    private ClassViewAdapter adapter;
    private ArrayList<Course> courses;

    public ClassViewWidget(View parent, ArrayList<Course> courses) {
        this.courses = courses;
        this.rv = parent.findViewById(R.id.course_recycler_view);
        this.refresh = parent.findViewById(R.id.swipe_container);
        init();
    }

    //init the layout manager and adapter
    private void init() {
        // Setting layout manager
        LinearLayoutManager lManager = new LinearLayoutManager(rv.getContext());
        rv.setLayoutManager(lManager);

        // Setting Adapter
        adapter = new ClassViewAdapter(courses);
        rv.setAdapter(adapter);

        // Configure the refreshing colors
        refresh.setColorSchemeResources(R.color.deparmentHighlight);

        //setup for fling
        rv.setNestedScrollingEnabled(false);
    }

    public void updateClasses(ArrayList<Course> courses){
        this.courses = courses;
        adapter.courses = courses;
        adapter.notifyDataSetChanged();
        rv.scrollTo(0, 0);
    }

    public ArrayList<Course> getCourses(){
        return courses;
    }
}
