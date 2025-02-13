package com.svilvo.hc_database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.svilvo.hc_database.entities.EmployeeEntity;

import java.util.List;

@Dao
public interface EmployeeDao {
    @Insert
    long insert(EmployeeEntity employee);

    @Query("SELECT * FROM employees ORDER BY lastName ASC, firstName ASC, age ASC")
    List<EmployeeEntity> getEmployees();

    @Query("SELECT * FROM employees ORDER BY lastName ASC, firstName ASC, age ASC")
    LiveData<List<EmployeeEntity>> getEmployeesLd();

    @Query("SELECT * FROM employees WHERE id LIKE :id")
    EmployeeEntity findById(int id);

    @Update
    int update(EmployeeEntity employee);
    @Delete
    void delete(EmployeeEntity employee);
}
