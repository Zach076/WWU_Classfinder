package com.csci412.classfinder;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    private String sel_crn = "";
    public String term = "";
    public String sel_gur = "";
    public String sel_attr = "";
    public String sel_site = "";
    public String sel_subj = "";
    public String sel_inst = "";
    public String sel_day = "";
    public String sel_crse = "";
    public String begin_hh = "";
    public String begin_mi = "";
    public String end_hh = "";
    public String end_mi = "";
    public String sel_cdts = "";

    public List<Pair<String, String>> getFormData(){
        List<Pair<String, String>> formData = new ArrayList<>();
        formData.add(new Pair<>("sel_crn", sel_crn));
        formData.add(new Pair<>("term", term));
        formData.add(new Pair<>("sel_gur", sel_gur));
        formData.add(new Pair<>("sel_attr", sel_attr));
        formData.add(new Pair<>("sel_site", sel_site));
        formData.add(new Pair<>("sel_subj", sel_subj));
        formData.add(new Pair<>("sel_inst", sel_inst));
        formData.add(new Pair<>("sel_day", sel_day));
        formData.add(new Pair<>("sel_crse", sel_crse));
        formData.add(new Pair<>("begin_hh", begin_hh));
        formData.add(new Pair<>("begin_mi", begin_mi));
        formData.add(new Pair<>("end_hh", end_hh));
        formData.add(new Pair<>("end_mi", end_mi));
        formData.add(new Pair<>("sel_cdts", sel_cdts));
        return formData;
    }

    @Override
    public boolean equals(Object obj) {
        Filter fil = (Filter) obj;

        if(!sel_crn.equals(fil.sel_crn))
            return false;
        if(!term.equals(fil.term))
            return false;
        if(!sel_gur.equals(fil.sel_gur))
            return false;
        if(!sel_attr.equals(fil.sel_attr))
            return false;
        if(!sel_site.equals(fil.sel_site))
            return false;
        if(!sel_subj.equals(fil.sel_subj))
            return false;
        if(!sel_inst.equals(fil.sel_inst))
            return false;
        if(!sel_crse.equals(fil.sel_crse))
            return false;
        if(!begin_hh.equals(fil.begin_hh))
            return false;
        if(!begin_mi.equals(fil.begin_mi))
            return false;
        if(!end_hh.equals(fil.end_hh))
            return false;
        if(!end_mi.equals(fil.end_mi))
            return false;
        if(!sel_cdts.equals(fil.sel_cdts))
            return false;

        return true;
    }
}
