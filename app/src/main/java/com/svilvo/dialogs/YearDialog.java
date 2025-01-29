package com.svilvo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.svilvo.hourscalculator.R;

import java.util.ArrayList;
import java.util.List;

public class YearDialog {
    public YearDialog() {}

    public boolean open(Context ctx, YearDialogCallback cb, String year) {

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);
        dialog.setTitle(R.string.add_year_btn);
        LinearLayout dialogView =
                (LinearLayout) LayoutInflater.from(ctx)
                        .inflate(R.layout.dialog_year_picker, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        Spinner editTextYear = dialogView.findViewById(R.id.editTextYear);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

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
                    Toast.makeText(ctx, R.string.error_confirm_year, Toast.LENGTH_SHORT).show();
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
