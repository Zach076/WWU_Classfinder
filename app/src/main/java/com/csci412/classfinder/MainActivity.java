package com.csci412.classfinder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.csci412.classfinder.animatedbottombar.BottomBar;
import com.csci412.classfinder.animatedbottombar.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    HashMap<String, List<Course>> classes;
    HashMap<String, String> term;
    HashMap<String, String> otherAttributes;
    HashMap<String, String> subject;
    HashMap<String, String> gurAttributes;
    HashMap<String, String> siteAttributes;
    HashMap<String, String> Instructor;


    //content views
    View filterView;
    View clsView;
    View scheView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //intitalize all the menu options
        new getMenuAttributes().execute();
        //todo make the main thread wait on the thread above
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get content views
        filterView = findViewById(R.id.filter_view);
        filterView.setVisibility(View.VISIBLE);
        clsView = findViewById(R.id.classes_view);
        scheView = findViewById(R.id.schedule_view);

        //example getting all classes
        //required fields to get classes from classfinder
        //unknown behavior if out of order
        final List<Pair<String, String>> formData = new ArrayList<>();
        formData.add(new Pair<>("sel_crn", ""));
        formData.add(new Pair<>("term", "201930"));
        formData.add(new Pair<>("sel_gur", "All"));
        formData.add(new Pair<>("sel_attr", "All"));
        formData.add(new Pair<>("sel_site", "All"));
        formData.add(new Pair<>("sel_subj", "CSCI"));
        formData.add(new Pair<>("sel_inst", "ANY"));
        formData.add(new Pair<>("sel_crse", ""));
        formData.add(new Pair<>("begin_hh", "0"));
        formData.add(new Pair<>("begin_mi", "A"));
        formData.add(new Pair<>("end_hh", "0"));
        formData.add(new Pair<>("end_mi", "A"));
        formData.add(new Pair<>("sel_cdts", "%25"));
        //actually gets and parses classes on a background thread using the filters provided
        //when complete the classes field will be populated with classes from

        //commented out so classfinder isnt called unnecessarily
        //new GetClasses().execute(formData);

        //done

        //setup up nav bar
        setupBar();
    }

    private void setupBar(){
        //animated bottom bar
        //views for send activities
        BottomBar bottomView = findViewById(R.id.bottomView);

        //set colors
        bottomView.setIndicatorColor(Color.BLUE);
        bottomView.setActiveColor(Color.BLUE);

        //set listener
        bottomView.setupListener((int oldPos, int newPos) -> {
            //do nothing or refresh depending on page
            if(oldPos == newPos) {
                if(newPos == 1){
                    //todo refresh classes
                }
                return;
            }

            //get direction for animation
            int dir = getOpenDir(oldPos, newPos);

            //open selected page
            //todo handle any special needs when navigating to a page like loading all classes based on filters
            switch (newPos) {
                case 0:
                    show(filterView, dir);
                    break;
                case 1:
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
                    close(filterView, -dir);
                    break;
                case 1:
                    close(clsView, -dir);
                    break;
                case 2:
                    close(scheView, -dir);
                    break;
            }
        });

        //add items to nav bar
        bottomView.addItem(new Item("Filter"));
        bottomView.addItem(new Item("Classes"));
        bottomView.addItem(new Item("Schedule"));

        //build nav bar
        bottomView.build(0);
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

    //example async class for getting classes from classfinder
    private class GetClasses extends AsyncTask<List<Pair<String, String>>, Void, HashMap<String, List<Course>>> {

        @Override
        protected HashMap<String, List<Course>> doInBackground(List<Pair<String, String>>... list) {
            return Utilities.getClasses(list[0]);
        }

        @Override
        protected void onPostExecute(HashMap<String, List<Course>> result) {
            classes = result;
        }
    }

    private class getMenuAttributes extends AsyncTask<List<Pair<String, String>>, Void, List<HashMap<String, String>> >{

        @Override
        protected List<HashMap<String, String>> doInBackground(List<Pair<String, String>>... list) {
            return Utilities.getMenuAttributes();
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            term = result.get(0);
            gurAttributes = result.get(1);
            otherAttributes = result.get(2);
            siteAttributes = result.get(3);
            subject = result.get(4);
            Instructor = result.get(5);
        }
    }
}
