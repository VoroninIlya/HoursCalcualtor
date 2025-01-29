package com.svo7777777.hourscalculator;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class HoursCalculator extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
