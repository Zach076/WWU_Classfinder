package com.csci412.classfinder;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CustomItems {

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

    public static final List<ScheduleItem> SCHEDULES = new ArrayList<>();

    public static class ScheduleItem {
        public List<Course> classes;
        public String name;

        public ScheduleItem(String name) {
            this.name = name;
            this.classes = new ArrayList<>();
        }
    }

    public static void addSchedule(String name) {
        SCHEDULES.add(new ScheduleItem(name));
    }

}

