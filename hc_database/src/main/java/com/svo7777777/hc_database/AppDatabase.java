package com.svo7777777.hc_database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
        EmployeeEntity.class,
        YearEntity.class,
        MonthEntity.class,
        DayEntity.class,
        SettingsEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EmployeeDao employeeDao();
    public abstract YearDao yearDao();
    public abstract MonthDao monthDao();
    public abstract DayDao dayDao();
    public abstract SettingsDao settingsDao();
}
