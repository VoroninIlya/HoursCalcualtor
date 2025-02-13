package com.svilvo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hourscalculator.R;

import java.util.ArrayList;
import java.util.List;

public class DayWidgetSettingsDialog {
    public DayWidgetSettingsDialog() {}

    public boolean open(Context ctx, DayWidgetSettingsDialogCallback cb, List<EmployeeEntity> employees) {

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);

        LinearLayout dialogView =
                (LinearLayout) LayoutInflater.from(ctx)
                        .inflate(R.layout.dialog_day_widget_settings_picker, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        Spinner employeesSpinner = dialogView.findViewById(R.id.employeesSpinner);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        List<String> dynamicList = new ArrayList<>();
        for(EmployeeEntity e : employees) {
            dynamicList.add(e.lastName + " " + e.firstName +
                    " (" + e.age + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_spinner_item,
                dynamicList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        employeesSpinner.setAdapter(adapter);

        // Set button actions
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedIndex = employeesSpinner.getSelectedItemPosition();

                if (selectedIndex >= 0) {
                    int employeeId = employees.get(selectedIndex).id;
                    cb.onComplete(employeeId);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ctx, "Please enter hours and price", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.onComplete(-1);
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();

        return true;
    }
}
