package com.svilvo.hourscalculator.ui.fragments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.svilvo.hc_database.entities.EmployeeEntity;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<EmployeeEntity>> selectedEmployee = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedYear = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedMonth = new MutableLiveData<>();

    private MutableLiveData<Integer> selectedResult = new MutableLiveData<>();

    public MutableLiveData<List<EmployeeEntity>> getSelectedEmployee() {
        return selectedEmployee;
    }
    public void setSelectedEmployee(List<EmployeeEntity> emp) {
        selectedEmployee.setValue(emp);
    }

    public MutableLiveData<Integer> getSelectedYear() {
        return selectedYear;
    }
    public void setSelectedYear(Integer year) {
        selectedYear.setValue(year);
    }

    public MutableLiveData<Integer> getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(Integer month) {
        selectedMonth.setValue(month);
    }

    public MutableLiveData<Integer> getSelectedResult() {
        return selectedResult;
    }

    public void setSelectedResult(Integer result) {
        selectedResult.setValue(result);
    }
}
