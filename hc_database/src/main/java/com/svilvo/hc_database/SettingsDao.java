package com.svilvo.hc_database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SettingsDao {

    @Update
    int update(SettingsEntity settings);
    @Insert
    long insert(SettingsEntity settings);
    @Query("SELECT * FROM settings LIMIT 1")
    SettingsEntity get();
    @Query("SELECT * FROM settings WHERE employeeId = :employeeId")
    SettingsEntity get(int employeeId);
    @Delete
    void delete(SettingsEntity settings);
}
