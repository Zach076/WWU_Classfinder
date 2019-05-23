package com.csci412.classfinder.animatedbottombar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.csci412.classfinder.Utilities;

import java.util.ArrayList;

public class BottomBar extends LinearLayout {

private NavigationListner mListener = null;
private ArrayList<Item> mItems = new ArrayList<>();

private int bgColor = Color.WHITE;
private int bgIndicatorColor = Color.BLACK;
private int activeColor = Color.BLACK;
private int inactiveColor = Color.GRAY;

    public BottomBar(Context context){
        super(context);
        init();
    }

    public BottomBar(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        this.setOrientation(VERTICAL);
    }

    public BottomBar setupListener(NavigationListner listener){
        this.mListener = listener;
        return this;
    }

    public BottomBar addItem(Item item){
        item.activeColor = activeColor;
        item.inactiveColor = inactiveColor;
        this.mItems.add(item);
        return this;
    }

    public BottomBar setBgColor(int bgColor){
        this.bgColor = bgColor;
        return this;
    }

    public BottomBar setIndicatorColor(int bgIndicatorColor){
        this.bgIndicatorColor = bgIndicatorColor;
        return this;
    }

    public BottomBar setActiveColor(int activeColor){
        this.activeColor = activeColor;
        for(Item item : mItems)
            item.activeColor = activeColor;
        return this;
    }
    public BottomBar setInactiveColor(int inactiveColor){
        this.inactiveColor = inactiveColor;
        for(Item item : mItems)
            item.inactiveColor = inactiveColor;
        return this;
    }

    //called when everything is set up to build the nav bar
    public void build(int start) {
        createNavigationItems();

        setBackgroundColor(bgColor);

        moveIndicator(start, mItems.size());

        LinearLayout nav = (LinearLayout)getChildAt(1);
        NavItem item = (NavItem)nav.getChildAt(start);
        item.show();
    }

    //creates each nav item with the proper params
    private void createNavigationItems() {
        LayoutParams indicatorParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utilities.dpToPx(2));
        LinearLayout llIndicator = new LinearLayout(getContext());
        llIndicator.setOrientation(HORIZONTAL);
        llIndicator.setLayoutParams(indicatorParams);

        LayoutParams navParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LinearLayout llNav = new LinearLayout(getContext());
        llNav.setOrientation(HORIZONTAL);
        llNav.setLayoutParams(navParams);

        createIndicator(mItems.size(), llIndicator);

        for (int i = 0; i  < mItems.size(); i++) {
            createItem(mItems.get(i), llNav, i);
        }

        addView(llIndicator);
        addView(llNav);
    }

    //uses items to create nav item
    private void createItem(Item item, LinearLayout llNav, int position) {
        NavItem navi = new NavItem(getContext()).init(getContext(), item);

        navi.setOnClickListener(v -> {
            int oldPos = closeAll();
            moveIndicator(position, mItems.size());
            navi.show();
            mListener.OnClick(oldPos, position);
        });

        llNav.addView(navi);
    }

    //tries to close all navs and returns the pos of the opened nav
    private int closeAll() {
        LinearLayout nav = (LinearLayout)getChildAt(1);
        for (int i = 0; i < nav.getChildCount(); i++) {
            NavItem item = (NavItem)nav.getChildAt(i);
            if (item.statusOpened) {
                item.close();
                return i;
            }
        }
        return 0;
    }

    //create indicator
    private void createIndicator(int size, LinearLayout llIndicator) {
            LayoutParams lfParams =  new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0f);
            LinearLayout leftFlank = new LinearLayout(getContext());
            leftFlank.setLayoutParams(lfParams);

            LayoutParams indicatorParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
            LinearLayout indicator = new LinearLayout(getContext());
            indicator.setLayoutParams(indicatorParams);
            indicator.setBackgroundColor(bgIndicatorColor);

            LayoutParams lrParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, (float)(size - 1));
            LinearLayout rightFlank = new LinearLayout(getContext());
            rightFlank.setLayoutParams(lrParams);

            llIndicator.addView(leftFlank);
            llIndicator.addView(indicator);
            llIndicator.addView(rightFlank);
    }

    //move indicator
    private void moveIndicator(int position,  int size) {
            LinearLayout llIndicator = (LinearLayout)this.getChildAt(0);
            LinearLayout leftFlank = (LinearLayout)llIndicator.getChildAt(0);
            LinearLayout rightFlank = (LinearLayout)llIndicator.getChildAt(2);

            LayoutParams llparams = (LayoutParams)leftFlank.getLayoutParams();
            LayoutParams rlparams = (LayoutParams)rightFlank.getLayoutParams();

            llparams.weight = (float)position;
            rlparams.weight = (float)(size - position - 1);

            leftFlank.setLayoutParams(llparams);
            rightFlank.setLayoutParams(rlparams);
    }
}
