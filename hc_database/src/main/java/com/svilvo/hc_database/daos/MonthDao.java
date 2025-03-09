package com.svilvo.hc_database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.svilvo.hc_database.entities.MonthEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.hc_database.views.MonthSummary;

import java.util.List;

@Dao
public interface MonthDao {
    @Query("SELECT * FROM months WHERE yearId = :yearId ORDER BY month ASC" )
    List<MonthEntity> getMonthsForYear(int yearId);

    @Query("SELECT * FROM months WHERE (yearId = :yearId AND month = :month) ORDER BY month ASC" )
    MonthEntity getMonthForYear(int yearId, int month);

    @Query("SELECT * FROM months WHERE (yearId = :yearId AND month = :month) ORDER BY month ASC" )
    LiveData<MonthEntity> getMonthForYearLd(int yearId, int month);

    @Query("SELECT SUM(hours) FROM months " +
            "LEFT JOIN days ON (months.id = days.monthId) " +
            "WHERE yearId = :yearId")
    double getHoursForYear(int yearId);

    @Query("SELECT SUM(hours) FROM months " +
            "LEFT JOIN days ON (months.id = days.monthId) " +
            "WHERE (yearId = :yearId AND months.month = :month)")
    double getHoursForMonth(int yearId, int month);

    @Query("SELECT SUM(hours*price) FROM months " +
            "LEFT JOIN days ON (months.id = days.monthId) " +
            "WHERE yearId = :yearId")
    double getSalaryForYear(int yearId);

    @Query("SELECT SUM(hours*price) FROM months " +
            "LEFT JOIN days ON (months.id = days.monthId) " +
            "WHERE (yearId = :yearId AND months.month = :month)")
    double getSalaryForMonth(int yearId, int month);

    @Transaction
    @Query("SELECT m.id AS id, m.month AS month, y.id AS yearId, " +
            "SUM(m.hours) AS hours, " +
            "SUM(m.salary) AS salary " +
            "FROM month_summary_view m " +
            "LEFT JOIN years y ON y.id = m.yearId " +
            "WHERE (y.employeeId = :employeeId AND y.year = :year AND m.month = :month) " +
            "GROUP BY m.id, m.month")
    LiveData<MonthSummary> getMonthSummary(int employeeId, int year, int month);
    @Insert
    long insert(MonthEntity month);

    @Delete
    void delete(MonthEntity month);
}
