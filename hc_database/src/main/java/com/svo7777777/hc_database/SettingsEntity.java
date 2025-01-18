package com.svo7777777.hc_database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class SettingsEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double hours;
    public double price;
}
