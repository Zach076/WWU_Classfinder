package com.csci412.classfinder.animatedbottombar;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavItem extends RelativeLayout {

    //track open nav item
    boolean statusOpened = false;

    public NavItem(Context context) {
        super(context);
    }


    public NavItem init(Context context, Item item){

        //setup params for nav item
        this.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1f)
        );
        this.setGravity(Gravity.CENTER);

        //set up inactive
        TextView tv = new TextView(context);
        tv.setText(item.label);
        tv.setTextColor(item.inactiveColor);
        addView(tv);

        //setup active
        tv = new TextView(context);
        tv.setText(item.label);
        tv.setTextColor(item.activeColor);
        addView(tv);

        //setup indicator for this nav item
        RelativeLayout layoutHover = new RelativeLayout(context);
        layoutHover.setLayoutParams(this.getLayoutParams());
        layoutHover.setVisibility(View.GONE);
        layoutHover.setGravity(Gravity.CENTER);
        addView(layoutHover);

        //start closed
        close();
        return this;
    }

    //closes this nav item
    public void close() {
        statusOpened = false;
        getChildAt(0).setVisibility(View.VISIBLE);
        getChildAt(1).setVisibility(View.GONE);
    }

    //opens this nav item
    public void show() {
        statusOpened = true;
        getChildAt(0).setVisibility(View.GONE);
        getChildAt(1).setVisibility(View.VISIBLE);
    }
}
