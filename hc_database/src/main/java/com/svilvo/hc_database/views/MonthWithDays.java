package com.svilvo.hc_database.views;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.svilvo.hc_database.entities.DayEntity;

import java.util.List;

public class MonthWithDays {
    @Embedded
    public MonthSummary summary;
    @Relation(
            parentColumn = "id",
            entityColumn = "monthId"
    )
    public List<DayEntity> days;
}
