package com.svilvo.utils;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.svilvo.hc_database.AppDatabaseClient;
import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.entities.MonthEntity;
import com.svilvo.hc_database.entities.SettingsEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.hc_database.views.MonthSummary;
import com.svilvo.hc_database.views.YearSummary;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseHandler {
    private AppDatabaseClient dbc = null;

    public DatabaseHandler(Context context){
        dbc = AppDatabaseClient.getInstance(context);
    }

    public List<EmployeeEntity> getEmployees() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<EmployeeEntity> employees = null;
        try {
            Future<List<EmployeeEntity>> employeesFuture = executorService.submit(() -> {
                List<EmployeeEntity> empl = dbc.getAppDatabase().employeeDao().getEmployees();
                // Можно выполнить дополнительные действия, например, логгирование
                //runOnUiThread(() -> {
                //    // Обновите UI, если нужно
                //    Log.d("MainActivity", "User added successfully");
                //});

                return empl;
            });
            employees = employeesFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return employees;
    }

    public LiveData<List<EmployeeEntity>> getEmployeesLd() {
        return dbc.getAppDatabase().employeeDao().getEmployeesLd();
    }

    public EmployeeEntity getEmployee(int id) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        EmployeeEntity employee = null;
        try {
            Future<EmployeeEntity> employeeFuture = executorService.submit(() -> {
                EmployeeEntity empl = dbc.getAppDatabase().employeeDao().findById(id);
                // Можно выполнить дополнительные действия, например, логгирование
                //runOnUiThread(() -> {
                //    // Обновите UI, если нужно
                //    Log.d("MainActivity", "User added successfully");
                //});

                return empl;
            });
            employee = employeeFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return employee;
    }

    public Long writeEmployee(EmployeeEntity employee) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() ->
                    dbc.getAppDatabase().employeeDao().insert(employee));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public Long updateEmployee(EmployeeEntity employee) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() ->
                    (long) dbc.getAppDatabase().employeeDao().update(employee));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public void deleteEmployee(EmployeeEntity employee) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.execute(() -> {
                dbc.getAppDatabase().employeeDao().delete(employee);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
    }

    public List<YearEntity> getYears(int employeeId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<YearEntity> years = null;
        try {
            Future<List<YearEntity>> yearsFuture = executorService.submit(() ->
                    dbc.getAppDatabase().yearDao().getYearsForEmployee(employeeId));
            years = yearsFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return years;
    }

    public LiveData<YearSummary> getYearSummary(int employeeId, int year) {
        return dbc.getAppDatabase().yearDao().getYearSummary(employeeId, year);
    }

    public Long insertYear(YearEntity year) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() ->
                    dbc.getAppDatabase().yearDao().insert(year));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public Long updateYear(YearEntity yearEntity) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Long id = (long) -1;
        try {
            Future<Long> idFuture = executorService.submit(() ->
                    (long) dbc.getAppDatabase().yearDao().update(yearEntity));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public void deleteYear(YearEntity year) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.execute(() ->
                    dbc.getAppDatabase().yearDao().delete(year));
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
    }

    public MonthEntity getMonth(int yearId, int month){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        MonthEntity monthRes = null;
        try {
            Future<MonthEntity> monthFuture = executorService.submit(() ->
                    dbc.getAppDatabase().monthDao().getMonthForYear(yearId, month));
            monthRes = monthFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return monthRes;
    }

    public long writeMonth(MonthEntity month){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Long> idFuture = executorService.submit(() ->
                    dbc.getAppDatabase().monthDao().insert(month));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public LiveData<MonthSummary> getMonthSummary(int employeeId, int year, int month) {
        return dbc.getAppDatabase().monthDao().getMonthSummary(employeeId, year, month);
    }

    public List<DayEntity> getDays(int monthId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<DayEntity> days = null;
        try {
            Future<List<DayEntity>> daysFuture = executorService.submit(() ->
                    dbc.getAppDatabase().dayDao().getDaysForMonth((int)monthId));
            days = daysFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return days;
    }

    public DayEntity getDay(int monthId, int dayNum) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        DayEntity day = null;
        try {
            Future<DayEntity> daysFuture = executorService.submit(() ->
                    dbc.getAppDatabase().dayDao().getDay((int)monthId, dayNum));
            day = daysFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return day;
    }

    public long writeDay(DayEntity day){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Long> idFuture = executorService.submit(() ->
                    dbc.getAppDatabase().dayDao().insert(day));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public void deleteDay(DayEntity day) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Void> idFuture = (Future<Void>) executorService.submit(() ->
                    dbc.getAppDatabase().dayDao().delete(day));
            idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
    }

    public int updateDay(DayEntity day) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        int id = -1;
        try {
            Future<Integer> idFuture = (Future<Integer>) executorService.submit(() ->
                    dbc.getAppDatabase().dayDao().update(day));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public double getHours(int yearId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        double hours = 0.0;
        try {
            Future<Double> hoursFuture = executorService.submit(() ->
                    dbc.getAppDatabase().monthDao().getHoursForYear(yearId));
            hours = hoursFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return hours;
    }

    public double getHours(int yearId, int month){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        double hours = 0.0;
        try {
            Future<Double> hoursFuture = executorService.submit(() ->
                    dbc.getAppDatabase().monthDao().getHoursForMonth(yearId, month));
            hours = hoursFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return hours;
    }

    public double getSalary(int yearId){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        double salary = 0.0;
        try {
            Future<Double> salaryFuture = executorService.submit(() ->
                    dbc.getAppDatabase().monthDao().getSalaryForYear(yearId));
            salary = salaryFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return salary;
    }

    public double getSalary(int yearId, int month){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        double salary = 0.0;
        try {
            Future<Double> salaryFuture = executorService.submit(() ->
                    dbc.getAppDatabase().monthDao().getSalaryForMonth(yearId, month));
            salary = salaryFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return salary;
    }

    public SettingsEntity getSettings() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SettingsEntity settings = null;
        try {
            Future<SettingsEntity> daysFuture = executorService.submit(() ->
                    dbc.getAppDatabase().settingsDao().get());
            settings = daysFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return settings;
    }

    public SettingsEntity getSettings(int employeeId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SettingsEntity settings = null;
        try {
            Future<SettingsEntity> daysFuture = executorService.submit(() ->
                    dbc.getAppDatabase().settingsDao().get(employeeId));
            settings = daysFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return settings;
    }

    public int updateSettings(SettingsEntity settings) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        int id = -1;
        try {
            Future<Integer> idFuture = executorService.submit(() ->
                    dbc.getAppDatabase().settingsDao().update(settings));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public long writeSettings(SettingsEntity settings){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Long> idFuture = executorService.submit(() ->
                    dbc.getAppDatabase().settingsDao().insert(settings));
            id = idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
        return id;
    }

    public void deleteSettings(SettingsEntity settings) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long id = -1;
        try {
            Future<Void> idFuture = (Future<Void>) executorService.submit(() ->
                    dbc.getAppDatabase().settingsDao().delete(settings));
            idFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the executor
            executorService.shutdown();
        }
    }
}
