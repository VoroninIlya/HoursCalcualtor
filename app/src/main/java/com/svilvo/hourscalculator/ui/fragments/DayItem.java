package com.svilvo.hourscalculator.ui.fragments;

import android.view.View;

public class DayItem {
    public int dayNumber;
    public String dayOfWeek;
    public boolean isWeekend;
    public boolean isInsideDb;
    public boolean isVisible;
    public double hours;
    public double price;
    public int itemSize;

    public View.OnClickListener onClick;
    public DayItem(int dayNumber, String dayOfWeek,
                   boolean isWeekend, boolean isInsideDb,
                   double hours, double price,
                   int itemSize, boolean isVisible,
                   View.OnClickListener onClick) {
        this.dayNumber = dayNumber;
        this.dayOfWeek = dayOfWeek;
        this.isWeekend = isWeekend;
        this.isInsideDb = isInsideDb;
        this.hours = hours;
        this.price = price;
        this.itemSize = itemSize;
        this.isVisible = isVisible;
        this.onClick = onClick;
    }

}
