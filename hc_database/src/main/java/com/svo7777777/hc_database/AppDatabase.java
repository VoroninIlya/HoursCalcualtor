package com.svo7777777.hc_database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {EmployeeEntity.class, YearEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EmployeeDao employeeDao();
    public abstract YearDao yearDao();

}
