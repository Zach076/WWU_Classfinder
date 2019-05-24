package com.csci412.classfinder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.csci412.classfinder.animatedbottombar.BottomBar;
import com.csci412.classfinder.animatedbottombar.Item;
import com.csci412.classfinder.classviewwidget.ClassViewWidget;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

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

    private ClassViewWidget classList;
    private BottomBar bottomView;
    private HashMap<String, List<Course>> classes;

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
    private HashMap<String, String> term;
    private HashMap<String, String> otherAttributes;
    private HashMap<String, String> subject;
    private HashMap<String, String> gurAttributes;
    private HashMap<String, String> siteAttributes;
    private HashMap<String, String> Instructor;
    private HashMap<String, String> defaultValues;

    //things that are selected in the menu attributes
    private ArrayList<String> termSelected;
    private ArrayList<String> otherAttributesSelected;
    private ArrayList<String> subjectSelected;
    private ArrayList<String> gurAttributesSelected;
    private ArrayList<String> siteAttributesSelected;
    private ArrayList<String> InstructorSelected;
    private ArrayList<String> startHourSelected;
    private ArrayList<String> endHourSelected;
    private ArrayList<String> creditHourSelected;

    Filter activeFilter;

    //content views
    private View filterView;
    private View clsView;
    private View scheView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //intitalize all the menu options
        new getMenuAttributes().execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get content views
        filterView = findViewById(R.id.filter_view);
        clsView = findViewById(R.id.classes_view);
        scheView = findViewById(R.id.schedule_view);

        //set references to menu items
        getMenuReferences();

        //setup up nav bar
        setupBar();
        //set up class view
        setUpClasses();
    }

    private void getMenuReferences() {
        termButton = findViewById(R.id.termButton);
        otherAttributesButton = findViewById(R.id.otherAttributesButton);
        subjectButton = findViewById(R.id.subjectButton);
        gurAttributesButton = findViewById(R.id.GURattributesButton);
        siteAttributesButton = findViewById(R.id.siteAttributesButton);
        InstructorButton = findViewById(R.id.instructorButton);
        startHourButton = findViewById(R.id.startHour);
        endHourButton = findViewById(R.id.endHour);
        creditHourButton = findViewById(R.id.creditHoursButton);

        //references to menu check boxes
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.Tue);
        wed = findViewById(R.id.Wed);
        thu = findViewById(R.id.Thu);
        fri = findViewById(R.id.Fri);
        sat = findViewById(R.id.Sat);
        sun = findViewById(R.id.Sun);
        openSections = findViewById(R.id.openSectionsCheck);

        //reference to text view
        noInternet = findViewById(R.id.noInternet);

        //reference to edit text
        courseNumber = findViewById(R.id.editCourseNumber);

        //references to menu radio buttons
        startAM = findViewById(R.id.startAM);
        startPM = findViewById(R.id.startPM);
        endAM = findViewById(R.id.endAM);
        endPM = findViewById(R.id.endPM);
    }

    private void setupBar(){
        //animated bottom bar
        //views for send activities
        bottomView = findViewById(R.id.bottomView);

        //set colors
        bottomView.setIndicatorColor(getResources().getColor(R.color.deparmentHighlight));
        bottomView.setActiveColor(getResources().getColor(R.color.blue));

        //set listener
        bottomView.setupListener((int oldPos, int newPos) -> {
            //do nothing or refresh depending on page
            if (oldPos == newPos) {
                if (newPos == 1) {
                    if(clsView.findViewById(R.id.progressBar).getVisibility() == View.VISIBLE)
                        return;
                    updateClasses(getFilters(), true);
                }
                return;
            }

            //get direction for animation
            int dir = getOpenDir(oldPos, newPos);

            //open selected page
            //todo handle any special needs when navigating to a page like loading all classes based on filters
            switch (newPos) {
                case 0:
                    show(filterView, dir);
                    break;
                case 1:
                    if(clsView.findViewById(R.id.progressBar).getVisibility() == View.INVISIBLE) {
                        updateClasses(getFilters(), false);
                    }

                    RecyclerView rv = clsView.findViewById(R.id.course_recycler_view);
                    View labels = clsView.findViewById(R.id.labels);

                    float height = labels.getMeasuredHeight();
                    float transY = labels.getTranslationY();
                    rv.setPadding(0, (int) height, 0, 0);
                    rv.setClipToPadding(false);

                    rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                            float newY = labels.getTranslationY() - dy;

                            newY = Math.max(newY, -(height - Utilities.dpToPx(1)));
                            newY = Math.min(newY, 0);

                            labels.setTranslationY(newY);

                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });

                    show(clsView, dir);
                    break;
                case 2:
                    show(scheView, dir);
                    break;
            }

            //close old page
            //todo handle any special needs when navigating away from a page
            switch (oldPos) {
                case 0:
                    close(filterView, -dir);
                    break;
                case 1:
                    RecyclerView rv = clsView.findViewById(R.id.course_recycler_view);
                    rv.clearOnScrollListeners();
                    close(clsView, -dir);
                    break;
                case 2:
                    close(scheView, -dir);
                    break;
            }
        }).addItem(new Item("Filter"));

        //add items to nav bar
        bottomView.addItem(new Item("Classes"));
        bottomView.addItem(new Item("Schedule"));

        //build nav bar
        bottomView.build(0);

        //set up page change on drag
        clsView.setOnDragListener((view, event) -> {
            bottomView.changePosition(2);
            return true;
        });
    }

    private void setUpClasses(){
        classList = new ClassViewWidget(findViewById(R.id.course_recycler_layout), new ArrayList<>());
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
     /*   outState.putBoolean("created",true);
        outState.putInt("termLength",termSelected.size());
        for(int i = 0; i < termSelected.size(); i++){
            outState.putString("t" + i,termSelected.get(i));
        }
        outState.putInt("gurAttributesLength",gurAttributesSelected.size());
        for(int i = 0; i < gurAttributesSelected.size(); i++){
            outState.putString("ga" + i,gurAttributesSelected.get(i));
        }
        outState.putInt("otherAttributesLength",otherAttributesSelected.size());
        for(int i = 0; i < otherAttributesSelected.size(); i++){
            outState.putString("oa" + i,otherAttributesSelected.get(i));
        }
        outState.putInt("subjectLength",subjectSelected.size());
        for(int i = 0; i < subjectSelected.size(); i++){
            outState.putString("s" + i,subjectSelected.get(i));
        }
        outState.putInt("siteAttributesLength",siteAttributesSelected.size());
        for(int i = 0; i < siteAttributesSelected.size(); i++){
            outState.putString("sa" + i,siteAttributesSelected.get(i));
        }
        outState.putInt("InstructorLength",InstructorSelected.size());
        for(int i = 0; i < InstructorSelected.size(); i++){
            outState.putString("i" + i,InstructorSelected.get(i));
        }*/
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState){
        super.onRestoreInstanceState(savedState);
     /*   termSelected = new ArrayList<>();
        int termLength = savedState.getInt("termLength");
        for(int i = 0; i < termLength; i++){
            termSelected.add(savedState.getString("t" + i));
        }
        gurAttributesSelected = new ArrayList<>();
        int gurAttributesLength = savedState.getInt("gurAttributesLength");
        for(int i = 0; i < gurAttributesLength; i++){
            gurAttributesSelected.add(savedState.getString("ga" + i));
        }
        siteAttributesSelected = new ArrayList<>();
        int siteAttributesLength = savedState.getInt("siteAttributesLength");
        for(int i = 0; i < siteAttributesLength; i++){
            siteAttributesSelected.add(savedState.getString("sa" + i));
        }
        subjectSelected = new ArrayList<>();
        int subjectLength = savedState.getInt("subjectLength");
        for(int i = 0; i < subjectLength; i++){
            subjectSelected.add(savedState.getString("s" + i));
        }
        otherAttributesSelected = new ArrayList<>();
        int otherAttributesLength = savedState.getInt("otherAttributesLength");
        for(int i = 0; i < otherAttributesLength; i++){
            otherAttributesSelected.add(savedState.getString("oa" + i));
        }
        InstructorSelected = new ArrayList<>();
        int InstructorLength = savedState.getInt("InstructorLength");
        for(int i = 0; i < InstructorLength; i++){
            InstructorSelected.add(savedState.getString("i" + i));
        }*/
    }





    //get direction for animation
    private int getOpenDir(int oldPos, int newPos){
        if(oldPos < newPos)
            return 1;
        else
            return -1;
    }

    //closes page with animation
    private void close(View view, int dir){
        view.setTranslationX(0);
        view.setAlpha(1.0f);
        view.animate()
            .setDuration(200)
            .translationX(dir*view.getWidth()/2f)
            .alpha(0.0f)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
    }

    //shows page with animation
    private void show(View view, int dir){
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(dir*view.getWidth()/2f);
        view.setAlpha(0.0f);
        view.animate()
            .setDuration(200)
            .translationX(0)
            .alpha(1.0f)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.VISIBLE);
                    view.setTranslationX(0);
                    view.setAlpha(1.0f);
                }
            });
    }


    private void updateClasses(Filter filter, boolean force){

        if(force || activeFilter == null || !activeFilter.equals(filter)) {
            //actually gets and parses classes on a background thread using the filters provided
            //when complete the classes field will be populated with classes from classfinder
            GetClasses getClasses = new GetClasses();
            getClasses.formData = filter.getFormData();
            getClasses.execute();

            TextView tv = clsView.findViewById(R.id.time);
            tv.setText("Valid as of: " + DateFormat.getTimeInstance().format(new Date()));

            clsView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            activeFilter = filter;
        }
    }

    public void callMenuList(String[] content, int length, ArrayList<String> isSelected,int requestCode,String defaultItem, boolean oneSelectMode, int sortType){
        Intent intent = new Intent(this, menuListActivity.class);
        if(sortType == ALPHABET){
            Arrays.sort(content);
        }else if(sortType == YEAR){
            Arrays.sort(content, new Comparator<String>() {

                @Override
                public int compare(String s1, String s2) {
                    //return ORDER.indexOf(o1) -  ORDER.indexOf(o2) ;
                    return Integer.parseInt(s2.split(" ")[1]) - Integer.parseInt(s1.split(" ")[1]);
                }
            });
        }

        for(int i = 0; i < length; i++) {
            intent.putExtra("0" + i, content[i]);
            if(isSelected.contains(content[i])){
                intent.putExtra(content[i],"s");
            }
        }
        intent.putExtra("length",length);
        intent.putExtra("default",defaultItem);
        intent.putExtra("defaultSel",isSelected.contains(defaultItem));
        intent.putExtra("oneSelectMode",oneSelectMode);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);

        // The returned result data is identified by requestCode.
        // The request code is specified in startActivityForResult(intent, REQUEST_CODE); method.
        int length = dataIntent.getIntExtra("length",0);
        ArrayList<String> isSelected = null;
        switch (requestCode)
        {
            case TERM:
                isSelected = termSelected;
                updateSelected(isSelected,length, dataIntent,termButton);
                break;
            case OTHERATTRIBUTES:
                isSelected = otherAttributesSelected;
                updateSelected(isSelected,length, dataIntent, otherAttributesButton);
                break;
            case SUBJECT:
                isSelected = subjectSelected;
                updateSelected(isSelected,length, dataIntent, subjectButton);
                break;
            case GURATTRIBUTES:
                isSelected = gurAttributesSelected;
                updateSelected(isSelected,length, dataIntent,gurAttributesButton);
                break;
            case SITEATTRIBUTES:
                isSelected = siteAttributesSelected;
                updateSelected(isSelected,length, dataIntent, siteAttributesButton);
                break;
            case INSTRUCTOR:
                isSelected = InstructorSelected;
                updateSelected(isSelected,length, dataIntent, InstructorButton);
                break;
            case STARTHOUR:
                isSelected = startHourSelected;
                updateSelected(isSelected,length, dataIntent, startHourButton);
                break;
            case ENDHOUR:
                isSelected = endHourSelected;
                updateSelected(isSelected,length, dataIntent, endHourButton);
                break;
            case CREDITS:
                isSelected = creditHourSelected;
                updateSelected(isSelected,length, dataIntent, creditHourButton);
                break;
        }

    }

    private void updateSelected(ArrayList<String> isSelected, int length, Intent dataIntent, Button button) {
        if(isSelected != null) {
            isSelected.clear();
            for (int i = 0; i < length; i++) {
                String name = dataIntent.getStringExtra("" + i);
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
    }
    private void setButtonText(Button button, String text){
        if(text.length() > 15) {
            button.setText(text.substring(0,15) + "...");
        }else{
            button.setText(text);
        }
    }

    public void termButton(View view) {
        Set<String> keys = term.keySet();
        keys.remove(defaultValues.get("" + 0));
        callMenuList(keys.toArray(new String[keys.size()]),term.size(), termSelected,TERM, defaultValues.get("" + 0),true,YEAR);
    }

    public void GURattributesButton(View view) {
        Set<String> keys = gurAttributes.keySet();
        keys.remove(defaultValues.get("" + 1));
        callMenuList(keys.toArray(new String[keys.size()]),gurAttributes.size(),gurAttributesSelected,GURATTRIBUTES, defaultValues.get("" + 1),true,ALPHABET);
    }

    public void otherAttributesButton(View view) {
        Set<String> keys = otherAttributes.keySet();
        keys.remove(defaultValues.get("" + 2));
        callMenuList(keys.toArray(new String[keys.size()]),otherAttributes.size(), otherAttributesSelected,OTHERATTRIBUTES, defaultValues.get("" + 2),true,ALPHABET);
    }

    public void siteAttributesButton(View view) {
        Set<String> keys = siteAttributes.keySet();
        keys.remove(defaultValues.get("" + 3));
        callMenuList(keys.toArray(new String[keys.size()]),siteAttributes.size(),siteAttributesSelected ,SITEATTRIBUTES, defaultValues.get("" + 3),true,ALPHABET);
    }

    public void subjectButton(View view) {
        Set<String> keys = subject.keySet();
        keys.remove(defaultValues.get("" + 4));
        callMenuList(keys.toArray(new String[keys.size()]),subject.size(), subjectSelected,SUBJECT, defaultValues.get("" + 4),false,ALPHABET);
    }

    public void instructorButton(View view) {
        Set<String> keys = Instructor.keySet();
        keys.remove(defaultValues.get("" + 5));
        callMenuList(keys.toArray(new String[keys.size()]),Instructor.size(), InstructorSelected,INSTRUCTOR, defaultValues.get("" + 5),true,ALPHABET);
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
        mon.setChecked(true);
        tue.setChecked(true);
        wed.setChecked(true);
        thu.setChecked(true);
        fri.setChecked(true);
        sat.setChecked(true);
        sun.setChecked(true);
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

    public void search(View view) {
        bottomView.changePosition(1);
    }

    private Filter getFilters(){
        Filter filter = new Filter();
        filter.term = term.get(termSelected.get(0));
        filter.sel_gur = gurAttributes.get(gurAttributesSelected.get(0));
        filter.sel_attr = otherAttributes.get(otherAttributesSelected.get(0));
        filter.sel_site = siteAttributes.get(siteAttributesSelected.get(0));
        filter.sel_subj = subject.get(subjectSelected.get(0));
        filter.sel_inst = Instructor.get(InstructorSelected.get(0));
        filter.sel_crse = courseNumber.getText().toString();
        if(startPM.isChecked()){
            filter.begin_mi = "P";
        }else {
            filter.begin_mi = "A";
        }
        if(endPM.isChecked()){
            filter.end_mi = "P";
        }else {
            filter.end_mi = "A";
        }
        String startHour = startHourSelected.get(0);
        if(startHour.equals("All")) {
            filter.begin_hh = "0";
        }else{
            filter.begin_hh = startHour;
        }
        String endHour = endHourSelected.get(0);
        if(startHour.equals("All")) {
            filter.end_hh = "0";
        }else{
            filter.end_hh = endHour;
        }
        String creditHour = creditHourSelected.get(0);
        if(creditHour.equals("All")) {
            filter.sel_cdts = "%25";
        }else{
            filter.sel_cdts = creditHour;
        }
        return filter;
    }

    public void startHourButton(View view) {
        String[] hours = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        callMenuList(hours,hours.length, startHourSelected,STARTHOUR,"All",true,NONE);
    }

    public void endHourButton(View view) {
        String[] hours = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        callMenuList(hours,hours.length, endHourSelected,ENDHOUR, "All",true,NONE);
    }

    public void creditHoursButton(View view) {
        String[] credits = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"};
        callMenuList(credits, credits.length,creditHourSelected,CREDITS, "All",true,NONE);
    }

    //example async class for getting classes from classfinder
    private class GetClasses extends AsyncTask<String, Void, HashMap<String, List<Course>>> {

        List<Pair<String, String>> formData;

        @Override
        protected HashMap<String, List<Course>> doInBackground(String... unused) {
            return Utilities.getClasses(formData);
        }

        @Override
        protected void onPostExecute(HashMap<String, List<Course>> result) {
            classes = result;
            ArrayList<Course> courses = new ArrayList<>();
            for(List<Course> list : result.values()){
                courses.addAll(list);
            }
            classList.updateClasses(courses);
            clsView.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            ((RecyclerView)clsView.findViewById(R.id.course_recycler_view)).scrollToPosition(0);
            clsView.findViewById(R.id.labels).setTranslationY(0);
        }
    }

    private class getMenuAttributes extends AsyncTask<List<Pair<String, String>>, Void, List<HashMap<String, String>> >{

        @Override
        protected List<HashMap<String, String>> doInBackground(List<Pair<String, String>>... list) {
            return Utilities.getMenuAttributes();
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            if(result == null){
                noInternet.setVisibility(View.VISIBLE);
                System.out.println("no internet");
            }else {
                term = result.get(0);
                gurAttributes = result.get(1);
                otherAttributes = result.get(2);
                siteAttributes = result.get(3);
                subject = result.get(4);
                Instructor = result.get(5);
                defaultValues = result.get(6);
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
                filterView.setVisibility(View.VISIBLE);
            }
        }
    }
}
