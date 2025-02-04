package com.svilvo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.svilvo.hc_database.DayEntity;
import com.svilvo.hourscalculator.R;

public class DayDialog {
    public DayDialog() {}
    public boolean open(Context ctx, DayDialogCallback cb,
                        DayDeleteCallback cbD,
                        DayDialogDismissCallback dmcb,
                        DayEntity de) {

        // Create a custom dialog
        Dialog dialog = new Dialog(ctx);
        //dialog.setTitle(R.string.add_day_btn);
        LinearLayout dialogView =
                (LinearLayout) LayoutInflater.from(ctx)
                        .inflate(R.layout.dialog_day_picker, null);
        dialog.setContentView(dialogView);

        // Get references to the EditText and Buttons
        EditText editTextHours = dialogView.findViewById(R.id.editTextHours);
        EditText editTextPrice = dialogView.findViewById(R.id.editTextPrice);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);
        Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

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

        SharedPreferences prefs = ctx.getSharedPreferences("hours_calculator_tutorial", ctx.MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);
        if(!tutorialShown) {
            dialog.setOnShowListener(dialog1 -> showTutorial(ctx, dialog, cbD, de));
            buttonCancel.setEnabled(false);
            buttonConfirm.setEnabled(false);
        }

        dialog.setOnDismissListener(dialog1 -> dmcb.onComplete());
        // Show the dialog
        dialog.show();

        return true;
    }

    private void showTutorial(Context ctx, Dialog d, DayDeleteCallback cbD, DayEntity de) {
        TapTargetView.showFor(d,
            TapTarget.forView(d.findViewById(R.id.textView2),
                    ctx.getString(R.string.tutorial_day_dialog_title),
                    ctx.getString(R.string.tutorial_day_dialog_description))
                .id(3)
                .tintTarget(false)
                .transparentTarget(true)
                .targetRadius(150)
                .outerCircleColor(R.color.notice_100)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .descriptionTextSize(16)
                .cancelable(false),
            new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    cbD.onComplete(de);
                    d.dismiss();
                }
            }
        );
    }
}
