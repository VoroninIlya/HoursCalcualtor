package com.svilvo.dialogs;

import com.svilvo.hc_database.EmployeeEntity;

public interface EmployeeDialogCallback {
    void onComplete(String employeeLastName, String employeeFirstName, String employeeAge,
                    String hours, String price);
}
