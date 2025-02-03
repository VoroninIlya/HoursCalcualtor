package com.svilvo.hourscalculator;

import android.app.Activity;
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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.svilvo.dialogs.YearDialog;
import com.svilvo.hc_database.EmployeeEntity;
import com.svilvo.hc_database.YearEntity;
import com.svilvo.utils.DatabaseHandler;

import java.util.List;

public class YearsActivity extends AppCompatActivity {

    private DatabaseHandler dbh = null;
    private List<YearEntity> years;
    private EmployeeEntity ee = null;
    private long employeeId = -1;

    private YearEntity lastAddedYear = null;
    private int indexOfLastAddedYear = -1;
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

        TextView path = findViewById(R.id.path);

        ee = dbh.getEmployee((int)employeeId);

        path.setText(ee.lastName + " " + ee.firstName +
                ", " + ee.age);
        path.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.years), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateYearsList();

        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            LinearLayout yc = findViewById(R.id.years_container);
            if(yc.getChildCount() > 0)
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
            Intent intent = new Intent(YearsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (R.id.action_about == id) {
            // Handle about action
            Intent intent = new Intent(YearsActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (R.id.action_tutorial == id) {
            SharedPreferences.Editor editor = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE).edit();
            editor.putBoolean("tutorial_shown", false);
            editor.apply();

            showTutorial(1);
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

                    SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
                    boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

                    if(!tutorialShown) {
                        LinearLayout yc = findViewById(R.id.years_container);
                        if(yc.getChildCount() > 0)
                            showTutorial(2);
                        else
                            showTutorial(1);
                    }
                }
            });

    public void addYearClickHandler(View view) {
        YearDialog ed = new YearDialog();

        ed.open(this, this::addYearToYears, this::dialogDismissHandler, "");
    }

    private void addYearToYears(String year) {
        lastAddedYear = new YearEntity();
        lastAddedYear.year = Integer.parseInt(year);
        lastAddedYear.employeeId = (int)employeeId;
        long empId = dbh.insertYear(lastAddedYear);
        lastAddedYear.id = (int)empId;
        updateYearsList();
    }

    private void dialogDismissHandler() {
        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            showTutorial(2);
        }
    }

    private void updateYearsList() {
        LinearLayout ec = findViewById(R.id.years_container);

        ec.clearDisappearingChildren();

        years = dbh.getYears((int)employeeId);

        for (int i = ec.getChildCount() - 1; i >= 0; i--) {
            View child = ec.getChildAt(i);
            ec.removeView(child);
        }

        int i = 0;
        for (YearEntity ye : years) {

            if (lastAddedYear != null && lastAddedYear.id == ye.id)
                indexOfLastAddedYear = i;
            i++;

            LayoutInflater inflater = LayoutInflater.from(this);
            View newEmployeeItem = inflater.inflate(R.layout.item_card, ec, false);

            CardView newEmplButton = newEmployeeItem.findViewById(R.id.item_button);

            TextView tlt = newEmployeeItem.findViewById(R.id.textLeftTop);
            tlt.setText(ee.lastName);
            TextView tlb = newEmployeeItem.findViewById(R.id.textLeftBottom);
            tlb.setText(ee.firstName + ", " + ee.age);
            TextView tc = newEmployeeItem.findViewById(R.id.textCenter);
            tc.setText(String.valueOf(ye.year));
            TextView trt = newEmployeeItem.findViewById(R.id.textRightTop);
            trt.setText(""); trt.setVisibility(View.INVISIBLE);
            TextView trb = newEmployeeItem.findViewById(R.id.textRightBottom);
            trb.setText(""); trb.setVisibility(View.INVISIBLE);

            CardView newEmplInfo = newEmployeeItem.findViewById(R.id.item_info);

            double hours = dbh.getHours(ye.id);
            double salary = dbh.getSalary(ye.id);

            TextView ttlt = newEmplInfo.findViewById(R.id.titleLeftTop);
            ttlt.setText(this.getString(R.string.hours) + ":");
            TextView vlt = newEmplInfo.findViewById(R.id.valueLeftTop);
            vlt.setText(String.format("%.2f", hours));
            TextView ttct = newEmplInfo.findViewById(R.id.titleCenterTop);
            ttct.setText(this.getString(R.string.salary) + ":");
            TextView vct = newEmplInfo.findViewById(R.id.valueCenterTop);
            vct.setText(String.format("%.2f", salary));

            ImageButton moreButton = newEmployeeItem.findViewById(R.id.more_button);

            // Set click listener for the new button
            newEmplButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(YearsActivity.this, MonthsActivity.class);
                    intent.putExtra("employeeId", ye.employeeId);
                    intent.putExtra("yearId", ye.id);
                    intent.putExtra("year", ye.year);
                    startActivityForResult.launch(intent);
                }
            });

            moreButton.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(YearsActivity.this, v);
                menu.getMenuInflater().inflate(R.menu.menu_item, menu.getMenu());

                menu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (R.id.action_edit == id) {
                        YearDialog ed = new YearDialog();

                        ed.open(YearsActivity.this,
                                (String yr) -> {
                                    ye.year = Integer.parseInt(yr);
                                    Long yrId = dbh.updateYear(ye);
                                    updateYearsList();
                                }, this::dialogDismissHandler, String.valueOf(ye.year));
                        return true;
                    }
                    else if (R.id.action_delete == id) {
                        double h = dbh.getHours(ye.id);

                        if(h == 0) {
                            dbh.deleteYear(ye);
                            updateYearsList();
                        } else {
                            Toast.makeText(YearsActivity.this,
                                    R.string.error_delete_year, Toast.LENGTH_SHORT).show();
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
        LinearLayout yc = findViewById(R.id.years_container);
        if (part == 2 && yc.getChildCount() > 0) {

            years = dbh.getYears((int)employeeId);
            YearEntity ye = years.get(indexOfLastAddedYear >= 0 ? indexOfLastAddedYear : 0);

            View newYearItem = yc.getChildAt(indexOfLastAddedYear >= 0 ? indexOfLastAddedYear : 0);

            TapTargetView.showFor(this,
                TapTarget.forView(newYearItem,
                        getString(R.string.tutorial_year_item_title),
                        getString(R.string.tutorial_year_item_description))
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
                        Intent intent = new Intent(YearsActivity.this, MonthsActivity.class);
                        intent.putExtra("employeeId", ye.employeeId);
                        intent.putExtra("yearId", ye.id);
                        intent.putExtra("year", ye.year);
                        startActivityForResult.launch(intent);
                    }
                }
            );
        } else {
            TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.fab_add),
                                getString(R.string.tutorial_add_year_title),
                                getString(R.string.tutorial_add_year_description))
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
                        addYearClickHandler(findViewById(R.id.fab_add));
                    }
                }
            );
        }
    }
}