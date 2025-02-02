package com.csci412.classfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchedViewActivity extends AppCompatActivity {

    public SchedViewFragment fragment;
    public int press = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(SchedViewFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(SchedViewFragment.ARG_ITEM_ID));
            fragment = new SchedViewFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    public void deleteSche(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Are you sure you want to delete this schedule?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
                CustomItems.removeSchedule(fragment.Sched);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    public void cloneSche(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Enter new schedule name");

        String currEditText;

        final EditText input = new EditText(view.getContext());
        builder.setView(input);

        builder.setPositiveButton("Clone", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(input.getText().toString() != null && CustomItems.SCHEDULE_MAP.get(input.getText().toString()) == null) {
                    List<Course> classes = new ArrayList<>();
                    for(int j = 0; j < fragment.Sched.classes.size(); j++){
                     classes.add(new Course());
                    }
                    Collections.copy(classes,fragment.Sched.classes);
                    CustomItems.ScheduleItem item = new CustomItems.ScheduleItem(input.getText().toString(), classes);
                    CustomItems.addSchedule(item);
                    CustomItems.rva.notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    public void checkSche(View view) {
        if(press == 0) {
            press++;
            int size  = fragment.Sched.classes.size();
            for (Course c : fragment.Sched.classes) {
                Filter f = CustomItems.getFilter(c);
                CustomItems.getAvail a = new CustomItems.getAvail();
                a.size = size;
                a.crn = c.crn;
                a.formData = f.getFormData();
                a.execute();
            }
        } else {
            Toast.makeText(SchedViewActivity.this, "Checking availability uses many resources.\nPlease be mindful when using this button.", Toast.LENGTH_LONG).show();
            press = 0;
        }
        view.findViewById(R.id.checkBtn).setVisibility(View.GONE);
    }
}
