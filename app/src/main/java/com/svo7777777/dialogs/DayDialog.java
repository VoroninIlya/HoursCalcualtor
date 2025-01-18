package com.svo7777777.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.svo7777777.hc_database.DayEntity;

public class DayDialog {
    public boolean isInitialized = false;
    private int dialogTitleId = -1;
    private int dialogLayoutId = -1;
    private int hoursEditTextId = -1;
    private int priceEditTextId = -1;
    private int buttonConfirmId = -1;
    private int buttonDeleteId = -1;
    private int buttonCancelId = -1;

    public DayDialog(int dialogTitleId, int dialogLayoutId,
                     int hoursEditTextId, int priceEditTextId,
                     int buttonConfirmId, int buttonDeleteId, int buttonCancelId) {

        isInitialized = false;

        this.dialogTitleId = dialogTitleId > 0 ? dialogTitleId : -1;
        this.dialogLayoutId = dialogLayoutId > 0 ? dialogLayoutId : -1;
        this.hoursEditTextId = hoursEditTextId > 0 ? hoursEditTextId : -1;
        this.priceEditTextId = priceEditTextId > 0 ? priceEditTextId : -1;
        this.buttonConfirmId = buttonConfirmId > 0 ? buttonConfirmId : -1;
        this.buttonDeleteId = buttonDeleteId > 0 ? buttonDeleteId : -1;
        this.buttonCancelId = buttonCancelId > 0 ? buttonCancelId : -1;

        if (this.dialogTitleId > 0 && this.dialogLayoutId > 0 &&
                this.hoursEditTextId > 0 && this.priceEditTextId > 0 &&
                this.buttonConfirmId > 0 && this.buttonDeleteId > 0 && this.buttonCancelId > 0) {
            isInitialized = true;
        }
    }
    public boolean open(Context ctx, DayDialogCallback cb,
                        DayDeleteCallback cbD,
                        DayEntity de) {

        if(!isInitialized) return false;

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);
        dialog.setTitle(dialogTitleId /*R.string.add_employee_btn*/);
        LinearLayout dialogView =
                (LinearLayout) LayoutInflater.from(ctx)
                        .inflate(dialogLayoutId /*R.layout.dialog_employee_picker*/, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        EditText editTextHours = dialogView.findViewById(hoursEditTextId);
        EditText editTextPrice = dialogView.findViewById(priceEditTextId);
        Button buttonConfirm = dialogView.findViewById(buttonConfirmId);
        Button buttonDelete = dialogView.findViewById(buttonDeleteId);
        Button buttonCancel = dialogView.findViewById(buttonCancelId);

        if (de != null) {
            editTextHours.setText(String.valueOf(de.hours));
            editTextPrice.setText(String.valueOf(de.price));
        }

        // Set button actions
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hours = editTextHours.getText().toString().trim();
                String price = editTextPrice.getText().toString().trim();
                if (!hours.isEmpty() && !price.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Year entered: " + year, Toast.LENGTH_SHORT).show();
                    de.hours = Double.valueOf(hours);
                    de.price = Double.valueOf(price);
                    cb.onComplete(de);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ctx, "Please enter hours and price", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbD.onComplete(de);
                dialog.dismiss();
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
