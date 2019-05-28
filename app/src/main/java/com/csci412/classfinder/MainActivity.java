package com.csci412.classfinder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.csci412.classfinder.animatedbottombar.BottomBar;
import com.csci412.classfinder.animatedbottombar.Item;
import com.csci412.classfinder.classviewwidget.ClassViewWidget;

import org.w3c.dom.Text;

import java.io.FileReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    private ClassViewWidget classList;
    private BottomBar bottomView;

    Filter activeFilter;
    Filter f;

    //content views
    private View clsView;
    private View scheView;
    private filter_layout filterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //intitalize all the menu options

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //get content views
        filterLayout =  (filter_layout) getFragmentManager().findFragmentById(R.id.filter_view);
        clsView = findViewById(R.id.classes_view);
        scheView = findViewById(R.id.schedule_view);

        //set references to menu items

        //setup up nav bar
        setupBar();
        //set up class view
        setUpClasses();
    }


    private void setupBar(){
        //animated bottom bar
        //views for send activities
        bottomView = findViewById(R.id.bottomView);

        //set colors
        bottomView.setIndicatorColor(getResources().getColor(R.color.deparmentHighlight));
        bottomView.setActiveColor(getResources().getColor(R.color.blue));

        //set listener
        bottomView.setupListener((int oldPos, int newPos) -> {
            //do nothing or refresh depending on page
            if (oldPos == newPos) {
                if (newPos == 1) {
                    if(classList.refresh.isRefreshing())
                        return;
                    updateClasses(getFilters(), true);
                }
                return;
            }

            //get direction for animation
            int dir = getOpenDir(oldPos, newPos);

            //open selected page
            //todo handle any special needs when navigating to a page like loading all classes based on filters
            switch (newPos) {
                case 0:
                    show(filterLayout.getView(), dir);
                    break;
                case 1:
                    if(!classList.refresh.isRefreshing()) {
                        updateClasses(getFilters(), false);
                    }

                    RecyclerView rv = clsView.findViewById(R.id.course_recycler_view);
                    View labels = clsView.findViewById(R.id.labels);

                    float height = labels.getMeasuredHeight();
                    rv.setPadding(0, (int)(height-Math.abs(labels.getTranslationY())), 0, 0);

                    rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                            float newY = labels.getTranslationY() - dy;

                            newY = Math.max(newY, -(height - Utilities.dpToPx(1)));
                            newY = Math.min(newY, 0);

                            labels.setTranslationY(newY);
                            rv.setPadding(0, (int)(height-Math.abs(newY)), 0, 0);

                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });

                    show(clsView, dir);
                    break;
                case 2:
                    show(scheView, dir);
                    break;
            }

            //close old page
            //todo handle any special needs when navigating away from a page
            switch (oldPos) {
                case 0:
                    close(filterLayout.getView(), -dir);
                    break;
                case 1:
                    RecyclerView rv = clsView.findViewById(R.id.course_recycler_view);
                    rv.clearOnScrollListeners();
                    close(clsView, -dir);
                    break;
                case 2:
                    close(scheView, -dir);
                    break;
            }
        }).addItem(new Item("Filter"));

        //add items to nav bar
        bottomView.addItem(new Item("Classes"));
        bottomView.addItem(new Item("Schedule"));

        //build nav bar
        bottomView.build(0);

        //set up page change on drag
        clsView.setOnDragListener((view, event) -> {
            bottomView.changePosition(2);
            return true;
        });
    }

    private void setUpClasses(){
        classList = new ClassViewWidget(findViewById(R.id.course_recycler_layout), new ArrayList<>());
        classList.refresh.setOnRefreshListener(() -> {
            updateClasses(activeFilter, true);
        });
    }





    //get direction for animation
    private int getOpenDir(int oldPos, int newPos){
        if(oldPos < newPos)
            return 1;
        else
            return -1;
    }

    //closes page with animation
    private void close(View view, int dir){
        view.setTranslationX(0);
        view.setAlpha(1.0f);
        view.animate()
            .setDuration(200)
            .translationX(dir*view.getWidth()/2f)
            .alpha(0.0f)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
    }

    //shows page with animation
    private void show(View view, int dir){
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(dir*view.getWidth()/2f);
        view.setAlpha(0.0f);
        view.animate()
            .setDuration(200)
            .translationX(0)
            .alpha(1.0f)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.VISIBLE);
                    view.setTranslationX(0);
                    view.setAlpha(1.0f);
                }
            });
    }


    private void updateClasses(Filter filter, boolean force){

        if(force || activeFilter == null || !activeFilter.equals(filter)) {
            //actually gets and parses classes on a background thread using the filters provided
            //when complete the classes field will be populated with classes from classfinder
            GetClasses getClasses = new GetClasses();
            getClasses.formData = filter.getFormData();
            getClasses.execute();

            TextView tv = clsView.findViewById(R.id.time);
            tv.setText("Valid as of: " + DateFormat.getTimeInstance().format(new Date()));

            classList.refresh.setRefreshing(true);

            activeFilter = filter;
        }
    }

    public void returnFilter(Filter filter){
        f = filter;
        bottomView.changePosition(1);
    }

    private Filter getFilters(){
        return filterLayout.getFilters();
    }

    public void termButton(View view) {
        //filterLayout.termButton(view);
    }

    public void GURattributesButton(View view) {
        //filterLayout.GURattributesButton(view);
    }

    public void otherAttributesButton(View view) {
       // filterLayout.otherAttributesButton(view);
    }

    public void siteAttributesButton(View view) {
       // filterLayout.siteAttributesButton(view);
    }

    public void subjectButton(View view) {
      //  filterLayout.subjectButton(view);
    }

    public void instructorButton(View view) {
      //  filterLayout.instructorButton(view);
    }

    public void startHourButton(View view) {
      //  filterLayout.startHourButton(view);
    }

    public void endHourButton(View view) {
       // filterLayout.endHourButton(view);
    }

    public void creditHoursButton(View view) {
      //  filterLayout.creditHoursButton(view);
    }

    public void resetFilters(View view) {
      //  filterLayout.resetFilters(view);
    }

    public void search(View view) {
      ///  bottomView.changePosition(1);
    }


    //example async class for getting classes from classfinder
    private class GetClasses extends AsyncTask<String, Void, HashMap<String, List<Course>>> {

        List<Pair<String, String>> formData;

        @Override
        protected HashMap<String, List<Course>> doInBackground(String... unused) {
            return Utilities.getClasses(formData);
        }

        @Override
        protected void onPostExecute(HashMap<String, List<Course>> result) {
            if(result != null) {
                clsView.findViewById(R.id.no_classes).setVisibility(View.INVISIBLE);
                ArrayList<Course> courses = new ArrayList<>();
                for (List<Course> list : result.values()) {
                    courses.addAll(list);
                }
                classList.updateClasses(courses);
            } else {
                clsView.findViewById(R.id.no_classes).setVisibility(View.VISIBLE);
                classList.updateClasses(new ArrayList<>());
            }
            classList.refresh.setRefreshing(false);
            ((RecyclerView)clsView.findViewById(R.id.course_recycler_view)).scrollToPosition(0);
            clsView.findViewById(R.id.labels).setTranslationY(0);
        }
    }

}
