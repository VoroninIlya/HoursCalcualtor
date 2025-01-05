package com.svo7777777.hc_database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "years",
        indices = {@Index(value = "year", unique = true)} // Year should be unique
)
public class YearEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int employeeId;
    public String year;
}
