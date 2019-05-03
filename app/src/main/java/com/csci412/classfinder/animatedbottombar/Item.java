package com.csci412.classfinder.animatedbottombar;

public class Item {

    String label;
    int activeColor;
    int inactiveColor;

    //an Item that has the information needed to create a nav item for a nav bar
    public Item(String label) {
        this.label = label;
    }
}