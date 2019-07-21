package com.csci412.classfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class SchedViewActivity extends AppCompatActivity {

    public SchedViewFragment fragment;

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
                System.out.println("delete");
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
                    CustomItems.ScheduleItem item = new CustomItems.ScheduleItem(input.getText().toString());
                    item.classes = fragment.Sched.classes;
                    CustomItems.SCHEDULES.add(item);
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
        System.out.println("check");
        for (Course c : fragment.Sched.classes) {
            Filter f = CustomItems.getFilter(c);
            CustomItems.getAvail a = new CustomItems.getAvail();
            a.crn = c.crn;
            a.formData = f.getFormData();
            a.execute();
        }
        view.findViewById(R.id.checkBtn).setVisibility(View.GONE);
    }
}
