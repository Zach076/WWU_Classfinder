package com.csci412.classfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.util.Pair;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class filter_layout extends Fragment {

    //sort types
    private final static int NONE = 0;
    private final static int ALPHABET = 1;
    private final static int YEAR = 2;

    //intent request codes
    private final static int TERM = 1;
    private final static int OTHERATTRIBUTES = 2;
    private final static int SUBJECT = 3;
    private final static int GURATTRIBUTES = 4;
    private final static int SITEATTRIBUTES = 5;
    private final static int INSTRUCTOR = 6;
    private final static int STARTHOUR = 7;
    private final static int ENDHOUR = 8;
    private final static int CREDITS = 9;

    //references to menu buttons
    private Button termButton;
    private Button otherAttributesButton;
    private Button subjectButton;
    private Button gurAttributesButton;
    private Button siteAttributesButton;
    private Button InstructorButton;
    private Button startHourButton;
    private Button endHourButton;
    private Button creditHourButton;
    private Button searchButton;
    private Button resetButton;

    //references to menu check boxes
    private CheckBox mon;
    private CheckBox tue;
    private CheckBox wed;
    private CheckBox thu;
    private CheckBox fri;
    private CheckBox sat;
    private CheckBox sun;
    private CheckBox openSections;

    //reference to text view
    private TextView noInternet;

    //reference to edit text
    private EditText courseNumber;

    //references to menu radio buttons
    private RadioButton startAM;
    private RadioButton startPM;
    private RadioButton endAM;
    private RadioButton endPM;

    //menu attributes
    public static HashMap<String, String> term;
    public HashMap<String, String> otherAttributes;
    public HashMap<String, String> subject;
    public HashMap<String, String> gurAttributes;
    public HashMap<String, String> siteAttributes;
    public HashMap<String, String> Instructor;
    public HashMap<String, String> defaultValues;

    //things that are selected in the menu attributes
    public static ArrayList<String> termSelected;
    public ArrayList<String> otherAttributesSelected;
    public ArrayList<String> subjectSelected;
    public ArrayList<String> gurAttributesSelected;
    public ArrayList<String> siteAttributesSelected;
    public ArrayList<String> InstructorSelected;
    public ArrayList<String> startHourSelected;
    public ArrayList<String> endHourSelected;
    public ArrayList<String> creditHourSelected;

    View v;

    Bundle savedInstance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public filter_layout() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment filter_layout.
     */
    // TODO: Rename and change types and number of parameters
    public static filter_layout newInstance(View view) {
        filter_layout fragment = new filter_layout();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;
        new getMenuAttributes().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_layout, container, false);
    }



    public void updateButton(Button button,ArrayList<String> isSelected){
        int length = isSelected.size();
        if (length > 1) {
            button.setText(length + " Selected");
        } else if (length == 1) {
            String text = isSelected.get(0);
            setButtonText(button, text);
        } else {
            button.setText("none");
        }
    }

    public void getMenuReferences() {
        termButton = v.findViewById(R.id.termButton);
        filter_layout f = this;
        termButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termButtonOnClick();
            }
        });
        otherAttributesButton = v.findViewById(R.id.otherAttributesButton);
        otherAttributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otherAttributesButtonOnClick();
            }
        });
        subjectButton = v.findViewById(R.id.subjectButton);
        subjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subjectButtonOnClick();
            }
        });

        gurAttributesButton = v.findViewById(R.id.GURattributesButton);
        gurAttributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gurAttributesButtonOnClick();
            }
        });
        siteAttributesButton = v.findViewById(R.id.siteAttributesButton);
        siteAttributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                siteAttributesOnClick();
            }
        });
        InstructorButton = v.findViewById(R.id.instructorButton);
        InstructorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstructorOnClick();
            }
        });
        startHourButton = v.findViewById(R.id.startHour);
        startHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHourOnClick();
            }
        });
        endHourButton = v.findViewById(R.id.endHour);
        endHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endHourOnClick();
            }
        });
        creditHourButton = v.findViewById(R.id.creditHoursButton);
        creditHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creditHourOnClick();
            }
        });
        searchButton = v.findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).searchFilter();
            }
        });
        resetButton = v.findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilters(view);
            }
        });

        //references to menu check boxes
        mon = v.findViewById(R.id.mon);
        tue = v.findViewById(R.id.Tue);
        wed = v.findViewById(R.id.Wed);
        thu = v.findViewById(R.id.Thu);
        fri = v.findViewById(R.id.Fri);
        sat = v.findViewById(R.id.Sat);
        sun = v.findViewById(R.id.Sun);
        openSections = v.findViewById(R.id.openSectionsCheck);

        //reference to text view
        noInternet = v.findViewById(R.id.noInternet);

        //reference to edit text
        courseNumber = v.findViewById(R.id.editCourseNumber);

        //references to menu radio buttons
        startAM = v.findViewById(R.id.startAM);
        startPM = v.findViewById(R.id.startPM);
        endAM = v.findViewById(R.id.endAM);
        endPM = v.findViewById(R.id.endPM);
    }

    private void creditHourOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        String[] credits = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"};
        menuListDialogue alertDialog = menuListDialogue.newInstance("Credit Hours", credits, creditHourSelected, "All", true, false, fragment, CREDITS);
        alertDialog.show(fm, "fragment_alert");
    }

    private void endHourOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        String[] hours = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        menuListDialogue alertDialog = menuListDialogue.newInstance("End Hour", hours, endHourSelected, "All", true, false, fragment, ENDHOUR);
        alertDialog.show(fm, "fragment_alert");
    }

    private void startHourOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        String[] hours = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        menuListDialogue alertDialog = menuListDialogue.newInstance("Start Hour", hours, startHourSelected, "All", true, false, fragment, STARTHOUR);
        alertDialog.show(fm, "fragment_alert");
    }

    private void InstructorOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        //cloning so that removing the default does not effect the hash map
        Set<String> keys= ((HashMap<String, String>)(Instructor.clone())).keySet();
        keys.remove(defaultValues.get("" + 5));
        String[] names = keys.toArray(new String[keys.size()]);
        Arrays.sort(names);
        menuListDialogue alertDialog = menuListDialogue.newInstance("Instructor", names, InstructorSelected, defaultValues.get("" + 5), true, false, fragment, INSTRUCTOR);
        alertDialog.show(fm, "fragment_alert");
    }

    private void siteAttributesOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        //cloning so that removing the default does not effect the hash map
        Set<String> keys= ((HashMap<String, String>)(siteAttributes.clone())).keySet();
        keys.remove(defaultValues.get("" + 3));
        String[] names = keys.toArray(new String[keys.size()]);
        Arrays.sort(names);
        menuListDialogue alertDialog = menuListDialogue.newInstance("Site Attributes", names, siteAttributesSelected, defaultValues.get("" + 3), true, false, fragment, SITEATTRIBUTES);
        alertDialog.show(fm, "fragment_alert");
    }

    private void gurAttributesButtonOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        //cloning so that removing the default does not effect the hash map
        Set<String> keys= ((HashMap<String, String>)(gurAttributes.clone())).keySet();
        keys.remove(defaultValues.get("" + 1));
        String[] names = keys.toArray(new String[keys.size()]);
        Arrays.sort(names);
        menuListDialogue alertDialog = menuListDialogue.newInstance("GUR Attributes", names, gurAttributesSelected, defaultValues.get("" + 1), true, false, fragment, GURATTRIBUTES);
        alertDialog.show(fm, "fragment_alert");
    }

    private void subjectButtonOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        //cloning so that removing the default does not effect the hash map
        Set<String> keys= ((HashMap<String, String>)(subject.clone())).keySet();
        keys.remove(defaultValues.get("" + 4));
        String[] names = keys.toArray(new String[keys.size()]);

        Arrays.sort(names);
        menuListDialogue alertDialog = menuListDialogue.newInstance("Subject", names, subjectSelected, defaultValues.get("" + 4), false, true, fragment, SUBJECT);
        alertDialog.show(fm, "fragment_alert");
    }

    private void otherAttributesButtonOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        //cloning so that removing the default does not effect the hash map
        Set<String> keys= ((HashMap<String, String>)(otherAttributes.clone())).keySet();
        keys.remove(defaultValues.get("" + 2));
        String[] names = keys.toArray(new String[keys.size()]);
        Arrays.sort(names);
        menuListDialogue alertDialog = menuListDialogue.newInstance("Other Attributes", names, otherAttributesSelected, defaultValues.get("" + 2), true, false, fragment, OTHERATTRIBUTES);
        alertDialog.show(fm, "fragment_alert");
    }

    private void termButtonOnClick() {
        filter_layout fragment  = this;
        FragmentManager fm = getFragmentManager();
        //cloning so that removing the default does not effect the hash map
        Set<String> keys= ((HashMap<String, String>)(term.clone())).keySet();
        keys.remove(defaultValues.get("" + 0));
        String[] names = keys.toArray(new String[keys.size()]);
        //sort by year
        Arrays.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                //return ORDER.indexOf(o1) -  ORDER.indexOf(o2) ;
                return Integer.parseInt(s2.split(" ")[1]) - Integer.parseInt(s1.split(" ")[1]);
            }
        });
        menuListDialogue alertDialog = menuListDialogue.newInstance("Term", names, termSelected, defaultValues.get("" + 0), true, false, fragment, TERM);
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        try {
            outState.putBoolean("created", true);
            outState.putInt("termLength", termSelected.size());
            for (int i = 0; i < termSelected.size(); i++) {
                outState.putString("t" + i, termSelected.get(i));
            }
            outState.putInt("gurAttributesLength", gurAttributesSelected.size());
            for (int i = 0; i < gurAttributesSelected.size(); i++) {
                outState.putString("ga" + i, gurAttributesSelected.get(i));
            }
            outState.putInt("otherAttributesLength", otherAttributesSelected.size());
            for (int i = 0; i < otherAttributesSelected.size(); i++) {
                outState.putString("oa" + i, otherAttributesSelected.get(i));
            }
            outState.putInt("subjectLength", subjectSelected.size());
            for (int i = 0; i < subjectSelected.size(); i++) {
                outState.putString("s" + i, subjectSelected.get(i));
            }
            outState.putInt("siteAttributesLength", siteAttributesSelected.size());
            for (int i = 0; i < siteAttributesSelected.size(); i++) {
                outState.putString("sa" + i, siteAttributesSelected.get(i));
            }
            outState.putInt("InstructorLength", InstructorSelected.size());
            for (int i = 0; i < InstructorSelected.size(); i++) {
                outState.putString("i" + i, InstructorSelected.get(i));
            }
            outState.putInt("startLength", startHourSelected.size());
            for (int i = 0; i < startHourSelected.size(); i++) {
                outState.putString("sh" + i, startHourSelected.get(i));
            }
            outState.putInt("endLength", endHourSelected.size());
            for (int i = 0; i < endHourSelected.size(); i++) {
                outState.putString("eh" + i, endHourSelected.get(i));
            }
            outState.putInt("creditLength", creditHourSelected.size());
            for (int i = 0; i < creditHourSelected.size(); i++) {
                outState.putString("c" + i, creditHourSelected.get(i));
            }
        }catch (Exception e){
            Log.e("filter", "There was an error saving filter state.");
        }
    }



    public void onDialogDismiss(int requestCode, Bundle resultData) {
        switch (requestCode)
        {
            case TERM:
                updateSelected(termSelected,resultData.getInt("length"), resultData,termButton);
                break;
            case OTHERATTRIBUTES:
                updateSelected(otherAttributesSelected,resultData.getInt("length"), resultData, otherAttributesButton);
                break;
            case SUBJECT:
                updateSelected(subjectSelected,resultData.getInt("length"), resultData, subjectButton);
                break;
            case GURATTRIBUTES:
                updateSelected(gurAttributesSelected,resultData.getInt("length"), resultData,gurAttributesButton);
                break;
            case SITEATTRIBUTES:
                updateSelected(siteAttributesSelected,resultData.getInt("length"), resultData, siteAttributesButton);
                break;
            case INSTRUCTOR:
                updateSelected(InstructorSelected,resultData.getInt("length"), resultData, InstructorButton);
                break;
            case STARTHOUR:
                updateSelected(startHourSelected,resultData.getInt("length"), resultData, startHourButton);
                break;
            case ENDHOUR:
                updateSelected(endHourSelected,resultData.getInt("length"), resultData, endHourButton);
                break;
            case CREDITS:
                updateSelected(creditHourSelected,resultData.getInt("length"), resultData, creditHourButton);
                break;
        }

    }

    private void updateSelected(ArrayList<String> isSelected, int length, Bundle resultData, Button button) {
            isSelected.clear();
            for (int i = 0; i < length; i++) {
                String name = resultData.getString("" + i);
                isSelected.add(name);
            }
            if (length > 1) {
                button.setText(length + " Selected");
            } else if (length == 1) {
                String text = isSelected.get(0);
                setButtonText(button, text);
            } else {
                button.setText("none");
            }
    }

    private void setButtonText(Button button, String text){
        if(text.length() > 15) {
            button.setText(text.substring(0,15) + "...");
        }else{
            button.setText(text);
        }
    }

    public void resetFilters(View view) {
        termSelected.clear();
        otherAttributesSelected.clear();
        subjectSelected.clear();
        gurAttributesSelected.clear();
        siteAttributesSelected.clear();
        InstructorSelected.clear();
        startHourSelected.clear();
        endHourSelected.clear();
        creditHourSelected.clear();
        termSelected.add(defaultValues.get("" + 0));
        gurAttributesSelected.add(defaultValues.get("" + 1));
        otherAttributesSelected.add(defaultValues.get("" + 2));
        siteAttributesSelected.add(defaultValues.get("" + 3));
        subjectSelected.add(defaultValues.get("" + 4));
        InstructorSelected.add(defaultValues.get("" + 5));
        startHourSelected.add("All");
        endHourSelected.add("All");
        creditHourSelected.add("All");
        mon.setChecked(false);
        tue.setChecked(false);
        wed.setChecked(false);
        thu.setChecked(false);
        fri.setChecked(false);
        sat.setChecked(false);
        sun.setChecked(false);
        startAM.setChecked(false);
        startPM.setChecked(false);
        endPM.setChecked(false);
        endAM.setChecked(false);
        openSections.setChecked(false);
        courseNumber.setText("");
        setButtonText(termButton, defaultValues.get("" + 0));
        setButtonText(otherAttributesButton, defaultValues.get("" + 2));
        setButtonText(subjectButton, defaultValues.get("" + 4));
        setButtonText(gurAttributesButton, defaultValues.get("" + 1));
        setButtonText(siteAttributesButton, defaultValues.get("" + 3));
        setButtonText(InstructorButton, defaultValues.get("" + 5));
        startHourButton.setText("All");
        endHourButton.setText("All");
        creditHourButton.setText("All");
    }

    public String getSubjects(){
        String subjects = "";
        for(int i = 0; i < subjectSelected.size(); i++){
            if(i == 0){
                subjects = subject.get(subjectSelected.get(i));
            }else {
                subjects += " " + subject.get(subjectSelected.get(i));
            }
        }
        return subjects;
    }

    public String getDays(){
        String days = "";
        boolean first = true;
        if(mon.isChecked()){
            if(!first){
                days += " ";
            }else{
                first = false;
            }
            days += "m";
        }
        if(tue.isChecked()){
            if(!first){
                days += " ";
            }else{
                first = false;
            }
            days += "t";
        }
        if(wed.isChecked()){
            if(!first){
                days += " ";
            }else{
                first = false;
            }
            days += "w";
        }
        if(thu.isChecked()){
            if(!first){
                days += " ";
            }else{
                first = false;
            }
            days += "r";
        }
        if(fri.isChecked()){
            if(!first){
                days += " ";
            }else{
                first = false;
            }
            days += "f";
        }
        if(sat.isChecked()){
            if(!first){
                days += " ";
            }else{
                first = false;
            }
            days += "s";
        }
        if(sun.isChecked()){
            if(!first){
                days += " ";
            }else{
                first = false;
            }
            days += "u";
        }
        return days;
    }

    public Filter getFilters(){
        Filter filter = new Filter();
        try {
            filter.term = term.get(termSelected.get(0));
            filter.sel_gur = gurAttributes.get(gurAttributesSelected.get(0));
            filter.sel_attr = otherAttributes.get(otherAttributesSelected.get(0));
            filter.sel_site = siteAttributes.get(siteAttributesSelected.get(0));
            filter.sel_subj = getSubjects();
            filter.sel_inst = Instructor.get(InstructorSelected.get(0));
            filter.sel_crse = courseNumber.getText().toString();
            filter.sel_day = getDays();
            if (startPM.isChecked()) {
                filter.begin_mi = "P";
            } else {
                filter.begin_mi = "A";
            }
            if (endPM.isChecked()) {
                filter.end_mi = "P";
            } else {
                filter.end_mi = "A";
            }
            String startHour = startHourSelected.get(0);
            if (startHour.equals("All")) {
                filter.begin_hh = "0";
            } else {
                filter.begin_hh = startHour;
            }
            String endHour = endHourSelected.get(0);
            if (startHour.equals("All")) {
                filter.end_hh = "0";
            } else {
                filter.end_hh = endHour;
            }
            String creditHour = creditHourSelected.get(0);
            if (creditHour.equals("All")) {
                filter.sel_cdts = "%25";
            } else {
                filter.sel_cdts = creditHour;
            }
            if (openSections.isChecked()) {
                filter.sel_open = "Y";
            }
        }catch (Exception e){
            //filter is either loading or errored
            return null;
        }
        return filter;
    }


    private class getMenuAttributes extends AsyncTask<List<Pair<String, String>>, Void, List<HashMap<String, String>> > {

        @Override
        protected List<HashMap<String, String>> doInBackground(List<Pair<String, String>>... list) {
            return Utilities.getMenuAttributes();
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            if(result == null){
                noInternet.setVisibility(View.VISIBLE);
            }else {
                term = result.get(0);
                gurAttributes = result.get(1);
                otherAttributes = result.get(2);
                siteAttributes = result.get(3);
                subject = result.get(4);
                Instructor = result.get(5);
                defaultValues = result.get(6);
                if(savedInstance == null) {
                    termSelected = new ArrayList<>();
                    gurAttributesSelected = new ArrayList<>();
                    otherAttributesSelected = new ArrayList<>();
                    siteAttributesSelected = new ArrayList<>();
                    subjectSelected = new ArrayList<>();
                    InstructorSelected = new ArrayList<>();
                    startHourSelected = new ArrayList<>();
                    endHourSelected = new ArrayList<>();
                    creditHourSelected = new ArrayList<>();
                    termSelected.add(defaultValues.get("" + 0));
                    gurAttributesSelected.add(defaultValues.get("" + 1));
                    otherAttributesSelected.add(defaultValues.get("" + 2));
                    siteAttributesSelected.add(defaultValues.get("" + 3));
                    subjectSelected.add(defaultValues.get("" + 4));
                    InstructorSelected.add(defaultValues.get("" + 5));
                    startHourSelected.add("All");
                    endHourSelected.add("All");
                    creditHourSelected.add("All");
                }
                v = getView();
                getMenuReferences();
                if(savedInstance != null){
                    termSelected = new ArrayList<>();
                    int termLength = savedInstance.getInt("termLength");
                    for(int i = 0; i < termLength; i++){
                        termSelected.add(savedInstance.getString("t" + i));
                    }
                    updateButton(termButton,termSelected);
                    gurAttributesSelected = new ArrayList<>();
                    int gurAttributesLength = savedInstance.getInt("gurAttributesLength");
                    for(int i = 0; i < gurAttributesLength; i++){
                        gurAttributesSelected.add(savedInstance.getString("ga" + i));
                    }
                    updateButton(gurAttributesButton,gurAttributesSelected);
                    siteAttributesSelected = new ArrayList<>();
                    int siteAttributesLength = savedInstance.getInt("siteAttributesLength");
                    for(int i = 0; i < siteAttributesLength; i++){
                        siteAttributesSelected.add(savedInstance.getString("sa" + i));
                    }
                    updateButton(siteAttributesButton,siteAttributesSelected);
                    subjectSelected = new ArrayList<>();
                    int subjectLength = savedInstance.getInt("subjectLength");
                    for(int i = 0; i < subjectLength; i++){
                        subjectSelected.add(savedInstance.getString("s" + i));
                    }
                    updateButton(subjectButton,subjectSelected);
                    otherAttributesSelected = new ArrayList<>();
                    int otherAttributesLength = savedInstance.getInt("otherAttributesLength");
                    for(int i = 0; i < otherAttributesLength; i++){
                        otherAttributesSelected.add(savedInstance.getString("oa" + i));
                    }
                    updateButton(otherAttributesButton,otherAttributesSelected);
                    InstructorSelected = new ArrayList<>();
                    int InstructorLength = savedInstance.getInt("InstructorLength");
                    for(int i = 0; i < InstructorLength; i++){
                        InstructorSelected.add(savedInstance.getString("i" + i));
                    }
                    updateButton(InstructorButton,InstructorSelected);
                    startHourSelected = new ArrayList<>();
                    int startLength = savedInstance.getInt("startLength");
                    for(int i = 0; i < startLength; i++){
                        startHourSelected.add(savedInstance.getString("sh" + i));
                    }
                    updateButton(startHourButton,startHourSelected);
                    endHourSelected = new ArrayList<>();
                    int endLength = savedInstance.getInt("endLength");
                    for(int i = 0; i < endLength; i++){
                        endHourSelected.add(savedInstance.getString("eh" + i));
                    }
                    updateButton(endHourButton,endHourSelected);
                    creditHourSelected = new ArrayList<>();
                    int creditLength = savedInstance.getInt("creditLength");
                    for(int i = 0; i < creditLength; i++){
                        creditHourSelected.add(savedInstance.getString("c" + i));
                    }
                    updateButton(creditHourButton,creditHourSelected);
                }
                v.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                v.setVisibility(View.VISIBLE);
            }
            CustomItems.getTerm();
        }
    }

}
