package com.svilvo.hourscalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.viewpager2.widget.ViewPager2;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.svilvo.adapters.MonthFragmentAdapter;
import com.svilvo.adapters.YearMonthAdapter;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.entities.MonthEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.hc_database.views.MonthSummary;
import com.svilvo.hc_database.views.YearSummary;
import com.svilvo.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DaysActivity extends AppCompatActivity {
    private ViewPager2 yearPager, monthPager, monthsViewPager;
    private DatabaseHandler dbh = null;
    private int employeeId = -1;
    private int year = -1;
    private int month = -1;
    private YearEntity currentYear = null;
    private MonthEntity currentMonth = null;
    YearSummary summary;
    private Menu menu;
    MonthFragmentAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_days);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use the toolbar as the app bar

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.days), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employeeId", -1);

        Calendar cldr = Calendar.getInstance();
        year = cldr.get(Calendar.YEAR);
        month = cldr.get(Calendar.MONTH);

        adapter = new MonthFragmentAdapter(
                this, employeeId, year,
                this::onSummaryUpdated, this::monthOnClick);

        yearPager = findViewById(R.id.year_pager);
        monthsViewPager = findViewById(R.id.months_viewpager);

        dbh = new DatabaseHandler(getApplicationContext());

        TextView path = findViewById(R.id.path);

        EmployeeEntity ee = dbh.getEmployee((int)employeeId);

        path.setText(ee.lastName + " " + ee.firstName + ", " + ee.age);
        path.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        List<Integer> years = new ArrayList<>();
        for (int i = 2000; i <= year; i++) years.add(i);
        yearPager.setAdapter(new YearMonthAdapter(years, y -> {
            //Intent intnt = new Intent(DaysActivity.this, YearsActivity.class);
            //intnt.putExtra("employeeId", ee.id);
            //startActivity(intnt);
        }));
        yearPager.setCurrentItem(year - 2000, false);
        yearPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    int position = yearPager.getCurrentItem();
                    year = position + 2000;
                    updateCalendar(year, month);
                }
            }
        });

        monthsViewPager.setAdapter(adapter);
        monthsViewPager.setCurrentItem(month, false);
        monthsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    int position = monthsViewPager.getCurrentItem();
                    updateSummary();
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                month = position;
            }
        });

        updateCalendar(year, month);

        SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        if(!tutorialShown) {
            //showTutorial(1);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("tutorial_shown", true);
            editor.apply();
        }
    }

    private void updateCalendar(Integer year, Integer month) {
        monthsViewPager.setAdapter(null);
        monthsViewPager.setAdapter(new MonthFragmentAdapter(
                this, employeeId, year, this::onSummaryUpdated, this::monthOnClick));
        monthsViewPager.setCurrentItem(month, false);
        updateSummary();
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

            //showTutorial(1);
        } else if (R.id.action_report == id) {
            // Handle about action
            Intent intent = new Intent(DaysActivity.this, ReportActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSummaryUpdated(YearSummary summary) {
        this.summary = summary;
        updateSummary();
    }

    private void monthOnClick(View v) {
        Intent intnt = new Intent(DaysActivity.this, MonthsActivity.class);
        intnt.putExtra("employeeId", employeeId);
        intnt.putExtra("year", year);
        startActivity(intnt);

    }
    private void updateSummary() {
        TextView summaryMonthlyText = this.findViewById(R.id.summaryMonthly);
        TextView summaryAnnualText = this.findViewById(R.id.summaryAnnual);
        double hoursMonthly = 0;
        double salaryMonthly = 0;
        double hoursAnnual = 0;
        double salaryAnnual = 0;
        if(summary != null && summary.months != null && summary.months.size() > 0) {
            hoursAnnual = summary.hours;
            salaryAnnual = summary.salary;
            for (MonthSummary ms : summary.months) {
                if(ms.month == month) {
                    hoursMonthly = ms.hours;
                    salaryMonthly = ms.salary;
                    break;
                }
            }
        }

        // if summary for year is 0, then clear year from db
        if(summary != null && hoursAnnual == 0 && salaryAnnual == 0) {
            YearEntity ye = dbh.getYear(employeeId, year);
            if(ye != null) {
                List<MonthEntity> months = dbh.getMonths(ye.id);
                for(MonthEntity me : months) {
                    dbh.deleteMonth(me);
                }
                dbh.deleteYear(ye);
            }
        }

        summaryAnnualText.setText(
                getResources().getString(R.string.hours) + ": " +
                        String.format("%.2f", hoursAnnual) + " " +
                        getResources().getString(R.string.salary) + ": " +
                        String.format("%.2f", salaryAnnual));

        summaryMonthlyText.setText(
                getResources().getString(R.string.hours) + ": " +
                        String.format("%.2f", hoursMonthly) + " " +
                        getResources().getString(R.string.salary) + ": " +
                        String.format("%.2f", salaryMonthly));
    }

    private void showTutorial(int part) {
        if (part == 1) {
            int dayNbr = 12;
            GridLayout dc = findViewById(R.id.months_viewpager);
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
                                      //int targetId = lastTarget.id();
                                      //DayEntity dayEntity = null;
//
                                      //if(monthEntity != null)
                                      //    dayEntity = dbh.getDay(monthEntity.id, dayNbr+1);
//
                                      //if(targetClicked && targetId == 1) {
                                      //    DayButton button = day.findViewById(R.id.item_button);
//
                                      //    SettingsEntity se = dbh.getSettings(employeeId);
//
                                      //    if (se == null) {
                                      //        se = dbh.getSettings();
//
                                      //        if (se == null) {
                                      //            se = new SettingsEntity();
                                      //            se.hours = 8;
                                      //            se.price = 0;
                                      //            dbh.writeSettings(se);
                                      //        }
                                      //    }
//
                                      //    if (monthEntity == null) {
                                      //        monthEntity = new MonthEntity();
                                      //        monthEntity.yearId = yearId;
                                      //        monthEntity.month = month;
                                      //        monthEntity.id = (int) dbh.writeMonth(monthEntity);
                                      //    }
//
                                      //    if (dayEntity == null) {
                                      //        double hours = se.hours; // using default
                                      //        double price = se.price; // using default
//
                                      //        DayEntity de = new DayEntity();
                                      //        de.monthId = monthEntity.id;
                                      //        de.day = dayNbr+1;
                                      //        de.hours = hours;
                                      //        de.price = price;
//
                                      //        dbh.writeDay(de);
                                      //        button.setIsInsideDb(true);
//
                                      //        button.setCenterText(String.valueOf(hours));
                                      //        button.setBottomRightText(String.format("%.2f", hours * price));
//
                                      //        Intent intent = new Intent();
                                      //        intent.putExtra("month", month);
                                      //        setResult(RESULT_OK, intent);
                                      //    }
                                      //}
                                      //else if(targetClicked && targetId == 2) {
                                      //    DayDialog ed = new DayDialog();
//
                                      //    ed.open(DaysActivity.this, DaysActivity.this::updateDay,
                                      //            DaysActivity.this::deleteDay, DaysActivity.this::dialogDismissHandler, dayEntity);
                                      //}
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
