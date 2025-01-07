package com.svo7777777.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EmployeeDialog {
    public boolean isInitialized = false;
    private int dialogTitleId = -1;
    private int dialogLayoutId = -1;
    private int employeeFirstNameEditTextId = -1;
    private int employeeLastNameEditTextId = -1;
    private int employeeAgeEditTextId = -1;
    private int buttonConfirmId = -1;
    private int buttonCancelId = -1;

    public EmployeeDialog(int dialogTitleId, int dialogLayoutId,
                          int employeeLastNameEditTextId, int employeeFirstNameEditTextId,
                          int employeeAgeEditTextId,
                          int buttonConfirmId, int buttonCancelId) {

        isInitialized = false;

        this.dialogTitleId = dialogTitleId > 0 ? dialogTitleId : -1;
        this.dialogLayoutId = dialogLayoutId > 0 ? dialogLayoutId : -1;
        this.employeeFirstNameEditTextId = employeeFirstNameEditTextId > 0 ? employeeFirstNameEditTextId : -1;
        this.employeeLastNameEditTextId = employeeLastNameEditTextId > 0 ? employeeLastNameEditTextId : -1;
        this.employeeAgeEditTextId = employeeAgeEditTextId > 0 ? employeeAgeEditTextId : -1;
        this.buttonConfirmId = buttonConfirmId > 0 ? buttonConfirmId : -1;
        this.buttonCancelId = buttonCancelId > 0 ? buttonCancelId : -1;

        if (this.dialogTitleId > 0 && this.dialogLayoutId > 0 &&
            this.employeeFirstNameEditTextId > 0 && this.employeeLastNameEditTextId > 0 &&
            this.employeeAgeEditTextId > 0 &&
            this.buttonConfirmId > 0 && this.buttonCancelId > 0) {
            isInitialized = true;
        }
    }
    public boolean open(Context ctx, EmployeeDialogCallback cb,
                        String lastName, String firstName, String age) {

        if(!isInitialized) return false;

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);
        dialog.setTitle(dialogTitleId /*R.string.add_employee_btn*/);
        LinearLayout dialogView =
                (LinearLayout)LayoutInflater.from(ctx)
                        .inflate(dialogLayoutId /*R.layout.dialog_employee_picker*/, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        EditText editTextEmployeeLastName = dialogView.findViewById(employeeLastNameEditTextId /*R.id.editTextEmployeeLastName*/);
        EditText editTextEmployeeFirstName = dialogView.findViewById(employeeFirstNameEditTextId /*R.id.editTextEmployeeFirstName*/);
        EditText editTextEmployeeAge = dialogView.findViewById(employeeAgeEditTextId /*R.id.editTextEmployeeAge*/);
        Button buttonConfirm = dialogView.findViewById(buttonConfirmId /*R.id.buttonConfirm*/);
        Button buttonCancel = dialogView.findViewById(buttonCancelId /*R.id.buttonCancel*/);

        if(!lastName.isEmpty()){
            editTextEmployeeLastName.setText(lastName);
        }

        if(!firstName.isEmpty()){
            editTextEmployeeFirstName.setText(firstName);
        }

        if(!age.isEmpty()){
            editTextEmployeeAge.setText(age);
        }

        // Set button actions
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeLastName = editTextEmployeeLastName.getText().toString().trim();
                String employeeFirstName = editTextEmployeeFirstName.getText().toString().trim();
                String employeeAge = editTextEmployeeAge.getText().toString().trim();
                if (!employeeLastName.isEmpty() && !employeeFirstName.isEmpty() && !employeeAge.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Year entered: " + year, Toast.LENGTH_SHORT).show();
                    cb.onComplete(employeeLastName, employeeFirstName, employeeAge);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ctx, "Please enter employee's first name, last name and age!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();

        return true;
    }
}
