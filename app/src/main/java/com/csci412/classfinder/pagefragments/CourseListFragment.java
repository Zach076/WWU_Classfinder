package com.csci412.classfinder.pagefragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csci412.classfinder.Course;
import com.csci412.classfinder.Filter;
import com.csci412.classfinder.R;
import com.csci412.classfinder.Utilities;
import com.csci412.classfinder.classviewwidget.ClassViewWidget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class CourseListFragment extends Fragment {

    private ClassViewWidget classList;
    Filter activeFilter;

    private OnFragmentInteractionListener mListener;

    public CourseListFragment() {
        // Required empty public constructor
    }

    public static CourseListFragment newInstance(AppCompatActivity parent) {
        CourseListFragment fragment = new CourseListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View crsView = inflater.inflate(R.layout.courses_layout, container);

        classList = new ClassViewWidget(crsView.findViewById(R.id.course_recycler_layout), new ArrayList<>());
        classList.refresh.setOnRefreshListener(() -> {
            updateClasses(activeFilter, true, null);
        });

        ArrayList<Course> courses;
        try {
            courses = (ArrayList<Course>) savedInstanceState.getSerializable("courses");
        }catch (Exception e){
            courses = null;
        }

        if(courses != null){
            classList.updateClasses(courses);
        } else {
            classList.updateClasses(new ArrayList<>());
        }

        crsView.setVisibility(View.INVISIBLE);
        return crsView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("courses", classList.getCourses());
    }


    //todo on destroy view

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateClasses(Filter filter, boolean force, InterstitialAd mInterstitialAd){
        if(force || activeFilter == null || !activeFilter.equals(filter)) {
            classList.refresh.setRefreshing(true);

            if(mInterstitialAd != null) {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            //actually gets and parses classes on a background thread using the filters provided
            //when complete the classes field will be populated with classes from classfinder
            GetClasses getClasses = new GetClasses();
            getClasses.formData = filter.getFormData();
            getClasses.execute();

            TextView tv = getView().findViewById(R.id.time);
            tv.setText("Valid as of: " + DateFormat.getTimeInstance().format(new Date()));

            activeFilter = filter;
        }
    }

    public boolean isRefreshing(){
        return classList.refresh.isRefreshing();
    }

    //example async class for getting classes from classfinder
    private class GetClasses extends AsyncTask<String, Void, TreeMap<String, List<Course>>> {

        List<Pair<String, String>> formData;

        @Override
        protected TreeMap<String, List<Course>> doInBackground(String... unused) {
            return Utilities.getClasses(formData);
        }

        @Override
        protected void onPostExecute(TreeMap<String, List<Course>> result) {
            if(result != null) {
                getView().findViewById(R.id.no_classes).setVisibility(View.INVISIBLE);
                //todo sort hashmap by key
                ArrayList<Course> courses = new ArrayList<>();
                for (List<Course> list : result.values()) {
                    courses.addAll(list);
                }
                classList.updateClasses(courses);
            } else {
                getView().findViewById(R.id.no_classes).setVisibility(View.VISIBLE);
                classList.updateClasses(new ArrayList<>());
            }
            classList.refresh.setRefreshing(false);
            //((RecyclerView)getView().findViewById(R.id.course_recycler_view)).scrollToPosition(0);
            //getView().findViewById(R.id.labels).setTranslationY(0);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
