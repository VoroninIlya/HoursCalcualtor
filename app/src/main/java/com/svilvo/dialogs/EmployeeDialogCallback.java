package com.svilvo.dialogs;

public interface EmployeeDialogCallback {
    void onComplete(String employeeLastName, String employeeFirstName, String employeeAge,
                    String hours, String price);
}
