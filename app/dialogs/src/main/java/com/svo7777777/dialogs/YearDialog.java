package com.svo7777777.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class YearDialog {
    public boolean isInitialized = false;
    private int dialogTitleId = -1;
    private int dialogLayoutId = -1;
    private int yearEditTextId = -1;
    private int buttonConfirmId = -1;
    private int buttonCancelId = -1;

    public YearDialog(int dialogTitleId, int dialogLayoutId,
                          int yearEditTextId,
                          int buttonConfirmId, int buttonCancelId) {

        isInitialized = false;

        this.dialogTitleId = dialogTitleId > 0 ? dialogTitleId : -1;
        this.dialogLayoutId = dialogLayoutId > 0 ? dialogLayoutId : -1;
        this.yearEditTextId = yearEditTextId > 0 ? yearEditTextId : -1;
        this.buttonConfirmId = buttonConfirmId > 0 ? buttonConfirmId : -1;
        this.buttonCancelId = buttonCancelId > 0 ? buttonCancelId : -1;

        if (this.dialogTitleId > 0 && this.dialogLayoutId > 0 &&
                this.yearEditTextId > 0 &&
                this.buttonConfirmId > 0 && this.buttonCancelId > 0) {
            isInitialized = true;
        }
    }

    public boolean open(Context ctx, YearDialogCallback cb, String year) {

        if(!isInitialized) return false;

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);
        dialog.setTitle(dialogTitleId /*R.string.add_employee_btn*/);
        LinearLayout dialogView =
                (LinearLayout) LayoutInflater.from(ctx)
                        .inflate(dialogLayoutId /*R.layout.dialog_employee_picker*/, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        Spinner editTextYear = dialogView.findViewById(yearEditTextId /*R.id.editTextEmployeeLastName*/);
        Button buttonConfirm = dialogView.findViewById(buttonConfirmId /*R.id.buttonConfirm*/);
        Button buttonCancel = dialogView.findViewById(buttonCancelId /*R.id.buttonCancel*/);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = 2000;
        int length = 10;
        int initialSelection = -1;
        int initialValue = -1;
        if(!year.isEmpty())
            initialValue = Integer.parseInt(year);

        List<String> dynamicList = new ArrayList<>();
        for(int i = 0; i < length; i++) {
            int tmpYear = currentYear - i;
            if(initialValue > 0 && initialValue == tmpYear)
                initialSelection = i;

            dynamicList.add(String.valueOf(tmpYear));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_spinner_item,
                dynamicList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editTextYear.setAdapter(adapter);

        if(initialSelection >= 0)
            editTextYear.setSelection(initialSelection);

        // Set button actions
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = editTextYear.getSelectedItem().toString().trim();
                if (!year.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Year entered: " + year, Toast.LENGTH_SHORT).show();
                    cb.onComplete(year);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ctx, "Please enter a year!", Toast.LENGTH_SHORT).show();
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
