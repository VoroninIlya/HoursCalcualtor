package com.svo7777777.hourscalculator;

import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svo7777777.dialogs.DayDialog;
import com.svo7777777.hc_database.AppDatabaseClient;
import com.svo7777777.hc_database.DayEntity;
import com.svo7777777.hc_database.MonthEntity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DaysActivity extends AppCompatActivity {
    private AppDatabaseClient dbc;
    private int yearId = -1;
    private int year = -1;
    private int month = -1;
    private MonthEntity monthEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_days);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        yearId = intent.getIntExtra("yearId", -1);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);

        dbc = AppDatabaseClient.getInstance(getApplicationContext());

        monthEntity = getMonthFromDb(yearId, month);
        List<DayEntity> days = null;
        if(monthEntity != null) {
            days = getDaysFromDb(monthEntity.id);
        }

        Calendar cldr = Calendar.getInstance(Locale.ROOT);
        cldr.set(Calendar.YEAR, year);
        cldr.set(Calendar.MONTH, month);
        int daysInMonth = cldr.getActualMaximum(Calendar.DAY_OF_MONTH);

        GridLayout dc = findViewById(R.id.days_container);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        float itemWidthDp = 70; // Example item width in dp
        int numColumns = (int) (screenWidthDp / itemWidthDp);
        itemWidthDp = screenWidthDp/numColumns;

        // Convert itemWidthDp to pixels for layout parameters
        int itemWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                itemWidthDp,
                getResources().getDisplayMetrics()
        );

        dc.setColumnCount(numColumns);

        for(int i = 1; i <= daysInMonth; i++) {

            DayEntity dayEntity = new DayEntity();
            if(days != null) {
                for(DayEntity de : days) {
                    if(de.day == i) {
                        dayEntity = de;
                    }
                }
            }

            // Create a Button programmatically
            Button button = new Button(DaysActivity.this);
            button.setText(String.valueOf(i) + "\n" +
                    String.valueOf(dayEntity.hours) + " - " +
                    String.valueOf(dayEntity.hours*dayEntity.price));
            if(dayEntity.hours > 0)
                button.setBackgroundColor(Color.argb(255, 100, 100, 200));

            int day = i;

            DayEntity finalDayEntity = dayEntity;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(monthEntity == null) {
                        monthEntity = new MonthEntity();
                        monthEntity.yearId = yearId;
                        monthEntity.month = month;
                        monthEntity.id = (int)writeMonthToDb(yearId, month);
                    }

                    if(finalDayEntity.hours== 0) {
                        double hours = 8.0; // using default
                        double price = 0; // using default
                        writeDayToDb(monthEntity.id, day, hours, price);
                        button.setBackgroundColor(Color.argb(255, 100, 100, 200));
                        button.setText(String.valueOf(day) + "\n" + String.valueOf(hours) + " - " + String.valueOf(hours*price));
                    } else {
                        DayDialog ed = new DayDialog(
                                R.string.add_year_btn, R.layout.dialog_day_picker,
                                R.id.editTextHours, R.id.editTextPrice,
                                R.id.buttonConfirm, R.id.buttonDelete, R.id.buttonCancel);

                        ed.open(DaysActivity.this, DaysActivity.this::updateDay,
                                DaysActivity.this::deleteDay, finalDayEntity);
                    }
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec((i-1)/numColumns); // Row index
            params.columnSpec = GridLayout.spec((i-1)%numColumns); // Column index
            params.width = itemWidthPx;
            params.height = itemWidthPx;
            button.setLayoutParams(params);

            dc.addView(button);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.days), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            Intent intent = new Intent(DaysActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.action_about == id) {
            // Handle about action
            Intent intent = new Intent(DaysActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDay(String hours, String price) {

    }

    private void deleteDay(DayEntity day) {
        deleteDayFromDb(day);
    }

    private MonthEntity getMonthFromDb(int yearId, int month){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        MonthEntity monthRes = null;
        try {
            Future<MonthEntity> monthFuture = executorService.submit(() -> {
                MonthEntity empl = dbc.getAppDatabase().monthDao().getMonthForYear(yearId, month);

                return empl;
            });
            monthRes = monthFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return monthRes;
    }

    private long writeMonthToDb(int yearId, int month){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Long> idFuture = executorService.submit(() -> {
                MonthEntity m = new MonthEntity();
                m.month = month;
                m.yearId = yearId;

                return dbc.getAppDatabase().monthDao().insert(m);
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

    private List<DayEntity> getDaysFromDb(int monthId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<DayEntity> days = null;
        try {
            Future<List<DayEntity>> daysFuture = executorService.submit(() -> {
                List<DayEntity> empl = dbc.getAppDatabase().dayDao().getDaysForMonth((int)monthId);

                return empl;
            });
            days = daysFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return days;
    }

    private long writeDayToDb(int monthId, int day, double hours, double price){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Long> idFuture = executorService.submit(() -> {
                DayEntity d = new DayEntity();
                d.monthId = monthId;
                d.day = day;
                d.hours = hours;
                d.price = price;

                return dbc.getAppDatabase().dayDao().insert(d);
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

    private void deleteDayFromDb(DayEntity day) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Void> idFuture = (Future<Void>) executorService.submit(() -> {
                dbc.getAppDatabase().dayDao().delete(day);
            });
            idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
    }
}