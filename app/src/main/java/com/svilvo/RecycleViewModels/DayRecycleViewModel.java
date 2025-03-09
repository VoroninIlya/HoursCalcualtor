package com.svilvo.recycleviewmodels;

import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.views.MonthSummary;

public class DayRecycleViewModel implements ListItem {
    private DayEntity day;
    private int value;

    public DayRecycleViewModel(DayEntity day, int value) {
        this.day = day;
        this.value = value;
    }

    public DayEntity getDay() { return day; }
    public int getValue() { return value; }
}
