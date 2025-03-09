package com.svilvo.hc_database.views;

import androidx.room.Relation;

import com.svilvo.hc_database.views.MonthSummary;

import java.util.List;

public class YearSummary {
    public int id;
    public int year;
    public double hours;
    public double salary;

    public int employeeId;

    @Relation(
            parentColumn = "id",
            entityColumn = "yearId"
    )
    public List<MonthSummary> months;
}
