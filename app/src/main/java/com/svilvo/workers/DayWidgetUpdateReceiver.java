package com.svilvo.workers;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.svilvo.hourscalculator.DayWidget;

public class DayWidgetUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, DayWidget.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);

        for (int widgetId : widgetIds) {
            DayWidget.updateAppWidget(context, appWidgetManager, widgetId);
        }
    }
}
