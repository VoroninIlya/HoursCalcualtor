package com.svilvo.hourscalculator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.svilvo.hc_database.DayEntity;
import com.svilvo.hc_database.EmployeeEntity;
import com.svilvo.hc_database.MonthEntity;
import com.svilvo.hc_database.SettingsEntity;
import com.svilvo.hc_database.YearEntity;
import com.svilvo.utils.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class DayWidget extends AppWidgetProvider {

    private static final Calendar cldr = Calendar.getInstance(Locale.ROOT);
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.day_widget);

        Intent settingsIntent = new Intent(context, DayWidgetSettingsActivity.class);
        settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        settingsIntent.putExtra("dialogType", 1);
        settingsIntent.putExtra("monthId", -1);
        settingsIntent.putExtra("day", -1);
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(
                context, appWidgetId, settingsIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.settings_button, settingsPendingIntent);

        DatabaseHandler dbh = new DatabaseHandler(context);

        Date date = cldr.getTime();
        int dayOfWeek = cldr.get(java.util.Calendar.DAY_OF_WEEK);
        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week_short);

        // Define the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        views.setTextViewText(R.id.date, sdf.format(date));
        views.setTextViewText(R.id.weekDay, daysOfWeek[dayOfWeek-1]);

        int employeeId = loadEmployeeId(context, appWidgetId);
        if(employeeId >= 0) {
            EmployeeEntity ee = dbh.getEmployee(employeeId);
            views.setTextViewText(R.id.employee, ee.lastName + " " + ee.firstName);
            List<YearEntity> ye = dbh.getYears(employeeId);

            if(ye.isEmpty()) {
                YearEntity y = new YearEntity();
                y.employeeId = ee.id;
                y.year = cldr.get(Calendar.YEAR);
                y.id = dbh.insertYear(y).intValue();
                ye.add(y);
            }
            for(YearEntity y : ye) {
                if(y.year == cldr.get(Calendar.YEAR)) {
                    DayEntity day = null;
                    int monthNmr = cldr.get(Calendar.MONTH);
                    MonthEntity month = dbh.getMonth(y.id, monthNmr);
                    if(month != null)
                        day = dbh.getDay(month.id, cldr.get(Calendar.DATE));

                    if(day != null) {
                        views.setTextViewText(R.id.hours, String.format("%.1f", day.hours));
                        views.setTextViewText(R.id.salary, String.format("%.2f", day.hours*day.price));

                        Intent intent = new Intent(context, DayWidgetSettingsActivity.class);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        intent.putExtra("dialogType", 2);
                        intent.putExtra("monthId", month.id);
                        intent.putExtra("day", day.day);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                context, appWidgetId, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                    } else {
                        views.setTextViewText(R.id.hours, "0.0");
                        views.setTextViewText(R.id.salary, "0.0");

                        Intent intent = new Intent(context, DayWidget.class);
                        intent.setAction("com.svilvo.hourscalculator.ACTION_WIDGET_CLICK");
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        intent.putExtra("employeeId", employeeId);
                        intent.putExtra("yearId", y.id);
                        intent.putExtra("monthId", month == null ? -1 : month.id);
                        intent.putExtra("month", monthNmr);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                context, appWidgetId, intent, PendingIntent.FLAG_MUTABLE);

                        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
                    }
                }
            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Проверяем, что это наше уникальное действие
        if ("com.svilvo.hourscalculator.ACTION_WIDGET_CLICK".equals(intent.getAction())) {

            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            int employeeId = intent.getIntExtra("employeeId", -1);
            int yearId = intent.getIntExtra("yearId", -1);
            int monthId = intent.getIntExtra("monthId", -1);
            int month = intent.getIntExtra("month", -1);

            DatabaseHandler dbh = new DatabaseHandler(context);

            MonthEntity me = null;
            if(monthId == -1) {
                me = new MonthEntity();
                me.yearId = yearId;
                me.month = month;
                me.id = (int) dbh.writeMonth(me);
                monthId = me.id;
            }

            int dayNmr = cldr.get(Calendar.DATE);
            DayEntity day = dbh.getDay(monthId, dayNmr);
            if (day == null) {
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

                DayEntity de = new DayEntity();
                de.monthId = monthId;
                de.day = dayNmr;
                de.hours = se.hours;
                de.price = se.price;

                dbh.writeDay(de);

            } else {

            }

            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
                DayWidget.updateAppWidget(context, AppWidgetManager.getInstance(context), widgetId);
        }
    }

    private static final String PREFS_NAME = "hours_calculator_widget";

    public static void saveEmployeeId(Context context, int widgetId, int id) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("employee_for_" + widgetId, id);
        editor.apply();
    }

    public static int loadEmployeeId(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt("employee_for_" + widgetId, -1);
    }
}