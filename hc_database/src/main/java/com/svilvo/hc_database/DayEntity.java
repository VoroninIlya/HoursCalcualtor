package com.svilvo.hc_database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "days",
        indices = {@Index(value = {"day", "monthId"}, unique = true)}
)
public class DayEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int monthId;
    public int day;
    public double hours;
    public double price;
}
