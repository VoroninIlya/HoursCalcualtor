package com.svilvo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.svilvo.hourscalculator.R;

public class EmployeeDialog {

    public EmployeeDialog() {}
    public boolean open(Context ctx, EmployeeDialogCallback cb,
                        EmployeeDialogOnDismissCallback dmcb,
                        String lastName, String firstName, String age,
                        String hours, String price) {

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);
        dialog.setTitle(R.string.add_employee_btn);
        LinearLayout dialogView =
                (LinearLayout)LayoutInflater.from(ctx)
                        .inflate(R.layout.dialog_employee_picker, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        EditText editTextEmployeeLastName = dialogView.findViewById(R.id.editTextEmployeeLastName);
        EditText editTextEmployeeFirstName = dialogView.findViewById(R.id.editTextEmployeeFirstName);
        EditText editTextEmployeeAge = dialogView.findViewById(R.id.editTextEmployeeAge);
        EditText editTextEmployeeHours = dialogView.findViewById(R.id.editTextEmployeeHours);
        EditText editTextEmployeePrice = dialogView.findViewById(R.id.editTextEmployeePrice);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        if(!lastName.isEmpty())
            editTextEmployeeLastName.setText(lastName);

        if(!firstName.isEmpty())
            editTextEmployeeFirstName.setText(firstName);

        if(!age.isEmpty())
            editTextEmployeeAge.setText(age);

        if(!hours.isEmpty())
            editTextEmployeeHours.setText(hours);

        if(!price.isEmpty())
            editTextEmployeePrice.setText(price);

        // Set button actions
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeLastName = editTextEmployeeLastName.getText().toString().trim();
                String employeeFirstName = editTextEmployeeFirstName.getText().toString().trim();
                String employeeAge = editTextEmployeeAge.getText().toString().trim();
                String hours = editTextEmployeeHours.getText().toString().trim();
                String price = editTextEmployeePrice.getText().toString().trim();
                if (!employeeLastName.isEmpty() && !employeeFirstName.isEmpty() && !employeeAge.isEmpty()) {
                    cb.onComplete(employeeLastName, employeeFirstName, employeeAge, hours, price);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ctx, R.string.error_confirm_employee, Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        SharedPreferences prefs = ctx.getSharedPreferences("hours_calculator_tutorial", ctx.MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);
        if(!tutorialShown) {
            dialog.setOnShowListener(dialog1 -> showTutorial(ctx, dialog));
            buttonCancel.setEnabled(false);
        }

        dialog.setOnDismissListener(dialog1 -> dmcb.onComplete());
        dialog.show();

        return true;
    }

    private void showTutorial(Context ctx, Dialog d) {
        new TapTargetSequence(d)
            .targets(
                TapTarget.forView(d.findViewById(R.id.editTextEmployeeAge),
                        ctx.getString(R.string.tutorial_employee_age_title),
                        ctx.getString(R.string.tutorial_employee_age_description))
                    .id(3)
                    .tintTarget(false)
                    .transparentTarget(true)
                    .targetRadius(150)
                    .outerCircleColor(R.color.notice_100)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .descriptionTextSize(16)
                    .cancelable(false),
                TapTarget.forView(d.findViewById(R.id.buttonConfirm),
                        ctx.getString(R.string.tutorial_employee_confirm_title),
                        ctx.getString(R.string.tutorial_employee_confirm_description))
                    .id(5)
                    .tintTarget(false)
                    .transparentTarget(true)
                    .targetRadius(50)
                    .outerCircleColor(R.color.notice_100)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .descriptionTextSize(16)
                    .cancelable(false)
            )
            .start();
    }
}
