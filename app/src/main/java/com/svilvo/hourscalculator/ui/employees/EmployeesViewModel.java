package com.svilvo.hourscalculator.ui.employees;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class EmployeesViewModel extends AndroidViewModel {
    DatabaseHandler dbh = null;
    private LiveData<List<EmployeeEntity>> employees = null;
    private final List<EmployeeEntity> selectedEmployee = new ArrayList<>();

    public EmployeesViewModel(@NonNull Application application) {
        super(application);
        dbh = new DatabaseHandler(application.getApplicationContext());
        employees = dbh.getEmployeesLd();
    }

    public LiveData<List<EmployeeEntity>> getEmployees() {
        return employees;
    }

    public List<EmployeeEntity> getSelectedEmployees() {
        return selectedEmployee;
    }

    public void addSelectedEmployee(EmployeeEntity emp) {
        selectedEmployee.add(emp);
    }

    public void removeSelectedEmployee(EmployeeEntity emp) {
        selectedEmployee.remove(emp);
    }

}