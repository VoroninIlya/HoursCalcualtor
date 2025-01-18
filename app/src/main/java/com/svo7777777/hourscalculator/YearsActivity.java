package com.svo7777777.hourscalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svo7777777.dialogs.EmployeeDialog;
import com.svo7777777.dialogs.YearDialog;
import com.svo7777777.hc_database.AppDatabaseClient;
import com.svo7777777.hc_database.EmployeeEntity;
import com.svo7777777.hc_database.YearEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class YearsActivity extends AppCompatActivity {

    private AppDatabaseClient dbc;
    private List<YearEntity> years;
    private long employeeId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_years);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employeeId", -1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.years), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbc = AppDatabaseClient.getInstance(getApplicationContext());
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

    ActivityResultLauncher<Intent> mStartForResult =
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
        Long empId = writeYearToDb(Integer.parseInt(year));
        updateYearsList();
    }

    private void updateYearsList() {
        LinearLayout ec = findViewById(R.id.years_container);

        ec.clearDisappearingChildren();

        years = getYearsFromDb();
        long yearsCount = 1;

        for (int i = ec.getChildCount() - 1; i >= 0; i--) {
            View child = ec.getChildAt(i);
            ec.removeView(child);
        }

        for (YearEntity ye : years) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View newEmployeeItem = inflater.inflate(R.layout.year_employee_item, ec, false);

            double hours = getHoursForYearFromDb(ye.id);

            // Create a new Button
            Button newEmplButton = newEmployeeItem.findViewById(R.id.item_button);
            newEmplButton.setText(String.valueOf(ye.year) + " : " + String.format("%.2f", hours));

            Button newEmplEditButton = newEmployeeItem.findViewById(R.id.edit_button);
            Button newEmplDeleteButton = newEmployeeItem.findViewById(R.id.delete_button);

            // Set click listener for the new button
            newEmplButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(YearsActivity.this, MonthsActivity.class);
                    intent.putExtra("yearId", ye.id);
                    intent.putExtra("year", ye.year);
                    mStartForResult.launch(intent);
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
                                Long yrId = updateYearInDb(ye, yr);
                                updateYearsList();
                            }, String.valueOf(ye.year));
                }
            });

            newEmplDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double hours = getHoursForYearFromDb(ye.id);

                    if(hours == 0) {
                        deleteYearFromDb(ye);
                        updateYearsList();
                    } else {
                        Toast.makeText(YearsActivity.this,
                                R.string.error_delete_year, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ec.addView(newEmployeeItem);

            yearsCount++;
        }
    }

    private List<YearEntity> getYearsFromDb() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<YearEntity> years = null;
        try {
            Future<List<YearEntity>> yearsFuture = executorService.submit(() -> {
                List<YearEntity> empl = dbc.getAppDatabase().yearDao().getYearsForEmployee((int)employeeId);

                return empl;
            });
            years = yearsFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return years;
    }
    private Long writeYearToDb(int year) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() -> {
                YearEntity e = new YearEntity();
                e.year = year;
                e.employeeId = (int)employeeId;
                return dbc.getAppDatabase().yearDao().insert(e);
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

    private Long updateYearInDb(YearEntity yearEntity, String year) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() -> {
                YearEntity e = yearEntity;
                e.year = Integer.parseInt(year);
                return (long) dbc.getAppDatabase().yearDao().update(e);
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

    private void deleteYearFromDb(YearEntity year) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.execute(() -> {
                dbc.getAppDatabase().yearDao().delete(year);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
    }

    private double getHoursForYearFromDb(int yearId){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        double hours = 0.0;
        try {
            Future<Double> hoursFuture = executorService.submit(() -> {
                double h = dbc.getAppDatabase().monthDao().getHoursForYear(yearId);

                return h;
            });
            hours = hoursFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return hours;
    }
}