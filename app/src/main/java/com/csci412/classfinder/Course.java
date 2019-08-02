package com.csci412.classfinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {

    //class info
    public String course = "";
    public String title = "";
    public String dept = "";
    public String crn = "";
    public List<String> location = new ArrayList<>();
    public String instructor = "";

    //class details
    public String dates = "";
    public String chrgs = "";
    public String credits = "";
    public String attrs = "";
    public List<String> times = new ArrayList<>();
    public String prereq = "";
    public String restrictions = "";
    public List<String> additional = new ArrayList<>();
    public String term = "";

    //class space
    public boolean waitlist = false;
    public String cap = "";
    public String enrl = "";
    public String avail = "";

}
