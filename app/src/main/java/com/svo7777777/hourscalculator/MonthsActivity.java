package com.svo7777777.hourscalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.svo7777777.hc_database.AppDatabaseClient;
import com.svo7777777.hc_database.MonthEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MonthsActivity extends AppCompatActivity {

    private AppDatabaseClient dbc;
    private int yearId = -1;
    private int year = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_months);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        yearId = intent.getIntExtra("yearId", -1);
        year = intent.getIntExtra("year", -1);

        dbc = AppDatabaseClient.getInstance(getApplicationContext());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.months), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] months = getResources().getStringArray(R.array.months);

        double januaryHours = getHoursForMonthFromDb(yearId, 0);
        Button januaryBtn = findViewById(R.id.january_btn);
        januaryBtn.setText(months[0] + " : " + String.format("%.2f", januaryHours));

        double februaryHours = getHoursForMonthFromDb(yearId, 1);
        Button februaryBtn = findViewById(R.id.february_btn);
        februaryBtn.setText(months[1] + " : " + String.format("%.2f", februaryHours));

        double marchHours = getHoursForMonthFromDb(yearId, 2);
        Button marchBtn = findViewById(R.id.march_btn);
        marchBtn.setText(months[2] + " : " + String.format("%.2f", marchHours));

        double aprilHours = getHoursForMonthFromDb(yearId, 3);
        Button aprilBtn = findViewById(R.id.april_btn);
        aprilBtn.setText(months[3] + " : " + String.format("%.2f", aprilHours));

        double mayHours = getHoursForMonthFromDb(yearId, 4);
        Button mayBtn = findViewById(R.id.may_btn);
        mayBtn.setText(months[4] + " : " + String.format("%.2f", mayHours));

        double juneHours = getHoursForMonthFromDb(yearId, 5);
        Button juneBtn = findViewById(R.id.june_btn);
        juneBtn.setText(months[5] + " : " + String.format("%.2f", juneHours));

        double julyHours = getHoursForMonthFromDb(yearId, 6);
        Button julyBtn = findViewById(R.id.july_btn);
        julyBtn.setText(months[6] + " : " + String.format("%.2f", julyHours));

        double augustHours = getHoursForMonthFromDb(yearId, 7);
        Button augustBtn = findViewById(R.id.august_btn);
        augustBtn.setText(months[7] + " : " + String.format("%.2f", augustHours));

        double septemberHours = getHoursForMonthFromDb(yearId, 8);
        Button septemberBtn = findViewById(R.id.september_btn);
        septemberBtn.setText(months[8] + " : " + String.format("%.2f", septemberHours));

        double octoberHours = getHoursForMonthFromDb(yearId, 9);
        Button octoberBtn = findViewById(R.id.october_btn);
        octoberBtn.setText(months[9] + " : " + String.format("%.2f", octoberHours));

        double novemberHours = getHoursForMonthFromDb(yearId, 10);
        Button novemberBtn = findViewById(R.id.november_btn);
        novemberBtn.setText(months[10] + " : " + String.format("%.2f", novemberHours));

        double decemberHours = getHoursForMonthFromDb(yearId, 11);
        Button decemberBtn = findViewById(R.id.december_btn);
        decemberBtn.setText(months[11]+ " : " + String.format("%.2f", decemberHours));
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
        }
        else if (R.id.action_about == id) {
            // Handle about action
            Intent intent = new Intent(MonthsActivity.this, AboutActivity.class);
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
                Intent intent = result.getData();
                int month = intent.getIntExtra("month", -1);

                String[] months = getResources().getStringArray(R.array.months);

                double hours = getHoursForMonthFromDb(yearId, month);
                Button btn;
                switch (month) {
                    case -1:
                        break;
                    case 0:
                        btn = findViewById(R.id.january_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 1:
                        btn = findViewById(R.id.february_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 2:
                        btn = findViewById(R.id.march_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 3:
                        btn = findViewById(R.id.april_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 4:
                        btn = findViewById(R.id.may_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 5:
                        btn = findViewById(R.id.june_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 6:
                        btn = findViewById(R.id.july_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 7:
                        btn = findViewById(R.id.august_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 8:
                        btn = findViewById(R.id.september_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 9:
                        btn = findViewById(R.id.october_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 10:
                        btn = findViewById(R.id.november_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    case 11:
                        btn = findViewById(R.id.december_btn);
                        btn.setText(months[month] + " : " + String.format("%.2f", hours));
                        break;
                    default:
                        break;
                }
                Intent resintent = new Intent();
                setResult(RESULT_OK, resintent);
            }
        }
    });

    public void januaryClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 0);
        mStartForResult.launch(intent);
    }

    public void februaryClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 1);
        mStartForResult.launch(intent);
    }

    public void marchClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 2);
        mStartForResult.launch(intent);
    }

    public void aprilClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 3);
        mStartForResult.launch(intent);
    }

    public void mayClickHandler(View viewe) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 4);
        mStartForResult.launch(intent);
    }

    public void juneClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 5);
        mStartForResult.launch(intent);
    }

    public void julyClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 6);
        mStartForResult.launch(intent);
    }

    public void augustClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 7);
        mStartForResult.launch(intent);
    }

    public void septemberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 8);
        mStartForResult.launch(intent);
    }

    public void octoberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 9);
        mStartForResult.launch(intent);
    }

    public void novemberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 10);
        mStartForResult.launch(intent);
    }

    public void decemberClickHandler(View view) {
        Intent intent = new Intent(MonthsActivity.this, DaysActivity.class);
        intent.putExtra("yearId", yearId);
        intent.putExtra("year", year);
        intent.putExtra("month", 11);
        mStartForResult.launch(intent);
    }

    private double getHoursForMonthFromDb(int yearId, int month){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        double hours = 0.0;
        try {
            Future<Double> hoursFuture = executorService.submit(() -> {
                double h = dbc.getAppDatabase().monthDao().getHoursForMonth(yearId, month);

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