package com.svilvo.hourscalculator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.svilvo.hourscalculator.ui.employees.EmployeesFragment;
import com.svilvo.hourscalculator.ui.employees.MonthlyReportFragment;
import com.svilvo.hourscalculator.ui.employees.SharedViewModel;
import com.svilvo.hourscalculator.ui.employees.YearlyReportFragment;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.report_menu);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        if (savedInstanceState == null)
            loadFragment(EmployeesFragment.newInstance());

        ExtendedFloatingActionButton yearlyButton = findViewById(R.id.fab_yearly);
        ExtendedFloatingActionButton monthlyButton = findViewById(R.id.fab_monthly);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            updateUI(true);
        });

        yearlyButton.setOnClickListener(v -> {
            YearlyReportFragment f = YearlyReportFragment.newInstance();
            loadFragment(f);
        });

        monthlyButton.setOnClickListener(v -> {
            MonthlyReportFragment f = MonthlyReportFragment.newInstance();
            loadFragment(f);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        updateUI(false);
    }

    private void updateUI(boolean fromBackstack) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(fromBackstack && currentFragment == null)
            this.finish();

        if (currentFragment instanceof EmployeesFragment) {
            ExtendedFloatingActionButton yearlyButton = findViewById(R.id.fab_yearly);
            yearlyButton.setVisibility(View.VISIBLE);
            ExtendedFloatingActionButton monthlyButton = findViewById(R.id.fab_monthly);
            monthlyButton.setVisibility(View.VISIBLE);
            Spinner spinnerYear = findViewById(R.id.spinnerYear);
            spinnerYear.setVisibility(View.INVISIBLE);
            Spinner spinnerMonth = findViewById(R.id.spinnerMonth);
            spinnerMonth.setVisibility(View.INVISIBLE);
            TextView textYear = findViewById(R.id.textYear);
            textYear.setVisibility(View.INVISIBLE);
            TextView textMonth = findViewById(R.id.textMonth);
            textMonth.setVisibility(View.INVISIBLE);
            Spinner spinnerResult = findViewById(R.id.spinnerResult);
            spinnerResult.setVisibility(View.INVISIBLE);
            TextView textResult = findViewById(R.id.textResult);
            textResult.setVisibility(View.INVISIBLE);
        } else if (currentFragment instanceof YearlyReportFragment) {
            ExtendedFloatingActionButton yearlyButton = findViewById(R.id.fab_yearly);
            yearlyButton.setVisibility(View.INVISIBLE);
            ExtendedFloatingActionButton monthlyButton = findViewById(R.id.fab_monthly);
            monthlyButton.setVisibility(View.INVISIBLE);
            Spinner spinnerYear = findViewById(R.id.spinnerYear);
            spinnerYear.setVisibility(View.VISIBLE);
            Spinner spinnerMonth = findViewById(R.id.spinnerMonth);
            spinnerMonth.setVisibility(View.INVISIBLE);
            TextView textYear = findViewById(R.id.textYear);
            textYear.setVisibility(View.VISIBLE);
            TextView textMonth = findViewById(R.id.textMonth);
            textMonth.setVisibility(View.INVISIBLE);
            Spinner spinnerResult = findViewById(R.id.spinnerResult);
            spinnerResult.setVisibility(View.VISIBLE);
            TextView textResult = findViewById(R.id.textResult);
            textResult.setVisibility(View.VISIBLE);

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int length = 10;

            sharedViewModel.setSelectedYear(currentYear);

            List<String> dynamicList = new ArrayList<>();
            for(int i = 0; i < length; i++) {
                int tmpYear = currentYear - i;

                dynamicList.add(String.valueOf(tmpYear));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    dynamicList
            );

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerYear.setAdapter(adapter);

            spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sharedViewModel.setSelectedYear(Integer.parseInt(dynamicList.get(position).trim()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            List<String> resultList = new ArrayList<>();
            resultList.add(getResources().getString(R.string.hours));
            resultList.add(getResources().getString(R.string.salary));

            ArrayAdapter<String> resultAdapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    resultList
            );

            resultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerResult.setAdapter(resultAdapter);

            spinnerResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sharedViewModel.setSelectedResult(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } else if (currentFragment instanceof MonthlyReportFragment) {
            ExtendedFloatingActionButton yearlyButton = findViewById(R.id.fab_yearly);
            yearlyButton.setVisibility(View.INVISIBLE);
            ExtendedFloatingActionButton monthlyButton = findViewById(R.id.fab_monthly);
            monthlyButton.setVisibility(View.INVISIBLE);
            Spinner spinnerYear = findViewById(R.id.spinnerYear);
            spinnerYear.setVisibility(View.VISIBLE);
            Spinner spinnerMonth = findViewById(R.id.spinnerMonth);
            spinnerMonth.setVisibility(View.VISIBLE);
            TextView textYear = findViewById(R.id.textYear);
            textYear.setVisibility(View.VISIBLE);
            TextView textMonth = findViewById(R.id.textMonth);
            textMonth.setVisibility(View.VISIBLE);
            Spinner spinnerResult = findViewById(R.id.spinnerResult);
            spinnerResult.setVisibility(View.VISIBLE);
            TextView textResult = findViewById(R.id.textResult);
            textResult.setVisibility(View.VISIBLE);

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int length = 10;

            sharedViewModel.setSelectedYear(currentYear);

            List<String> yearsList = new ArrayList<>();
            for(int i = 0; i < length; i++) {
                int tmpYear = currentYear - i;

                yearsList.add(String.valueOf(tmpYear));
            }

            ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    yearsList
            );

            yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerYear.setAdapter(yearsAdapter);

            spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sharedViewModel.setSelectedYear(Integer.parseInt(yearsList.get(position).trim()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            sharedViewModel.setSelectedMonth(currentMonth);
            String[] months = getResources().getStringArray(R.array.months);

            List<String> monthsList = new ArrayList<>();
            for(int i = 0; i < 12; i++) {
                monthsList.add(months[i]);
            }

            ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    monthsList
            );

            monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerMonth.setAdapter(monthsAdapter);

            spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sharedViewModel.setSelectedMonth(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            List<String> resultList = new ArrayList<>();
            resultList.add(getResources().getString(R.string.hours));
            resultList.add(getResources().getString(R.string.salary));

            ArrayAdapter<String> resultAdapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    resultList
            );

            resultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerResult.setAdapter(resultAdapter);

            spinnerResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sharedViewModel.setSelectedResult(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

}