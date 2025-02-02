package com.csci412.classfinder.classviewwidget;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csci412.classfinder.Course;
import com.csci412.classfinder.MainActivity;
import com.csci412.classfinder.R;
import com.csci412.classfinder.Utilities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ClassViewAdapter extends RecyclerView.Adapter<ClassViewHolder> {

    //need the items here
    //as well as the id of the layout for each item to be used in the recycler view
    ArrayList<Course> courses;
    ViewGroup parent;

    ClassViewAdapter(ArrayList<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //inflate the item layout and return in in a new holder
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_layout, parent, false);
        return new ClassViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }



    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        //finish setting up item and holder
        Course course = courses.get(position);
        View courseView = holder.itemView;

        int pxPad = Utilities.dpToPx(8);
        courseView.findViewById(R.id.tableLayout).setPadding(pxPad, pxPad, pxPad, pxPad);
        int light = courseView.getResources().getColor(R.color.white);
        int dark = courseView.getResources().getColor(R.color.classHighlight);

        TextView tv = courseView.findViewById(R.id.course_number);
        tv.setText(course.course);
        tv.setBackgroundColor(light);

        tv = courseView.findViewById(R.id.course_attributes);
        tv.setText(course.attrs);
        tv.setBackgroundColor(dark);

        tv = courseView.findViewById(R.id.restrictions);
        tv.setText(course.restrictions);
        tv.setBackgroundColor(light);

        tv = courseView.findViewById(R.id.prerequisites);
        tv.setText(course.prereq);
        tv.setBackgroundColor(dark);

        tv = courseView.findViewById(R.id.course_title);
        tv.setText(course.title);
        tv.setBackgroundColor(dark);

        StringBuilder times = new StringBuilder();
        for(String time : course.times){
            times.append(time);
            times.append("\n");
        }
        if(times.length() > 0)
            times.deleteCharAt(times.length()-1);
        tv = courseView.findViewById(R.id.course_times);
        tv.setText(times.toString());
        tv.setBackgroundColor(dark);

        StringBuilder details = new StringBuilder();
        for(String detail : course.additional){
            details.append(detail);
            details.append("\n");
        }
        if(details.length() > 0)
            details.deleteCharAt(details.length()-1);
        tv = courseView.findViewById(R.id.additional_details);
        tv.setText(details.toString());
        tv.setBackgroundColor(dark);

        tv = courseView.findViewById(R.id.course_crn);
        tv.setText(course.crn);
        tv.setBackgroundColor(light);

        tv = courseView.findViewById(R.id.course_credits);
        tv.setText(course.credits);
        tv.setBackgroundColor(dark);

        StringBuilder locs = new StringBuilder();
        for(String loc : course.location) {
            locs.append(loc);
            locs.append("\n");
        }
        if(locs.length() > 0)
            locs.deleteCharAt(locs.length()-1);
        tv = courseView.findViewById(R.id.location);
        tv.setText(locs.toString());
        tv.setBackgroundColor(light);

        tv = courseView.findViewById(R.id.dates);
        tv.setText(course.dates);
        tv.setBackgroundColor(light);

        tv = courseView.findViewById(R.id.instructor);
        tv.setText(course.instructor);
        tv.setBackgroundColor(light);

        tv = courseView.findViewById(R.id.available);
        tv.setText(course.avail);
        tv.setBackgroundColor(dark);
        if(Integer.parseInt(course.avail) == 0)
            tv.setTextColor(courseView.getResources().getColor(R.color.red));
        else
            tv.setTextColor(courseView.getResources().getColor(R.color.green));

        tv = courseView.findViewById(R.id.charges);
        tv.setText(course.chrgs);
        tv.setBackgroundColor(light);

        tv = courseView.findViewById(R.id.dept_label);
        if(position == 0 || !course.dept.equals(courses.get(position - 1).dept)){
            tv.setText(course.dept);
            tv.setVisibility(View.VISIBLE);
        } else
            tv.setVisibility(View.GONE);

        courseView.findViewById(R.id.tableLayout).setOnLongClickListener((view) -> {
            MainActivity.bottomView.changePosition(2);
            Intent intent = new Intent();
            intent.putExtra("course", course);
            ClipData dragData = ClipData.newIntent("data", intent);
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
            view.startDrag(dragData, shadow, null, 0);
            return true;
        });

        courseView.findViewById(R.id.tableLayout).setOnClickListener((view) -> {
            GetClassInfo task = new GetClassInfo();
            task.formData = new Pair<>(course.term, course.course.replaceAll(" ", ""));
            task.contextRef = new WeakReference<>(parent.getContext());
            task.execute("");
        });
    }

    public static class GetClassInfo extends AsyncTask<String, Void, Pair<String, String>> {

        WeakReference<Context> contextRef;
        Pair<String, String> formData;

        @Override
        protected Pair<String, String> doInBackground(String... unused) {
            return Utilities.getClassInfo(formData.first, formData.second);
        }

        @Override
        protected void onPostExecute(Pair<String, String> result) {
            new AlertDialog.Builder(contextRef.get())
                    .setTitle(result.first)
                    .setMessage(result.second)
                    .setNegativeButton(android.R.string.ok, null)
                    .show();
            contextRef.clear();
        }
    }
}