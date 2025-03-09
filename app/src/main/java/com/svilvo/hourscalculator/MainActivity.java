package com.svilvo.hourscalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.svilvo.dialogs.EmployeeDialog;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.entities.SettingsEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.utils.DatabaseHandler;

import java.util.List;

import com.getkeepsafe.taptargetview.TapTarget;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler dbh = null;
    private List<EmployeeEntity> employees;
    private EmployeeEntity lastAddedEmployee = null;
    private int indexOfLastAddedEmployee = -1;

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

        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            LinearLayout ec = findViewById(R.id.employees_container);
            if(ec.getChildCount() > 0)
                showTutorial(2);
            else
                showTutorial(1);
        }
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
        } else if (R.id.action_tutorial == id) {
            SharedPreferences.Editor editor = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE).edit();
            editor.putBoolean("tutorial_shown", false);
            editor.apply();

            showTutorial(1);
        } else if (R.id.action_report == id) {
            // Handle about action
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
            return true;
        }

       return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> startActivityForResult =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
                boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

                if(!tutorialShown) {
                    LinearLayout ec = findViewById(R.id.employees_container);
                    if(ec.getChildCount() > 0)
                        showTutorial(2);
                    else
                        showTutorial(1);
                }
            });

    public void addEmployeeClickHandler(View view) {
        EmployeeDialog ed = new EmployeeDialog();

        ed.open(this, this::addEmployeeToEmployees, this::dialogDismissHandler,
                "", "", "", "", "");
    }

    private void addEmployeeToEmployees(String lastName, String firstName, String age,
                                        String hours, String price) {

        lastAddedEmployee = new EmployeeEntity();
        lastAddedEmployee.lastName = lastName;
        lastAddedEmployee.firstName = firstName;
        lastAddedEmployee.age = Integer.parseInt(age);

        long empId = dbh.writeEmployee(lastAddedEmployee);
        lastAddedEmployee.id = (int)empId;

        if(!hours.isEmpty() || !price.isEmpty()) {
            SettingsEntity se = dbh.getSettings((int)empId);
            if(se == null) {
                se = new SettingsEntity();
                se.employeeId = (int)empId;
                se.hours = hours.isEmpty() ? 0.0 : Double.parseDouble(hours);
                se.price = price.isEmpty() ? 0.0 : Double.parseDouble(price);
                long stId = dbh.writeSettings(se);
            } else {
                se.employeeId = (int)empId;
                se.hours = hours.isEmpty() ? 0.0 : Double.parseDouble(hours);
                se.price = price.isEmpty() ? 0.0 : Double.parseDouble(price);
                long stId = dbh.updateSettings(se);
            }
        }

        updateEmployeesList();
    }

    private void dialogDismissHandler() {
        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            showTutorial(2);
        }
    }

    private void updateEmployeesList() {
        LinearLayout ec = findViewById(R.id.employees_container);
        ec.clearDisappearingChildren();

        for (int i = ec.getChildCount() - 1; i >= 0; i--) {
            View child = ec.getChildAt(i);
            ec.removeView(child);
        }

        employees = dbh.getEmployees();

        if (employees == null) return;

        int i = 0;
        for (EmployeeEntity ee : employees) {

            if (lastAddedEmployee != null && lastAddedEmployee.id == ee.id)
                indexOfLastAddedEmployee = i;
            i++;

            // Inflate the custom LinearLayout from XML
            LayoutInflater inflater = LayoutInflater.from(this);
            View newEmployeeItem = inflater.inflate(R.layout.item_card, ec, false);

            CardView newEmplButton = newEmployeeItem.findViewById(R.id.item_button);

            TextView tc = newEmployeeItem.findViewById(R.id.textCenter);
            tc.setText(ee.lastName + " " + ee.firstName);
            TextView tlt = newEmployeeItem.findViewById(R.id.textLeftTop);
            tlt.setText(this.getString(R.string.age) + ": " + ee.age);
            TextView tlb = newEmployeeItem.findViewById(R.id.textLeftBottom);
            tlb.setVisibility(View.INVISIBLE);
            TextView trc = newEmployeeItem.findViewById(R.id.textRightTop);
            trc.setVisibility(View.INVISIBLE);
            TextView trb = newEmployeeItem.findViewById(R.id.textRightBottom);
            trb.setVisibility(View.INVISIBLE);

            CardView newEmplInfo = newEmployeeItem.findViewById(R.id.item_info);

            TextView ttlt = newEmplInfo.findViewById(R.id.titleLeftTop);
            ttlt.setText(""); ttlt.setVisibility(View.INVISIBLE);
            TextView vlt = newEmplInfo.findViewById(R.id.valueLeftTop);
            vlt.setText(""); vlt.setVisibility(View.INVISIBLE);
            TextView ttct = newEmplInfo.findViewById(R.id.titleCenterTop);
            ttct.setText(""); ttct.setVisibility(View.INVISIBLE);
            TextView vct = newEmplInfo.findViewById(R.id.valueCenterTop);
            vct.setText(""); vct.setVisibility(View.INVISIBLE);

            ImageButton moreButton = newEmployeeItem.findViewById(R.id.more_button);

            //// Set click listener for the new button
            newEmplButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaysActivity.class);
                intent.putExtra("employeeId", ee.id);
                startActivity(intent);
            });

            moreButton.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(MainActivity.this, v);
                menu.getMenuInflater().inflate(R.menu.menu_item, menu.getMenu());

                menu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (R.id.action_edit == id) {
                        SettingsEntity[] se = {dbh.getSettings(ee.id)};
                        EmployeeDialog ed = new EmployeeDialog();
                        ed.open(MainActivity.this,
                                (String ln, String fn, String ag, String h, String p) -> {
                            ee.lastName = ln;
                            ee.firstName = fn;
                            ee.age = Integer.parseInt(ag);
                            long empId = dbh.updateEmployee(ee);
                            if(!h.isEmpty() || !p.isEmpty()) {
                                if(se[0] != null) {
                                    se[0].hours = h.isEmpty() ? 0.0 : Double.parseDouble(h);
                                    se[0].price = p.isEmpty() ? 0.0 : Double.parseDouble(p);
                                    long stId = dbh.updateSettings(se[0]);
                                } else {
                                    se[0] = new SettingsEntity();
                                    se[0].employeeId = (int)ee.id;
                                    se[0].hours = h.isEmpty() ? 0.0 : Double.parseDouble(h);
                                    se[0].price = p.isEmpty() ? 0.0 : Double.parseDouble(p);
                                    long stId = dbh.writeSettings(se[0]);
                                }
                            } else {
                                if(se[0] != null) {
                                    dbh.deleteSettings(se[0]);
                                }
                            }
                            updateEmployeesList();
                            }, this::dialogDismissHandler, ee.lastName, ee.firstName, String.valueOf(ee.age),
                                se[0] != null ? String.valueOf(se[0].hours) : "",
                                se[0] != null ? String.valueOf(se[0].price) : "");
                        return true;
                    }
                    else if (R.id.action_delete == id) {
                        List<YearEntity> years = dbh.getYears(ee.id);
                        if(years == null || years.size() == 0) {
                            dbh.deleteEmployee(ee);

                            SettingsEntity se = dbh.getSettings(ee.id);
                            if(se != null)
                                dbh.deleteSettings(se);

                            updateEmployeesList();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    R.string.error_delete_employee, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    };
                    return false;
                });

                menu.show();
            });

            ec.addView(newEmployeeItem);
        }
    }

    private void showTutorial(int part) {
        LinearLayout ec = findViewById(R.id.employees_container);
        if (part == 2 && ec.getChildCount() > 0) {
            employees = dbh.getEmployees();
            EmployeeEntity ee = employees.get(indexOfLastAddedEmployee >= 0 ? indexOfLastAddedEmployee : 0);

            View newEmployeeItem = ec.getChildAt(indexOfLastAddedEmployee >= 0 ? indexOfLastAddedEmployee : 0);

        TapTargetView.showFor(this,
            TapTarget.forView(newEmployeeItem,
                    getString(R.string.tutorial_employee_item_title),
                    getString(R.string.tutorial_employee_item_description))
                .id(1)
                .tintTarget(false)
                .transparentTarget(true)
                .targetRadius(60)
                .outerCircleColor(R.color.notice_100)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .descriptionTextSize(16)
                .cancelable(false),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        Intent intent = new Intent(MainActivity.this, DaysActivity.class);
                        intent.putExtra("employeeId", ee.id);
                        startActivityForResult.launch(intent);
                    }
                }

        );
    } else {
        TapTargetView.showFor(this,
            TapTarget.forView(findViewById(R.id.fab_add),
                    getString(R.string.tutorial_add_employee_title),
                    getString(R.string.tutorial_add_employee_description))
                .id(1)
                .tintTarget(false)
                .transparentTarget(false)
                .outerCircleColor(R.color.notice_100)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .descriptionTextSize(16)
                .cancelable(false),
            new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    addEmployeeClickHandler(findViewById(R.id.fab_add));
                }
            }
        );
    }
    }
}