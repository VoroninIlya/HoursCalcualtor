package com.svilvo.hc_database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.svilvo.hc_database.daos.DayDao;
import com.svilvo.hc_database.daos.EmployeeDao;
import com.svilvo.hc_database.daos.MonthDao;
import com.svilvo.hc_database.daos.SettingsDao;
import com.svilvo.hc_database.daos.YearDao;
import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.entities.MonthEntity;
import com.svilvo.hc_database.entities.SettingsEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.hc_database.views.MonthSummary;

@Database(entities = {
        EmployeeEntity.class,
        YearEntity.class,
        MonthEntity.class,
        DayEntity.class,
        SettingsEntity.class},
        views = {MonthSummary.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EmployeeDao employeeDao();
    public abstract YearDao yearDao();
    public abstract MonthDao monthDao();
    public abstract DayDao dayDao();
    public abstract SettingsDao settingsDao();
}
