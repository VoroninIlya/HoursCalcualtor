package com.svilvo.hc_database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "employees")
public class EmployeeEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "lastName")
    public String lastName;

    @ColumnInfo(name = "firstName")
    public String firstName;

    @ColumnInfo(name = "age")
    public int age;
}
