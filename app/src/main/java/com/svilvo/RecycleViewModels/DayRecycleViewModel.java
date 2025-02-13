package com.svilvo.recycleviewmodels;

import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.views.MonthSummary;

public class DayRecycleViewModel implements ListItem {
    private DayEntity day;

    public DayRecycleViewModel(DayEntity day) {
        this.day = day;
    }

    public DayEntity getDay() { return day; }
}
