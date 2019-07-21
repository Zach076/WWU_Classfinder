package com.csci412.classfinder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.csci412.classfinder.animatedbottombar.BottomBar;
import com.csci412.classfinder.animatedbottombar.Item;
import com.csci412.classfinder.classviewwidget.ClassViewWidget;
import com.csci412.classfinder.pagefragments.CourseListFragment;

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

public class MainActivity extends AppCompatActivity implements CourseListFragment.OnFragmentInteractionListener {

    private ClassViewWidget classList;
    public static BottomBar bottomView;

    Filter activeFilter;
    Filter f;

    //content views
    private View clsView;
    private CourseListFragment crseFrag;
    private View scheView;
    private filter_layout filterLayout;

    //editText to name schedules
    private String currEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //intitalize all the menu options

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //get content views
        filterLayout =  (filter_layout) getFragmentManager().findFragmentById(R.id.filter_view);
        clsView = findViewById(R.id.classes_view);
        crseFrag = (CourseListFragment) getFragmentManager().findFragmentById(R.id.classes_view);

        scheView = findViewById(R.id.schedule_view);
        List<CustomItems.ScheduleItem> schedules= CustomItems.getFromSharedPrefs(getApplicationContext());
        for (CustomItems.ScheduleItem item : schedules) {
            CustomItems.addSchedule(item);
        }

        //set references to menu items

        //setup up nav bar
        if(savedInstanceState != null)
            setupBar(savedInstanceState.getInt("page", 0));
        else
            setupBar(0);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onStop() {
        CustomItems.saveToSharedPrefs(getApplicationContext());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        CustomItems.saveToSharedPrefs(getApplicationContext());
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", bottomView.getCurrentPage());
    }

    //todo(nick) finish fling
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                System.out.println("show +1");
                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                System.out.println("show -1");
                return false; // Left to right
            }
            return false;
        }
    }


    private void setupBar(int start){
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
                    if(crseFrag.isRefreshing())
                        return;
                    Filter filter = getFilters();
                    if(filter != null) {
                        crseFrag.updateClasses(filter, true);
                    }
                }
                return;
            }

            //get direction for animation
            int dir = getOpenDir(oldPos, newPos);

            //open selected page
            //handle any special needs when navigating to a page like loading all classes based on filters
            switch (newPos) {
                case 0:
                    show(filterLayout.getView(), dir);
                    break;
                case 1:
                    if(!crseFrag.isRefreshing()) {
                        Filter filter = getFilters();
                        if(filter != null) {
                            crseFrag.updateClasses(filter, false);
                        }
                    }

                    RecyclerView rv = crseFrag.getView().findViewById(R.id.course_recycler_view);
                    View labels = crseFrag.getView().findViewById(R.id.labels);

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

                    show(crseFrag.getView(), dir);
                    break;
                case 2:
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
                    assert recyclerView != null;
                    CustomItems.SimpleItemRecyclerViewAdapter adapt = new CustomItems.SimpleItemRecyclerViewAdapter(this, CustomItems.SCHEDULES);
                    recyclerView.setAdapter(adapt);
                    CustomItems.rva = adapt;

                    Button newSchedBtn = (Button) findViewById(R.id.newScheduleButton);
                    newSchedBtn.setOnClickListener(view -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Enter new schedule name");

                        final EditText input = new EditText(MainActivity.this);
                        builder.setView(input);

                        builder.setPositiveButton("Add", (dialogInterface, i) -> {
                            if(input.getText().toString() != null && CustomItems.SCHEDULE_MAP.get(input.getText().toString()) == null) {
                                CustomItems.addSchedule(input.getText().toString());
                            }
                        });

                        builder.setNeutralButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

                        builder.show();
                    });

                    show(scheView, dir);
                    break;
            }

            //close old page
            //handle any special needs when navigating away from a page
            switch (oldPos) {
                case 0:
                    close(filterLayout.getView(), -dir);
                    break;
                case 1:
                    RecyclerView rv = crseFrag.getView().findViewById(R.id.course_recycler_view);
                    rv.clearOnScrollListeners();
                    close(crseFrag.getView(), -dir);
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
        bottomView.build(start);
        switch(start){
            case 0:
                filterLayout.getView().setVisibility(View.VISIBLE);
                crseFrag.getView().setVisibility(View.INVISIBLE);
                scheView.setVisibility(View.INVISIBLE);
                break;
            case 1:
                filterLayout.getView().setVisibility(View.INVISIBLE);
                crseFrag.getView().setVisibility(View.VISIBLE);
                scheView.setVisibility(View.INVISIBLE);
                break;
            case 2:
                filterLayout.getView().setVisibility(View.INVISIBLE);
                crseFrag.getView().setVisibility(View.INVISIBLE);
                scheView.setVisibility(View.VISIBLE);
                break;
        }

    }

    //todo add on start and on stop with a bundle

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

    public void searchFilter(){
     bottomView.changePosition(1);
    }

    private Filter getFilters(){
        return filterLayout.getFilters();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        //possible communication between fragment needed?
    }
}
