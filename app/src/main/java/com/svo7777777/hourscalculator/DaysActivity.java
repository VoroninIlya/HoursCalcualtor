package com.svo7777777.hourscalculator;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svo7777777.dialogs.DayDialog;
import com.svo7777777.hc_database.DayEntity;
import com.svo7777777.hc_database.EmployeeEntity;
import com.svo7777777.hc_database.MonthEntity;
import com.svo7777777.hc_database.SettingsEntity;
import com.svo7777777.utils.DatabaseHandler;
import com.svo7777777.views.DayButton;
import com.svo7777777.views.ItemButton;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DaysActivity extends AppCompatActivity {
    private DatabaseHandler dbh = null;
    private int employeeId = -1;
    private int yearId = -1;
    private int year = -1;
    private int month = -1;
    private MonthEntity monthEntity = null;
    private int daysInMonth = 0;
    private Map<Integer, Integer> buttonsIds = new HashMap<>();
    private static final Calendar cldr = Calendar.getInstance(Locale.ROOT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_days);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employeeId", -1);
        yearId = intent.getIntExtra("yearId", -1);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);

        dbh = new DatabaseHandler(getApplicationContext());

        TextView path = findViewById(R.id.path);

        EmployeeEntity ee = dbh.getEmployee((int)employeeId);

        String[] months = getResources().getStringArray(R.array.months);

        path.setText(">" + ee.lastName + "_" + ee.firstName.substring(0,1) +
                "_" + ee.age + ">" + year + ">" + months[month] + ">");

        cldr.set(Calendar.YEAR, year);
        cldr.set(Calendar.MONTH, month);
        daysInMonth = cldr.getActualMaximum(Calendar.DAY_OF_MONTH);

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

        monthEntity = dbh.getMonth(yearId, month);
        List<DayEntity> days = null;
        if(monthEntity != null) {
            days = dbh.getDays(monthEntity.id);
        }

        for(int i = 1; i <= daysInMonth; i++) {

            DayEntity dayEntity = null;
            if(monthEntity != null)
                dayEntity = dbh.getDay(monthEntity.id, i);

            if(dayEntity == null) {
                dayEntity = new DayEntity();
                dayEntity.hours = 0;
                dayEntity.price = 0;
            }

            cldr.set(Calendar.DATE, i);
            int dayOfWeek = cldr.get(Calendar.DAY_OF_WEEK);

            String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week_short);

            LayoutInflater inflater = LayoutInflater.from(this);
            View newDayItem = inflater.inflate(R.layout.item_day, dc, false);
            // Create a Button programmatically
            DayButton button = newDayItem.findViewById(R.id.item_button);

            if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
                button.setIsWeekend(true);
            else
                button.setIsWeekend(false);

            if(dayEntity.hours > 0)
                button.setIsInsideDb(true);
            else
                button.setIsInsideDb(false);

            // Set a unique ID
            buttonsIds.put(i, View.generateViewId());
            newDayItem.setId(buttonsIds.get(i));

            button.setText(String.valueOf(dayEntity.hours));
            button.setTopLeftText(daysOfWeek[dayOfWeek-1].trim());
            button.setTopRightText(String.valueOf(i));
            button.setBottomRightText(String.format("%.2f", dayEntity.hours*dayEntity.price));

            int day = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SettingsEntity se = dbh.getSettings(employeeId);

                    if(se == null) {
                        se = dbh.getSettings();

                        if (se == null) {
                            se = new SettingsEntity();
                            se.hours = 8;
                            se.price = 0;
                            dbh.writeSettings(se);
                        }
                    }

                    if(monthEntity == null) {
                        monthEntity = new MonthEntity();
                        monthEntity.yearId = yearId;
                        monthEntity.month = month;
                        monthEntity.id = (int)dbh.writeMonth(monthEntity);
                    }
                    DayEntity dayEntity = dbh.getDay(monthEntity.id, day);
                    if(dayEntity == null) {
                        double hours = se.hours; // using default
                        double price = se.price; // using default

                        DayEntity de = new DayEntity();
                        de.monthId = monthEntity.id;
                        de.day = day;
                        de.hours = hours;
                        de.price = price;

                        dbh.writeDay(de);
                        button.setIsInsideDb(true);

                        button.setText(String.valueOf(hours));
                        button.setTopLeftText(daysOfWeek[dayOfWeek-1].trim());
                        button.setTopRightText(String.valueOf(day));
                        button.setBottomRightText(String.format("%.2f", hours*price));

                        Intent intent = new Intent();
                        intent.putExtra("month", month);
                        setResult(RESULT_OK, intent);
                    } else {
                        DayDialog ed = new DayDialog();

                        ed.open(DaysActivity.this, DaysActivity.this::updateDay,
                                DaysActivity.this::deleteDay, dayEntity);
                    }
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec((i-1)/numColumns); // Row index
            params.columnSpec = GridLayout.spec((i-1)%numColumns); // Column index
            params.width = itemWidthPx;
            params.height = itemWidthPx;
            newDayItem.setLayoutParams(params);

            dc.addView(newDayItem);
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

    private void updateDaysOnActivity() {
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

        String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week_short);

        for(int i = 1; i <= daysInMonth; i++) {

            DayEntity dayEntity = dbh.getDay(monthEntity.id, i);

            if(dayEntity == null) {
                dayEntity = new DayEntity();
                dayEntity.hours = 0;
                dayEntity.price = 0;
            }

            cldr.set(Calendar.DATE, i);
            int dayOfWeek = cldr.get(Calendar.DAY_OF_WEEK);

            View dayItem = findViewById(buttonsIds.get(i));
            DayButton button = dayItem.findViewById(R.id.item_button);
            button.setText(String.valueOf(dayEntity.hours));
            button.setTopLeftText(daysOfWeek[dayOfWeek-1].trim());
            button.setTopRightText(String.valueOf(i ));
            button.setBottomRightText(String.format("%.2f", dayEntity.hours*dayEntity.price));

            if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
                button.setIsWeekend(true);
            else
                button.setIsWeekend(false);

            if(dayEntity.hours > 0)
                button.setIsInsideDb(true);
            else
                button.setIsInsideDb(false);

            //GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            //params.rowSpec = GridLayout.spec((i-1)/numColumns); // Row index
            //params.columnSpec = GridLayout.spec((i-1)%numColumns); // Column index
            //params.width = itemWidthPx;
            //params.height = itemWidthPx;
            //dayItem.setLayoutParams(params);
        }

        Intent intent = new Intent();
        intent.putExtra("month", month);
        setResult(RESULT_OK, intent);
    }

    private void updateDay(DayEntity day) {
        dbh.updateDay(day);
        updateDaysOnActivity();
    }

    private void deleteDay(DayEntity day) {
        dbh.deleteDay(day);
        updateDaysOnActivity();
    }

}