package com.svilvo.hc_database;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseClient {
    private static AppDatabaseClient instance;
    private final AppDatabase db;

    private AppDatabaseClient(Context context) {
        db = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "AppDatabase"
        ).build();
    }

    public static synchronized AppDatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return instance.db;
    }
}
