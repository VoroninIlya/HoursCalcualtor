package com.svilvo.hc_database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.svilvo.hc_database.views.YearSummary;
import com.svilvo.hc_database.entities.YearEntity;

import java.util.List;

@Dao
public interface YearDao {
    @Insert
    long insert(YearEntity year);

    @Query("SELECT * FROM years WHERE employeeId = :employeeId ORDER BY year DESC" )
    List<YearEntity> getYearsForEmployee(int employeeId);

    @Query("SELECT * FROM years WHERE (employeeId = :employeeId  AND year = :year) ORDER BY year DESC" )
    YearEntity getYearForEmployee(int employeeId, int year);

    @Query("SELECT * FROM years WHERE (employeeId = :employeeId  AND year = :year) ORDER BY year DESC" )
    LiveData<YearEntity> getYearForEmployeeLd(int employeeId, int year);

    @Transaction
    @Query("SELECT y.id AS id, y.year AS year, y.employeeId AS employeeId, " +
            "SUM(m.hours) AS hours, " +
            "SUM(m.salary) AS salary " +
            "FROM month_summary_view m " +
            "LEFT JOIN years y ON y.id = m.yearId " +
            "WHERE (y.employeeId = :employeeId AND y.year = :year) " +
            "GROUP BY y.id, y.year")
    LiveData<YearSummary> getYearSummary(int employeeId, int year);

    @Update
    int update(YearEntity year);

    @Delete
    void delete(YearEntity year);
}
