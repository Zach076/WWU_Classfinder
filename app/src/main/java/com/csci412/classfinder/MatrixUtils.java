package com.csci412.classfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixUtils {

    /*
    public static final List<MatrixItem> MATRIX = new ArrayList<MatrixItem>();

    public static final Map<Integer, MatrixItem> ITEM_MAP = new HashMap<Integer, MatrixItem>();

    public static void fillMatrix(int row, int col) {
        if (MATRIX.size() == 0) {
            MatrixItem item;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    item = new MatrixItem(i,j, null);
                    addItem(item);
                }
            }
        }
    }

    private static void addItem(MatrixItem item) {
        MATRIX.add(item);
        ITEM_MAP.put(item.id, item);
    }
    */

    public static class MatrixItem {
        public int id;
        public int row;
        public int col;
        public String text;

        public MatrixItem(int row, int col, String text) {
            this.id = (row*10) + col;
            this.row = row;
            this.col = col;
            this.text = text;
        }
    }
}

