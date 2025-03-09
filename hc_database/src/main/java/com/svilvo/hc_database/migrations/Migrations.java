package com.svilvo.hc_database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {
    // From vers 1 to 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create new View
            database.execSQL("CREATE VIEW IF NOT EXISTS `month_summary_view` AS " +
                    "SELECT m.id AS id, m.month AS month, m.yearId AS yearId, " +
                    "SUM(d.hours) AS hours, " +
                    "SUM(d.hours * d.price) AS salary " +
                    "FROM months m " +
                    "LEFT JOIN days d ON m.id = d.monthId " +
                    "GROUP BY m.id, m.month;");
        }
    };
}
