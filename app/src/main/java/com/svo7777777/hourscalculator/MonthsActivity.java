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

        double januaryHours = getHoursForMonthFromDb(yearId, 0);
        Button januaryBtn = findViewById(R.id.january_btn);
        januaryBtn.setText(getString(R.string.january_month) + " : " + String.valueOf(januaryHours));

        double februaryHours = getHoursForMonthFromDb(yearId, 1);
        Button februaryBtn = findViewById(R.id.february_btn);
        februaryBtn.setText(getString(R.string.february_month) + " : " + String.valueOf(februaryHours));

        double marchHours = getHoursForMonthFromDb(yearId, 2);
        Button marchBtn = findViewById(R.id.march_btn);
        marchBtn.setText(getString(R.string.march_month) + " : " + String.valueOf(marchHours));

        double aprilHours = getHoursForMonthFromDb(yearId, 3);
        Button aprilBtn = findViewById(R.id.april_btn);
        aprilBtn.setText(getString(R.string.april_month) + " : " + String.valueOf(aprilHours));

        double mayHours = getHoursForMonthFromDb(yearId, 4);
        Button mayBtn = findViewById(R.id.may_btn);
        mayBtn.setText(getString(R.string.may_month) + " : " + String.valueOf(mayHours));

        double juneHours = getHoursForMonthFromDb(yearId, 5);
        Button juneBtn = findViewById(R.id.june_btn);
        juneBtn.setText(getString(R.string.june_month) + " : " + String.valueOf(juneHours));

        double julyHours = getHoursForMonthFromDb(yearId, 6);
        Button julyBtn = findViewById(R.id.july_btn);
        julyBtn.setText(getString(R.string.july_month) + " : " + String.valueOf(julyHours));

        double augustHours = getHoursForMonthFromDb(yearId, 7);
        Button augustBtn = findViewById(R.id.august_btn);
        augustBtn.setText(getString(R.string.august_month) + " : " + String.valueOf(augustHours));

        double septemberHours = getHoursForMonthFromDb(yearId, 8);
        Button septemberBtn = findViewById(R.id.september_btn);
        septemberBtn.setText(getString(R.string.september_month) + " : " + String.valueOf(septemberHours));

        double octoberHours = getHoursForMonthFromDb(yearId, 9);
        Button octoberBtn = findViewById(R.id.october_btn);
        octoberBtn.setText(getString(R.string.october_month) + " : " + String.valueOf(octoberHours));

        double novemberHours = getHoursForMonthFromDb(yearId, 9);
        Button novemberBtn = findViewById(R.id.november_btn);
        novemberBtn.setText(getString(R.string.november_month) + " : " + String.valueOf(novemberHours));

        double decemberHours = getHoursForMonthFromDb(yearId, 9);
        Button decemberBtn = findViewById(R.id.december_btn);
        decemberBtn.setText(getString(R.string.december_month) + " : " + String.valueOf(decemberHours));
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

            if(result.getResultCode() == Activity.RESULT_OK){
                Intent intent = result.getData();
                long monthId = intent.getLongExtra("monthId", -1);
            }
            else{

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