package com.svo7777777.hc_database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface YearDao {
    @Insert
    long insert(YearEntity year);

    @Query("SELECT * FROM years WHERE employeeId = :employeeId ORDER BY year DESC" )
    List<YearEntity> getYearsForEmployee(int employeeId);

    @Update
    int update(YearEntity year);

    @Delete
    void delete(YearEntity year);
}
