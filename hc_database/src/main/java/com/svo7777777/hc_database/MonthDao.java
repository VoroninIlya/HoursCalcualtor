package com.svo7777777.hc_database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MonthDao {
    @Query("SELECT * FROM months WHERE yearId = :yearId ORDER BY month ASC" )
    List<MonthEntity> getMonthsForYear(int yearId);

    @Query("SELECT * FROM months WHERE (yearId = :yearId AND month = :month) ORDER BY month ASC" )
    MonthEntity getMonthForYear(int yearId, int month);

    @Query("SELECT SUM(hours) FROM months LEFT JOIN days ON (months.id = days.monthId) WHERE yearId = :yearId")
    double getHoursForYear(int yearId);

    @Query("SELECT SUM(hours) FROM months LEFT JOIN days ON (months.id = days.monthId) WHERE (yearId = :yearId AND months.month = :month)")
    double getHoursForMonth(int yearId, int month);

    @Query("SELECT SUM(hours*price) FROM months LEFT JOIN days ON (months.id = days.monthId) WHERE yearId = :yearId")
    double getSalaryForYear(int yearId);

    @Insert
    long insert(MonthEntity month);

    @Delete
    void delete(MonthEntity month);
}
