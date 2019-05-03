package com.csci412.classfinder.animatedbottombar;

public interface NavigationListner {
    //a listener used by nav bar to callback when a nav item is clicked
    void OnClick(int oldPos, int newPos);
}