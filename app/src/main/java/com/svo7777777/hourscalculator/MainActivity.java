package com.svo7777777.hourscalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.svo7777777.hc_database.YearEntity;
import com.svo7777777.utils.DatabaseHandler;

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
        EmployeeDialog ed = new EmployeeDialog(
                R.string.add_employee_btn, R.layout.dialog_employee_picker,
                R.id.editTextEmployeeLastName, R.id.editTextEmployeeFirstName,
                R.id.editTextEmployeeAge,
                R.id.buttonConfirm, R.id.buttonCancel);

        ed.open(this, this::addEmployeeToEmployees, "", "", "");
    }

    private void addEmployeeToEmployees(String lastName, String firstName, String age) {

        EmployeeEntity ee = new EmployeeEntity();
        ee.lastName = lastName;
        ee.firstName = firstName;
        ee.age = Integer.parseInt(age);

        Long empId = dbh.writeEmployee(ee);
        updateEmployeesList();
    }

    private void updateEmployeesList() {
        LinearLayout ec = findViewById(R.id.employees_container);
        ec.clearDisappearingChildren();

        employees = dbh.getEmployees();
        long employeesCount = 1;

        for (int i = ec.getChildCount() - 1; i >= 0; i--) {
            View child = ec.getChildAt(i);
            ec.removeView(child);
        }

        for (EmployeeEntity ee : employees) {
            // Inflate the custom LinearLayout from XML
            LayoutInflater inflater = LayoutInflater.from(this);
            View newEmployeeItem = inflater.inflate(R.layout.year_employee_item, ec, false);
            Button newEmplButton = newEmployeeItem.findViewById(R.id.item_button);
            newEmplButton.setText(ee.lastName + " " + ee.firstName  + " (" + ee.age + ")");

            Button newEmplEditButton = newEmployeeItem.findViewById(R.id.edit_button);
            Button newEmplDeleteButton = newEmployeeItem.findViewById(R.id.delete_button);

            // Set click listener for the new button
            newEmplButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, YearsActivity.class);
                    intent.putExtra("employeeId", ee.id);
                    startActivity(intent);
                }
            });

            newEmplEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EmployeeDialog ed = new EmployeeDialog(
                        R.string.add_employee_btn, R.layout.dialog_employee_picker,
                        R.id.editTextEmployeeLastName, R.id.editTextEmployeeFirstName,
                        R.id.editTextEmployeeAge,
                        R.id.buttonConfirm, R.id.buttonCancel);

                    ed.open(MainActivity.this,
                        (String ln, String fn, String ag) -> {
                            ee.lastName = ln;
                            ee.firstName = fn;
                            ee.age = Integer.parseInt(ag);
                            Long empId = dbh.updateEmployee(ee);
                            updateEmployeesList();
                        }, ee.lastName, ee.firstName, String.valueOf(ee.age));
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

            employeesCount++;
        }
    }
}