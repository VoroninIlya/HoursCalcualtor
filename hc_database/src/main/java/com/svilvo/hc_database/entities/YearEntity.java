package com.svilvo.hc_database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "years",
        indices = {@Index(value = {"year", "employeeId"}, unique = true)} // Year should be unique
)
public class YearEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int employeeId;
    public int year;
}
