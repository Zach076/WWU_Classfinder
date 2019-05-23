package com.csci412.classfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class SchedViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(SchedViewFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(SchedViewFragment.ARG_ITEM_ID));
            SchedViewFragment fragment = new SchedViewFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, SchedulesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
