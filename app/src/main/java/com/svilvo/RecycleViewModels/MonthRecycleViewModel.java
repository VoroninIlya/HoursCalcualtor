package com.svilvo.recycleviewmodels;

import com.svilvo.hc_database.views.MonthSummary;

public class MonthRecycleViewModel implements ListItem {
    private MonthSummary month;

    public MonthRecycleViewModel(MonthSummary month) {
        this.month = month;
    }

    public MonthSummary getMonth() { return month; }

}
