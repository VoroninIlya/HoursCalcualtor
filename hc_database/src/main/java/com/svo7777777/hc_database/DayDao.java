package com.svo7777777.hc_database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface DayDao {
    @Query("SELECT * FROM days WHERE monthId = :monthId ORDER BY day ASC")
    List<DayEntity> getDaysForMonth(int monthId);

    @Query("SELECT SUM(hours) FROM days WHERE monthId = :monthId")
    long getHoursForMonth(int monthId);

    @Query("SELECT SUM(hours*price) FROM days WHERE monthId = :monthId")
    long getSalaryForMonth(int monthId);

    @Update
    int update(DayEntity day);
    @Insert
    long insert(DayEntity day);
    @Delete
    void delete(DayEntity day);
}
