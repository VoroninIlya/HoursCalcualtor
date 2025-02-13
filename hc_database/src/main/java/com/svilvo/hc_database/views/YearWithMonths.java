package com.svilvo.hc_database.views;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.svilvo.hc_database.views.YearSummary;

import java.util.List;

public class YearWithMonths {
    @Embedded
    public YearSummary summary;
    @Relation(
            parentColumn = "id",
            entityColumn = "yearId"
    )
    public List<MonthSummary> months;
}
