package com.svilvo.hc_database.views;

import androidx.room.DatabaseView;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.svilvo.hc_database.entities.DayEntity;

import java.util.List;

@DatabaseView(
        viewName = "month_summary_view",
        value = "SELECT m.id AS id, m.month AS month, m.yearId AS yearId, " +
        "SUM(d.hours) AS hours, " +
        "SUM(d.hours * d.price) AS salary " +
        "FROM months m " +
        "LEFT JOIN days d ON m.id = d.monthId " +
        "GROUP BY m.id, m.month"
)
public class MonthSummary {
    @PrimaryKey
    public int id;
    public int month;
    public double hours;
    public double salary;

    public int yearId;

    @Relation(
            parentColumn = "id",
            entityColumn = "monthId"
    )
    public List<DayEntity> days;
}
