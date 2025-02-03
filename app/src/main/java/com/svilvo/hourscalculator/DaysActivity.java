package com.svilvo.hourscalculator;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.svilvo.dialogs.DayDialog;
import com.svilvo.hc_database.DayEntity;
import com.svilvo.hc_database.EmployeeEntity;
import com.svilvo.hc_database.MonthEntity;
import com.svilvo.hc_database.SettingsEntity;
import com.svilvo.utils.DatabaseHandler;
import com.svilvo.views.DayButton;

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
    private Menu menu;

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

        path.setText(ee.lastName + " " + ee.firstName +
                ", " + ee.age + " : " + year + " " + months[month]);
        path.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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
                                DaysActivity.this::deleteDay, DaysActivity.this::dialogDismissHandler, dayEntity);
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

        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            showTutorial(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
        } else if (R.id.action_tutorial == id) {
            SharedPreferences.Editor editor = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE).edit();
            editor.putBoolean("tutorial_shown", false);
            editor.apply();

            showTutorial(1);
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogDismissHandler() {
        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            showTutorial(2);
        }
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

    private void showTutorial(int part) {
        if (part == 1) {
            int dayNbr = 12;
            GridLayout dc = findViewById(R.id.days_container);
            View day = dc.getChildAt(dayNbr);
            new TapTargetSequence(this)
                .targets(
                    TapTarget.forView(day,
                            getString(R.string.tutorial_day_item_title),
                            getString(R.string.tutorial_day_item_description))
                        .id(1)
                        .tintTarget(false)
                        .transparentTarget(true)
                        .targetRadius(50)
                        .outerCircleColor(R.color.notice_100)
                        .targetCircleColor(R.color.white)
                        .titleTextSize(20)
                        .descriptionTextSize(16)
                        .cancelable(false),
                    TapTarget.forView(day,
                            getString(R.string.tutorial_day_item2_title),
                            getString(R.string.tutorial_day_item2_description))
                        .id(2)
                        .tintTarget(false)
                        .transparentTarget(true)
                        .targetRadius(50)
                        .outerCircleColor(R.color.notice_100)
                        .targetCircleColor(R.color.white)
                        .titleTextSize(20)
                        .descriptionTextSize(16)
                        .cancelable(false)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        int targetId = lastTarget.id();
                        DayEntity dayEntity = null;

                        if(monthEntity != null)
                            dayEntity = dbh.getDay(monthEntity.id, dayNbr+1);

                        if(targetClicked && targetId == 1) {
                            DayButton button = day.findViewById(R.id.item_button);

                            SettingsEntity se = dbh.getSettings(employeeId);

                            if (se == null) {
                                se = dbh.getSettings();

                                if (se == null) {
                                    se = new SettingsEntity();
                                    se.hours = 8;
                                    se.price = 0;
                                    dbh.writeSettings(se);
                                }
                            }

                            if (monthEntity == null) {
                                monthEntity = new MonthEntity();
                                monthEntity.yearId = yearId;
                                monthEntity.month = month;
                                monthEntity.id = (int) dbh.writeMonth(monthEntity);
                            }

                            if (dayEntity == null) {
                                double hours = se.hours; // using default
                                double price = se.price; // using default

                                DayEntity de = new DayEntity();
                                de.monthId = monthEntity.id;
                                de.day = dayNbr+1;
                                de.hours = hours;
                                de.price = price;

                                dbh.writeDay(de);
                                button.setIsInsideDb(true);

                                button.setText(String.valueOf(hours));
                                button.setBottomRightText(String.format("%.2f", hours * price));

                                Intent intent = new Intent();
                                intent.putExtra("month", month);
                                setResult(RESULT_OK, intent);
                            }
                        }
                        else if(targetClicked && targetId == 2) {
                            DayDialog ed = new DayDialog();

                            ed.open(DaysActivity.this, DaysActivity.this::updateDay,
                                    DaysActivity.this::deleteDay, DaysActivity.this::dialogDismissHandler, dayEntity);
                        }
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                }
            ).start();
        } else if (part == 2) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.post(new Runnable() {
                @Override
                public void run() {
                    if(menu != null) {
                        MenuItem overflowItem = menu.findItem(R.id.action_tutorial);
                        if (overflowItem != null) {
                            TapTargetView.showFor(DaysActivity.this,
                                TapTarget.forToolbarOverflow(toolbar,
                                        getString(R.string.tutorial_overflow_title),
                                        getString(R.string.tutorial_overflow_description))
                                    .id(3)
                                    .tintTarget(false)
                                    .transparentTarget(true)
                                    .targetRadius(50)
                                    .outerCircleColor(R.color.notice_100)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .cancelable(false),
                                new TapTargetView.Listener() {
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);
                                        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("tutorial_shown", true);
                                        editor.apply();
                                    }
                                }
                            );
                        }
                    }
                }
            });
        }
    }
}