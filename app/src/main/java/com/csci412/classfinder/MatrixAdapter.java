package com.csci412.classfinder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class MatrixAdapter extends BaseAdapter {
    Context context;
    List<MatrixUtils.MatrixItem> matrixList;

    public MatrixAdapter(Context context, List<MatrixUtils.MatrixItem> matrixList) {
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
        View v=View.inflate(context,R.layout.griditem,null);
        return v;
    }
}
