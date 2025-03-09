package com.svilvo.recycleviewmodels;

import com.svilvo.hc_database.entities.EmployeeEntity;

import java.util.List;

public class EmployeeRecycleViewModel implements ListItem {
    private EmployeeEntity employee;
    private double totalHours;
    private List<MonthRecycleViewModel> months;
    private List<DayRecycleViewModel> days;
    private boolean expanded;

    public EmployeeRecycleViewModel(EmployeeEntity employee, double totalHours) {
        this.employee = employee;
        this.totalHours = totalHours;
        this.months = null;
        this.expanded = false;
    }

    public EmployeeEntity getEmployee() { return employee; }
    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double d) { totalHours = d; }
    public List<MonthRecycleViewModel> getMonths() { return months; }
    public List<DayRecycleViewModel> getDays() { return days; }

    public void setMonths(List<MonthRecycleViewModel> m) { months = m; }

    public void setDays(List<DayRecycleViewModel> d) { days = d; }
    public MonthRecycleViewModel getMonth(int index) { return months.get(index); }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}
