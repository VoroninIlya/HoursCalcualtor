package com.svilvo.hourscalculator.ui.employees;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.svilvo.hc_database.views.YearSummary;
import com.svilvo.utils.DatabaseHandler;

public class YearlyReportViewModel extends AndroidViewModel {
    DatabaseHandler dbh = null;

    public YearlyReportViewModel(@NonNull Application application) {
        super(application);
        dbh = new DatabaseHandler(application.getApplicationContext());
    }

    public LiveData<YearSummary> getYearSummaries(int employeeId, int yearNbr) {
        return dbh.getYearSummary(employeeId, yearNbr);
    }

}