package com.svo7777777.hc_database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EmployeeDao {
    @Insert
    long insert(EmployeeEntity employee);

    @Query("SELECT * FROM employees ORDER BY lastName ASC, firstName ASC, age ASC")
    List<EmployeeEntity> getAll();

    @Query("SELECT * FROM employees WHERE lastName LIKE :lastName AND firstName LIKE :firstName AND age LIKE :age")
    EmployeeEntity findById(String lastName, String firstName, int age);

    @Query("SELECT * FROM employees WHERE id LIKE :id")
    EmployeeEntity findById(int id);

    @Update
    int update(EmployeeEntity employee);
    @Delete
    void delete(EmployeeEntity employee);
}
