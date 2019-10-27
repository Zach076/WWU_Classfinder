package com.csci412.classfinder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.csci412.classfinder.animatedbottombar.BottomBar;
import com.csci412.classfinder.animatedbottombar.Item;
import com.csci412.classfinder.classviewwidget.ClassViewWidget;
import com.csci412.classfinder.pagefragments.CourseListFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CourseListFragment.OnFragmentInteractionListener {

    private ClassViewWidget classList;
    public static BottomBar bottomView;

    Filter activeFilter;
    Filter f;

    //content views
    private View clsView;
    private CourseListFragment crseFrag;
    private View scheView;
    private filter_layout filterLayout;

    //editText to name schedules
    private String currEditText;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //intitalize all the menu options

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //production mInterstitialAd.setAdUnitId("ca-app-pub-8447225644842981~8148660639");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });

        AdView mAdView = findViewById(R.id.bannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //get content views
        filterLayout =  (filter_layout) getFragmentManager().findFragmentById(R.id.filter_view);
        clsView = findViewById(R.id.classes_view);
        crseFrag = (CourseListFragment) getFragmentManager().findFragmentById(R.id.classes_view);
        ConstraintLayout all = (ConstraintLayout) findViewById(R.id.all);

        findViewById(R.id.all).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeRight() {
                System.out.println("right");
            }

            public void onSwipeLeft() {
                System.out.println("left");
            }
        });
        scheView = findViewById(R.id.schedule_view);
        List<CustomItems.ScheduleItem> schedules= CustomItems.getFromSharedPrefs(getApplicationContext());
        for (CustomItems.ScheduleItem item : schedules) {
            CustomItems.addSchedule(item);
        }

        //set references to menu items

        //setup up nav bar
        if(savedInstanceState != null)
            setupBar(savedInstanceState.getInt("page", 0));
        else
            setupBar(0);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onStop() {
        CustomItems.saveToSharedPrefs(getApplicationContext());
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        CustomItems.saveToSharedPrefs(getApplicationContext());
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", bottomView.getCurrentPage());
    }



    private void setupBar(int start){
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
                    if(crseFrag.isRefreshing())
                        return;
                    Filter filter = getFilters();
                    if(filter != null) {
                        crseFrag.updateClasses(filter, true, null);
                    }
                }
                return;
            }

            //get direction for animation
            int dir = getOpenDir(oldPos, newPos);

            //open selected page
            //handle any special needs when navigating to a page like loading all classes based on filters
            switch (newPos) {
                case 0:
                    show(filterLayout.getView(), dir);
                    break;
                case 1:
                    if(!crseFrag.isRefreshing()) {
                        Filter filter = getFilters();
                        if(filter != null) {
                            crseFrag.updateClasses(filter, false, mInterstitialAd);
                        }
                    }

                    RecyclerView rv = crseFrag.getView().findViewById(R.id.course_recycler_view);
                    View labels = crseFrag.getView().findViewById(R.id.labels);

                    float height = labels.getMeasuredHeight();
                    rv.setPadding(0, (int)(height-Math.abs(labels.getTranslationY())), 0, 0);

                    rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                            float newY = labels.getTranslationY() - dy;

                            newY = Math.max(newY, -(height - Utilities.dpToPx(1)));
                            newY = Math.min(newY, 0);

                            labels.setTranslationY(newY);
                            rv.setPadding(0, (int)(height-Math.abs(newY)), 0, 0);

                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });

                    show(crseFrag.getView(), dir);
                    break;
                case 2:
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
                    assert recyclerView != null;
                    CustomItems.SimpleItemRecyclerViewAdapter adapt = new CustomItems.SimpleItemRecyclerViewAdapter(this, CustomItems.SCHEDULES);
                    recyclerView.setAdapter(adapt);
                    CustomItems.rva = adapt;

                    Button newSchedBtn = (Button) findViewById(R.id.newScheduleButton);
                    newSchedBtn.setOnClickListener(view -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Enter new schedule name");

                        final EditText input = new EditText(MainActivity.this);
                        builder.setView(input);

                        builder.setPositiveButton("Add", (dialogInterface, i) -> {
                            if(input.getText().toString() != null && CustomItems.SCHEDULE_MAP.get(input.getText().toString()) == null) {
                                CustomItems.addSchedule(input.getText().toString());
                            }
                        });

                        builder.setNeutralButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

                        builder.show();
                    });

                    show(scheView, dir);
                    break;
            }

            //close old page
            //handle any special needs when navigating away from a page
            switch (oldPos) {
                case 0:
                    close(filterLayout.getView(), -dir);
                    break;
                case 1:
                    RecyclerView rv = crseFrag.getView().findViewById(R.id.course_recycler_view);
                    rv.clearOnScrollListeners();
                    close(crseFrag.getView(), -dir);
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
        bottomView.build(start);
        switch(start){
            case 0:
                filterLayout.getView().setVisibility(View.VISIBLE);
                crseFrag.getView().setVisibility(View.INVISIBLE);
                scheView.setVisibility(View.INVISIBLE);
                break;
            case 1:
                filterLayout.getView().setVisibility(View.INVISIBLE);
                crseFrag.getView().setVisibility(View.VISIBLE);
                scheView.setVisibility(View.INVISIBLE);
                break;
            case 2:
                filterLayout.getView().setVisibility(View.INVISIBLE);
                crseFrag.getView().setVisibility(View.INVISIBLE);
                scheView.setVisibility(View.VISIBLE);
                break;
        }

    }

    //todo add on start and on stop with a bundle

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

    public void searchFilter(){
     bottomView.changePosition(1);
    }

    private Filter getFilters(){
        return filterLayout.getFilters();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        //possible communication between fragment needed?
    }

    private class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context,new GestureListener());
        }
        @Override
        public boolean onTouch(View v, MotionEvent event){
            return gestureDetector.onTouchEvent(event);
        }


        private final class GestureListener extends GestureDetector.SimpleOnGestureListener{

            private static final int SWIPE_THRESHOLD = 120;
            private static final int SWIPE_VELOCITY_THRESHOLD = 200;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    System.out.println(diffX);
                    System.out.println(diffY);
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

    }
}
