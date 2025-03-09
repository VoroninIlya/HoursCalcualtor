package com.svilvo.hourscalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.utils.DatabaseHandler;

public class MonthsActivity extends AppCompatActivity {

    private DatabaseHandler dbh = null;
    private int employeeId = -1;
    private int year = -1;
    private FrameLayout[] items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_months);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employeeId", -1);
        year = intent.getIntExtra("year", -1);

        dbh = new DatabaseHandler(getApplicationContext());

        TextView path = findViewById(R.id.path);

        EmployeeEntity ee = dbh.getEmployee(employeeId);
        YearEntity ye = dbh.getYear(employeeId, year);

        path.setText(ee.lastName + " " + ee.firstName +
                ", " + ee.age + " : " + year);
        path.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.months), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] months = getResources().getStringArray(R.array.months);
        items = new FrameLayout[]{
                findViewById(R.id.january_btn_container),
                findViewById(R.id.february_btn_container),
                findViewById(R.id.march_btn_container),
                findViewById(R.id.april_btn_container),
                findViewById(R.id.may_btn_container),
                findViewById(R.id.june_btn_container),
                findViewById(R.id.july_btn_container),
                findViewById(R.id.august_btn_container),
                findViewById(R.id.september_btn_container),
                findViewById(R.id.october_btn_container),
                findViewById(R.id.november_btn_container),
                findViewById(R.id.december_btn_container)};

        TextView summary = findViewById(R.id.summary);

        double yearHours = 0;
        double yearSalary = 0;
        if(ye != null) {
            dbh.getHours(ye.id);
            dbh.getSalary(ye.id);
        }

        summary.setText(getResources().getText(R.string.hours) + ": " +
                yearHours + " " +
                getResources().getText(R.string.salary) + ": " +
                yearSalary);

        for(int i = 0; i < 12; i++) {
            double monthHours = 0;
            double monthSalary = 0;
            if(ye != null) {
                monthHours = dbh.getHours(ye.id, i);
                monthSalary = dbh.getSalary(ye.id, i);
            }

            CardView newEmplButton = items[i].findViewById(R.id.item_button);

            TextView tlt = items[i].findViewById(R.id.textLeftTop);
            tlt.setText(ee.lastName);
            TextView tlb = items[i].findViewById(R.id.textLeftBottom);
            tlb.setText(ee.firstName + ", " + ee.age);
            TextView tc = items[i].findViewById(R.id.textCenter);
            tc.setText(months[i]);
            TextView trt = items[i].findViewById(R.id.textRightTop);
            trt.setText(String.valueOf(year));
            TextView trb = items[i].findViewById(R.id.textRightBottom);
            trb.setVisibility(View.INVISIBLE);

            CardView newEmplInfo = items[i].findViewById(R.id.item_info);

            TextView ttlt = newEmplInfo.findViewById(R.id.titleLeftTop);
            ttlt.setText(this.getString(R.string.hours) + ":");
            TextView vlt = newEmplInfo.findViewById(R.id.valueLeftTop);
            vlt.setText(String.format("%.2f", monthHours));
            TextView ttct = newEmplInfo.findViewById(R.id.titleCenterTop);
            ttct.setText(this.getString(R.string.salary) + ":");
            TextView vct = newEmplInfo.findViewById(R.id.valueCenterTop);
            vct.setText(String.format("%.2f", monthSalary));

            ImageButton moreButton = items[i].findViewById(R.id.more_button);
            moreButton.setVisibility(View.INVISIBLE);

            int month = i;
            int y = year;
            newEmplButton.setOnClickListener(v -> {
                Intent resintent = new Intent();
                resintent.putExtra("source", "months");
                resintent.putExtra("month", month);
                resintent.putExtra("year", y);
                setResult(RESULT_OK, resintent);
                finish();
            });
        }

        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            showTutorial();
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
            Intent intent = new Intent(MonthsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (R.id.action_about == id) {
            // Handle about action
            Intent intent = new Intent(MonthsActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (R.id.action_tutorial == id) {
            SharedPreferences.Editor editor = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE).edit();
            editor.putBoolean("tutorial_shown", false);
            editor.apply();

            showTutorial();
        } else if (R.id.action_report == id) {
            // Handle about action
            Intent intent = new Intent(MonthsActivity.this, ReportActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTutorial() {
        int month = 4;
        TapTargetView.showFor(this,
                TapTarget.forView(items[month],
                                getString(R.string.tutorial_month_item_title),
                                getString(R.string.tutorial_month_item_description))
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

                    }
                }
        );
    }
}