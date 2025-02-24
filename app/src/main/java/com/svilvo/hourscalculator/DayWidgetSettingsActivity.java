package com.svilvo.hourscalculator;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.svilvo.dialogs.DayDialog;
import com.svilvo.dialogs.DayWidgetSettingsDialog;
import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.utils.DatabaseHandler;

import java.util.List;

public class DayWidgetSettingsActivity extends AppCompatActivity {
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int dialogType = -1;
    private DatabaseHandler dbh = null;
    private List<EmployeeEntity> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_day_widget_settings);

        Intent intent = getIntent();
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        dialogType = intent.getIntExtra("dialogType", -1);

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        dbh = new DatabaseHandler(getApplicationContext());

        employees = dbh.getEmployees();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(dialogType == 1) {
            DayWidgetSettingsDialog dwsd = new DayWidgetSettingsDialog();

            dwsd.open(this, this::daySettingsDialogHandler,
                    this::daySettingsDialogOnDismissHandler, employees,
                    DayWidget.loadEmployeeId(this, widgetId),
                    DayWidget.loadDayId(this, widgetId));

        } else if (dialogType == 2) {
            DayDialog ed = new DayDialog();

            int monthId = intent.getIntExtra("monthId", -1);
            int dayNmr = intent.getIntExtra("day", -1);

            DayEntity day = dbh.getDay(monthId, dayNmr);

            ed.open(this, this::updateDay,
                    this::deleteDay, this::dialogDismissHandler, day);
        }
    }

    public void daySettingsDialogHandler(int employeeId, int dayId) {
        if (employeeId >= 0) {
            DayWidget.saveEmployeeId(this, widgetId, employeeId);
            DayWidget.saveDayId(this, widgetId, dayId);
            DayWidget.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetId);
        }
        this.finish();
    }

    public void daySettingsDialogOnDismissHandler() {
        this.finish();
    }

    private void updateDay(DayEntity day) {
        dbh.updateDay(day);
        DayWidget.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetId);
        this.finish();
    }

    private void deleteDay(DayEntity day) {
        dbh.deleteDay(day);
        DayWidget.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetId);
        this.finish();
    }

    private void dialogDismissHandler() {
        this.finish();
    }
}