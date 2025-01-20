package com.svo7777777.hourscalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svo7777777.dialogs.YearDialog;
import com.svo7777777.hc_database.YearEntity;
import com.svo7777777.utils.DatabaseHandler;

import java.util.List;

public class YearsActivity extends AppCompatActivity {

    private DatabaseHandler dbh = null;
    private List<YearEntity> years;
    private long employeeId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_years);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        dbh = new DatabaseHandler(getApplicationContext());

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employeeId", -1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.years), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateYearsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Replace 'menu_main' with your menu resource name
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_settings == id) {
            // Handle settings action
            Intent intent = new Intent(YearsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.action_about == id) {
            // Handle about action
            Intent intent = new Intent(YearsActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> startActivityForResult =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK) {
                        updateYearsList();
                    }
                }
            });

    public void addYearClickHandler(View view) {
        YearDialog ed = new YearDialog(
                R.string.add_year_btn, R.layout.dialog_year_picker,
                R.id.editTextYear,
                R.id.buttonConfirm, R.id.buttonCancel);

        ed.open(this, this::addYearToYears, "");
    }

    private void addYearToYears(String year) {
        YearEntity ye = new YearEntity();
        ye.year = Integer.parseInt(year);
        ye.employeeId = (int)employeeId;
        Long empId = dbh.insertYear(ye);
        updateYearsList();
    }

    private void updateYearsList() {
        LinearLayout ec = findViewById(R.id.years_container);

        ec.clearDisappearingChildren();

        years = dbh.getYears((int)employeeId);

        for (int i = ec.getChildCount() - 1; i >= 0; i--) {
            View child = ec.getChildAt(i);
            ec.removeView(child);
        }

        for (YearEntity ye : years) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View newEmployeeItem = inflater.inflate(R.layout.item_year_employee, ec, false);

            double hours = dbh.getHours(ye.id);
            double salary = dbh.getSalary(ye.id);

            // Create a new Button
            Button newEmplButton = newEmployeeItem.findViewById(R.id.item_button);
            newEmplButton.setText(String.valueOf(ye.year) + " : " +
                    String.format("%.2f", hours) + " - " + String.format("%.2f", salary));

            ImageButton newEmplEditButton = newEmployeeItem.findViewById(R.id.edit_button);
            ImageButton newEmplDeleteButton = newEmployeeItem.findViewById(R.id.delete_button);

            // Set click listener for the new button
            newEmplButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(YearsActivity.this, MonthsActivity.class);
                    intent.putExtra("yearId", ye.id);
                    intent.putExtra("year", ye.year);
                    startActivityForResult.launch(intent);
                }
            });

            newEmplEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YearDialog ed = new YearDialog(
                            R.string.add_year_btn, R.layout.dialog_year_picker,
                            R.id.editTextYear,
                            R.id.buttonConfirm, R.id.buttonCancel);

                    ed.open(YearsActivity.this,
                            (String yr) -> {
                                ye.year = Integer.parseInt(yr);
                                Long yrId = dbh.updateYear(ye);
                                updateYearsList();
                            }, String.valueOf(ye.year));
                }
            });

            newEmplDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double hours = dbh.getHours(ye.id);

                    if(hours == 0) {
                        dbh.deleteYear(ye);
                        updateYearsList();
                    } else {
                        Toast.makeText(YearsActivity.this,
                                R.string.error_delete_year, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ec.addView(newEmployeeItem);
        }
    }


}