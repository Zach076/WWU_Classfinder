package com.csci412.classfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        // GET THE MATRIX DIMENSIONS
        int columns=6;
        int rows=12;

        // INITIALISE YOUR GRID
        GridView grid=(GridView)findViewById(R.id.grid);
        grid.setNumColumns(columns);

        // CREATE A LIST OF MATRIX OBJECT
        List<MatrixUtils.MatrixItem> matrixList=new ArrayList<>();

        // ADD SOME CONTENTS TO EACH ITEM
        for (int i=0;i<rows;i++)
        {
            for (int j=0;j<columns;j++)
            {
                matrixList.add(new MatrixUtils.MatrixItem(i,j,null));
            }
        }

        // CREATE AN ADAPTER  (MATRIX ADAPTER)
        MatrixAdapter adapter=new MatrixAdapter(getApplicationContext(),matrixList);

        // ATTACH THE ADAPTER TO GRID
        grid.setAdapter(adapter);
    }
}
