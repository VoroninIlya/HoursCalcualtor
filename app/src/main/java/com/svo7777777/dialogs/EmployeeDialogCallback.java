package com.svo7777777.dialogs;

import com.svo7777777.hc_database.EmployeeEntity;

public interface EmployeeDialogCallback {
    void onComplete(String employeeLastName, String employeeFirstName, String employeeAge,
                    String hours, String price);
}
