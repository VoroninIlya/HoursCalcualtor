package com.svo7777777.hc_database;

import androidx.annotation.RequiresPermission;
import androidx.room.Dao;
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
}
