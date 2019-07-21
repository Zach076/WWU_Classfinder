package com.csci412.classfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MatrixAdapter extends BaseAdapter {
    Context context;
    List<SchedViewFragment.MatrixItem> matrixList;

    public MatrixAdapter(Context context, List<SchedViewFragment.MatrixItem> matrixList) {
        this.context = context;
        this.matrixList = matrixList;
    }

    @Override
    public int getCount() {
        return matrixList.size();
    }

    @Override
    public Object getItem(int i) {
        return matrixList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final SchedViewFragment.MatrixItem matrixItem = matrixList.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.schedule_griditem, null);
        }

        final TextView textView = (TextView)view.findViewById(R.id.griditem);

        textView.setText(matrixItem.text);
        if(matrixItem.color == 1) {
            textView.setTextColor(context.getResources().getColor(R.color.red));
        }

        return view;
    }
}
