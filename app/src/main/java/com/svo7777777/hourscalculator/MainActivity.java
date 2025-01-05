package com.svo7777777.hourscalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svo7777777.dialogs.EmployeeDialog;
import com.svo7777777.hc_database.AppDatabaseClient;
import com.svo7777777.hc_database.EmployeeEntity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private AppDatabaseClient dbc;
    private List<EmployeeEntity> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbc = AppDatabaseClient.getInstance(getApplicationContext());
        updateEmployeesList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Replace 'menu_main' with your menu resource name
        return true;
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
        Long empId = writeEmployeesToDb(lastName, firstName, age);
        updateEmployeesList();
    }

    private void updateEmployeesList() {
        GridLayout ec = findViewById(R.id.employees_container);
        ec.setColumnCount(3);
        ec.clearDisappearingChildren();

        employees = getEmployeesFromDb();
        long employeesCount = 1;

        for (int i = ec.getChildCount() - 1; i >= 0; i--) {
            View child = ec.getChildAt(i);
            ec.removeView(child);
        }

        for (EmployeeEntity ee : employees) {
            // Create LayoutParams for the button
            GridLayout.LayoutParams emplButtonParams = new GridLayout.LayoutParams();
            emplButtonParams.width = 0;
            emplButtonParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            emplButtonParams.columnSpec = GridLayout.spec( 0, 1, 5f);
            emplButtonParams.rowSpec = GridLayout.spec((int)employeesCount, 1, 1f);

            GridLayout.LayoutParams emplEditButtonParams = new GridLayout.LayoutParams();
            emplEditButtonParams.width = 0;
            emplEditButtonParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            emplEditButtonParams.columnSpec = GridLayout.spec( 1, 1, 1f);
            emplEditButtonParams.rowSpec = GridLayout.spec((int)employeesCount, 1, 1f);

            GridLayout.LayoutParams emplDeleteButtonParams = new GridLayout.LayoutParams();
            emplDeleteButtonParams.width = 0;
            emplDeleteButtonParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            emplDeleteButtonParams.columnSpec = GridLayout.spec( 2, 1, 1f);
            emplDeleteButtonParams.rowSpec = GridLayout.spec((int)employeesCount, 1, 1f);

            // Create a new Button
            Button newEmplButton = new Button(this);
            newEmplButton.setText(ee.lastName + " " + ee.firstName  + " (" + ee.age + ")");

            Button newEmplEditButton = new Button(this);
            newEmplEditButton.setText("E");
            Button newEmplDeleteButton = new Button(this);
            newEmplDeleteButton.setText("D");

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
                                Long empId = updateEmployeeInDb(ee, ln, fn, ag);
                                updateEmployeesList();
                            }, ee.lastName, ee.firstName, String.valueOf(ee.age));
                }
            });

            newEmplDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEmployeesFromDb(ee);
                    updateEmployeesList();
                }
            });

            newEmplButton.setLayoutParams(emplButtonParams);
            newEmplEditButton.setLayoutParams(emplEditButtonParams);
            newEmplDeleteButton.setLayoutParams(emplDeleteButtonParams);

            ec.addView(newEmplButton);
            ec.addView(newEmplEditButton);
            ec.addView(newEmplDeleteButton);

            employeesCount++;
        }
    }

    private List<EmployeeEntity> getEmployeesFromDb() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<EmployeeEntity> employees = null;
        try {
            Future<List<EmployeeEntity>> employeesFuture = executorService.submit(() -> {
                List<EmployeeEntity> empl = dbc.getAppDatabase().employeeDao().getAll();
                // Можно выполнить дополнительные действия, например, логгирование
                //runOnUiThread(() -> {
                //    // Обновите UI, если нужно
                //    Log.d("MainActivity", "User added successfully");
                //});

                return empl;
            });
            employees = employeesFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return employees;
    }

    private EmployeeEntity findEmployeeInDb(int id) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        EmployeeEntity employee = null;
        try {
            Future<EmployeeEntity> employeeFuture = executorService.submit(() -> {
                EmployeeEntity empl = dbc.getAppDatabase().employeeDao().findById(id);
                // Можно выполнить дополнительные действия, например, логгирование
                //runOnUiThread(() -> {
                //    // Обновите UI, если нужно
                //    Log.d("MainActivity", "User added successfully");
                //});

                return empl;
            });
            employee = employeeFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return employee;
    }

    private Long writeEmployeesToDb(String lastName, String firstName, String age) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() -> {
                EmployeeEntity e = new EmployeeEntity();
                e.lastName = lastName;
                e.firstName = firstName;
                e.age = Integer.parseInt(age);

                return dbc.getAppDatabase().employeeDao().insert(e);
            });
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    private Long updateEmployeeInDb(EmployeeEntity emplEntity, String lastName, String firstName, String age) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() -> {
                EmployeeEntity e = emplEntity;
                e.lastName = lastName;
                e.firstName = firstName;
                e.age = Integer.parseInt(age);

                return (long) dbc.getAppDatabase().employeeDao().update(e);
            });
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    private void deleteEmployeesFromDb(EmployeeEntity employee) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.execute(() -> {
                dbc.getAppDatabase().employeeDao().delete(employee);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
    }
}