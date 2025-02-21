package com.svilvo.recycleviewmodels;

import com.svilvo.hc_database.views.MonthSummary;

public class MonthRecycleViewModel implements ListItem {
    private MonthSummary month;
    private int value;

    public MonthRecycleViewModel(MonthSummary month, int value) {
        this.month = month;
        this.value = value;
    }

    public MonthSummary getMonth() { return month; }
    public int getValue() { return value; }
}
