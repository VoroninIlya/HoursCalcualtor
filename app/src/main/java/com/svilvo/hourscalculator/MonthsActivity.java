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
import android.widget.LinearLayout;
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
import com.svilvo.hc_database.EmployeeEntity;
import com.svilvo.utils.DatabaseHandler;

public class MonthsActivity extends AppCompatActivity {

    private DatabaseHandler dbh = null;
    private int employeeId = -1;
    private int yearId = -1;
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
        yearId = intent.getIntExtra("yearId", -1);
        year = intent.getIntExtra("year", -1);

        dbh = new DatabaseHandler(getApplicationContext());

        TextView path = findViewById(R.id.path);

        EmployeeEntity ee = dbh.getEmployee((int)employeeId);

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

        for(int i = 0; i < 12; i++) {
            double monthHours = dbh.getHours(yearId, i);
            double monthSalary = dbh.getSalary(yearId, i);

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

            int mon = i;
            newEmplButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
                    intent.putExtra("employeeId", employeeId);
                    intent.putExtra("yearId", yearId);
                    intent.putExtra("year", year);
                    intent.putExtra("month", mon);
                    startActivityForResult.launch(intent);
                }
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
        }

        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> startActivityForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                int month = intent.getIntExtra("month", -1);

                String[] months = getResources().getStringArray(R.array.months);

                double hours = dbh.getHours(yearId, month);
                double salary = dbh.getSalary(yearId, month);

                CardView newEmplInfo = items[month].findViewById(R.id.item_info);

                TextView ttlt = newEmplInfo.findViewById(R.id.titleLeftTop);
                ttlt.setText(MonthsActivity.this.getString(R.string.hours) + ":");
                TextView vlt = newEmplInfo.findViewById(R.id.valueLeftTop);
                vlt.setText(String.format("%.2f", hours));
                TextView ttct = newEmplInfo.findViewById(R.id.titleCenterTop);
                ttct.setText(MonthsActivity.this.getString(R.string.salary) + ":");
                TextView vct = newEmplInfo.findViewById(R.id.valueCenterTop);
                vct.setText(String.format("%.2f", salary));
                
                Intent resintent = new Intent();
                setResult(RESULT_OK, resintent);
            }

            SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
            boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

            if(!tutorialShown) {
                showTutorial();
            }
        }
    });

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
                        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
                        intent.putExtra("employeeId", employeeId);
                        intent.putExtra("yearId", yearId);
                        intent.putExtra("year", year);
                        intent.putExtra("month", month);
                        startActivityForResult.launch(intent);
                    }
                }
        );
    }
}