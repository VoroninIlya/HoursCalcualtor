package com.svo7777777.hourscalculator;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svo7777777.dialogs.EmployeeDialog;
import com.svo7777777.hc_database.EmployeeEntity;
import com.svo7777777.hc_database.SettingsEntity;
import com.svo7777777.hc_database.YearEntity;
import com.svo7777777.utils.DatabaseHandler;
import com.svo7777777.views.ItemButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler dbh = null;
    private List<EmployeeEntity> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        dbh = new DatabaseHandler(getApplicationContext());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateEmployeesList();
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.action_about == id) {
            // Handle about action
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

       return super.onOptionsItemSelected(item);
    }

    public void addEmployeeClickHandler(View view) {
        EmployeeDialog ed = new EmployeeDialog();

        ed.open(this, this::addEmployeeToEmployees,
                "", "", "", "", "");
    }

    private void addEmployeeToEmployees(String lastName, String firstName, String age,
                                        String hours, String price) {

        EmployeeEntity ee = new EmployeeEntity();
        ee.lastName = lastName;
        ee.firstName = firstName;
        ee.age = Integer.parseInt(age);

        long empId = dbh.writeEmployee(ee);

        if(!hours.isEmpty() || !price.isEmpty()) {
            SettingsEntity se = new SettingsEntity();
            se.employeeId = (int)empId;
            se.hours = hours.isEmpty() ? 0.0 : Double.parseDouble(hours);
            se.price = price.isEmpty() ? 0.0 : Double.parseDouble(price);
            long stId = dbh.updateSettings(se);
        }

        updateEmployeesList();
    }

    private void updateEmployeesList() {
        LinearLayout ec = findViewById(R.id.employees_container);
        ec.clearDisappearingChildren();

        employees = dbh.getEmployees();

        for (int i = ec.getChildCount() - 1; i >= 0; i--) {
            View child = ec.getChildAt(i);
            ec.removeView(child);
        }

        for (EmployeeEntity ee : employees) {
            // Inflate the custom LinearLayout from XML
            LayoutInflater inflater = LayoutInflater.from(this);
            View newEmployeeItem = inflater.inflate(R.layout.item_year_employee, ec, false);
            ItemButton newEmplButton = newEmployeeItem.findViewById(R.id.item_button);
            newEmplButton.setText(ee.lastName + " " + ee.firstName);
            newEmplButton.setTopRightText(String.valueOf(ee.age));
            newEmplButton.setSubTextSize(34f);
            newEmplButton.setTextSize(16f);

            ImageButton newEmplEditButton = newEmployeeItem.findViewById(R.id.edit_button);
            ImageButton newEmplDeleteButton = newEmployeeItem.findViewById(R.id.delete_button);

            // Set click listener for the new button
            newEmplButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, YearsActivity.class);
                    intent.putExtra("employeeId", ee.id);
                    startActivity(intent);
                }
            });
            final SettingsEntity[] se = {dbh.getSettings(ee.id)};

            newEmplEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EmployeeDialog ed = new EmployeeDialog();

                    ed.open(MainActivity.this,
                        (String ln, String fn, String ag, String h, String p) -> {
                            ee.lastName = ln;
                            ee.firstName = fn;
                            ee.age = Integer.parseInt(ag);
                            long empId = dbh.updateEmployee(ee);

                            if(!h.isEmpty() || !p.isEmpty()) {
                                if(se[0] != null) {
                                    se[0].hours = Double.parseDouble(h);
                                    se[0].price = Double.parseDouble(p);
                                    long stId = dbh.updateSettings(se[0]);
                                } else {
                                    se[0] = new SettingsEntity();
                                    se[0].employeeId = (int)empId;
                                    se[0].hours = Double.parseDouble(h);
                                    se[0].price = Double.parseDouble(p);
                                    long stId = dbh.writeSettings(se[0]);
                                }
                            } else {
                                if(se[0] != null) {
                                    dbh.deleteSettings(se[0]);
                                }
                            }

                            updateEmployeesList();
                        }, ee.lastName, ee.firstName, String.valueOf(ee.age),
                            se[0] != null ? String.valueOf(se[0].hours) : "",
                            se[0] != null ? String.valueOf(se[0].price) : "");
                }
            });

            newEmplDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<YearEntity> years = dbh.getYears(ee.id);

                    if(years == null || years.size() == 0) {
                        dbh.deleteEmployee(ee);
                        updateEmployeesList();
                    } else {
                        Toast.makeText(MainActivity.this,
                                R.string.error_delete_employee, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ec.addView(newEmployeeItem);
        }
    }
}