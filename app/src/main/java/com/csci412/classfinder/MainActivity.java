package com.csci412.classfinder;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        //example getting all classes
        //unknown behavior if out of order
        ////////////////////////testing////////////////////
        final List<Pair<String, String>> formData = new ArrayList<>();
        formData.add(new Pair<>("sel_crn", ""));
        formData.add(new Pair<>("term", "201930"));
        formData.add(new Pair<>("sel_gur", "All"));
        formData.add(new Pair<>("sel_attr", "All"));
        formData.add(new Pair<>("sel_site", "All"));
        formData.add(new Pair<>("sel_subj", "All"));
        formData.add(new Pair<>("sel_inst", "ANY"));
        formData.add(new Pair<>("sel_crse", ""));
        formData.add(new Pair<>("begin_hh", "0"));
        formData.add(new Pair<>("begin_mi", "A"));
        formData.add(new Pair<>("end_hh", "0"));
        formData.add(new Pair<>("end_mi", "A"));
        formData.add(new Pair<>("sel_cdts", "%25"));
        GetClasses classGetter = new GetClasses();
        classGetter.execute(formData);
        ///////////////////////////////////////////////////
    }

    private static class GetClasses extends AsyncTask<List<Pair<String, String>>, Void, Integer> {

        @Override
        protected Integer doInBackground(List<Pair<String, String>>... list) {
            return Utilities.getClasses(list[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            //currently returning line count of response for debugging
            System.out.println(result);
        }
    }

    @Override
    public void onClick(View v) {
        Resources re = getResources();
        Bundle bundle;

        switch (v.getId()) {
            case R.id.button1:
                //stuff
                break;
            case R.id.button2:
                //stuff
                break;
            case R.id.button3:
                //stuff
                break;
        }
    }

}
