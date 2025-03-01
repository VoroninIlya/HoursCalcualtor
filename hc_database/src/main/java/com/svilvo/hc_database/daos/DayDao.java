package com.svilvo.hc_database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.svilvo.hc_database.entities.DayEntity;

import java.util.List;

@Dao
public interface DayDao {
    @Query("SELECT * FROM days WHERE monthId = :monthId ORDER BY day ASC")
    List<DayEntity> getDaysForMonth(int monthId);

    @Query("SELECT * FROM days WHERE monthId = :monthId ORDER BY day ASC")
    LiveData<List<DayEntity>> getDaysForMonthLd(int monthId);

    @Query("SELECT * FROM days WHERE (monthId = :monthId AND day = :day)")
    DayEntity getDay(int monthId, int day);

    @Query("SELECT * FROM days WHERE (monthId = :monthId AND day = :day)")
    LiveData<DayEntity> getDayLd(int monthId, int day);
    
    @Update
    int update(DayEntity day);
    @Insert
    long insert(DayEntity day);
    @Delete
    void delete(DayEntity day);
}
