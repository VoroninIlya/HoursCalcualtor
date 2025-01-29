package com.svilvo.hc_database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "settings",
        indices = {@Index(value = {"employeeId"}, unique = true) }
)
public class SettingsEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int employeeId;

    public double hours;
    public double price;
}
