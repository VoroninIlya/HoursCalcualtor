package com.svo7777777.hc_database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface MonthDao {
    @Query("SELECT * FROM months WHERE yearId = :yearId ORDER BY month ASC" )
    List<MonthEntity> getMonthsForYear(int yearId);

    @Query("SELECT SUM(hours) FROM months LEFT JOIN days ON (months.id = days.monthId) WHERE yearId = :yearId")
    long getHoursForYear(int yearId);

    @Query("SELECT SUM(hours*price) FROM months LEFT JOIN days ON (months.id = days.monthId) WHERE yearId = :yearId")
    long getSalaryForYear(int yearId);

    @Insert
    long insert(MonthEntity month);

    @Delete
    void delete(MonthEntity month);
}
