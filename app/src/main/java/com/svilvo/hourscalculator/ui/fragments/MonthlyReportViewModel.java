package com.svilvo.hourscalculator.ui.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.svilvo.hc_database.views.MonthSummary;
import com.svilvo.utils.DatabaseHandler;

public class MonthlyReportViewModel extends AndroidViewModel {
    DatabaseHandler dbh = null;
    public MonthlyReportViewModel(@NonNull Application application) {
        super(application);
        dbh = new DatabaseHandler(application.getApplicationContext());
    }

    public LiveData<MonthSummary> getMonthSummary(int employeeId, int yearNbr, int month) {
        return dbh.getMonthSummary(employeeId, yearNbr, month);
    }
}