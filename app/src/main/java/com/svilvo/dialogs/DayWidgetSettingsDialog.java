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
import java.util.Arrays;
import java.util.List;

public class DayWidgetSettingsDialog {
    public DayWidgetSettingsDialog() {}

    public boolean open(Context ctx, DayWidgetSettingsDialogCallback cb,
                        DayWidgetSettingsDialogDismissCallback dmcb,
                        List<EmployeeEntity> employees, int selectedEmployee,
                        int selectedDay) {

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);

        LinearLayout dialogView =
                (LinearLayout) LayoutInflater.from(ctx)
                        .inflate(R.layout.dialog_day_widget_settings_picker, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        Spinner employeesSpinner = dialogView.findViewById(R.id.employeesSpinner);
        Spinner daySpinner = dialogView.findViewById(R.id.daySpinner);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        int selectedEmployeeIndex = 0;
        int index = 0;
        List<String> employeesList = new ArrayList<>();
        for(EmployeeEntity e : employees) {
            if(e.id == selectedEmployee)
                selectedEmployeeIndex = index;
            employeesList.add(e.lastName + " " + e.firstName +
                    " (" + e.age + ")");
            index++;
        }

        ArrayAdapter<String> employeesAdapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_spinner_item,
                employeesList
        );

        employeesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        employeesSpinner.setAdapter(employeesAdapter);
        employeesSpinner.setSelection(selectedEmployeeIndex);

        String[] days = ctx.getResources().getStringArray(R.array.days);

        List<String> daysList = new ArrayList<>();
        daysList.addAll(Arrays.asList(days));

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_spinner_item,
                daysList
        );

        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        daySpinner.setAdapter(daysAdapter);
        daySpinner.setSelection(selectedDay >= 0 ? selectedDay : 2);

        // Set button actions
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedEmployeeIndex = employeesSpinner.getSelectedItemPosition();
                int selectedDayIndex = daySpinner.getSelectedItemPosition();

                if (selectedEmployeeIndex >= 0 && selectedDayIndex >= 0) {
                    int employeeId = employees.get(selectedEmployeeIndex).id;
                    int day = selectedDayIndex;
                    cb.onComplete(employeeId, day);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ctx, "Please enter hours and price", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.onComplete(-1, -1);
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(dialog1 -> dmcb.onComplete());
        // Show the dialog
        dialog.show();

        return true;
    }
}
