package com.svilvo.hourscalculator.ui.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.entities.MonthEntity;
import com.svilvo.hc_database.entities.SettingsEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.hc_database.views.YearSummary;
import com.svilvo.utils.DatabaseHandler;

import java.util.List;

public class MonthViewModel extends AndroidViewModel {
    private DatabaseHandler dbh = null;
    public MonthViewModel(@NonNull Application application) {
        super(application);
        dbh = new DatabaseHandler(application.getApplicationContext());
    }

    public MonthEntity getMonth(int yearId, int month) {
        return dbh.getMonth(yearId, month);
    }

    public LiveData<MonthEntity> getMonthLd(int yearId, int month) {
        return dbh.getMonthLd(yearId, month);
    }

    public YearEntity getYear(int employeeId, int year) {
        return dbh.getYear(employeeId, year);
    }

    public LiveData<YearEntity> getYearLd(int employeeId, int year) {
        return dbh.getYearLd(employeeId, year);
    }

    public List<DayEntity> getDays(int monthId) {
        return dbh.getDays(monthId);
    }

    public LiveData<List<DayEntity>> getDaysLd(int monthId) {
        return dbh.getDaysLd(monthId);
    }

    public LiveData<DayEntity> getDayLd(int monthId, int day) {
        return dbh.getDayLd(monthId, day);
    }

    public LiveData<YearSummary> getYearSummary(int employeeId, int year) {
        return dbh.getYearSummary(employeeId, year);
    }

    public SettingsEntity getSettings() {
        return dbh.getSettings();
    }

    public SettingsEntity getSettings(int employeeId) {
        return dbh.getSettings(employeeId);
    }

    public void addSettings(SettingsEntity settings) {
        dbh.writeSettings(settings);
    }

    public long addYear(YearEntity year) {
        return dbh.insertYear(year);
    }

    public long addMonth(MonthEntity month) {
        return dbh.writeMonth(month);
    }

    public DayEntity getDay(int monthId, int day) {
        return dbh.getDay(monthId, day);
    }

    public long addDay(DayEntity day) {
        return dbh.writeDay(day);
    }

    public void deleteDay(DayEntity day) {
        dbh.deleteDay(day);
    }

    public void updateDay(DayEntity day) {
        dbh.updateDay(day);
    }
}
