package com.svo7777777.hc_database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "months",
        indices = {@Index(value = {"month", "yearId"}, unique = true)}
)
public class MonthEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int yearId;
    public int month;
}
