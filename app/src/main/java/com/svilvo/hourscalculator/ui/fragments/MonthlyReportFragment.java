package com.svilvo.hourscalculator.ui.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svilvo.adapters.EmployeeRecycleViewAdapter;
import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.views.MonthSummary;
import com.svilvo.hourscalculator.R;
import com.svilvo.recycleviewmodels.DayRecycleViewModel;
import com.svilvo.recycleviewmodels.EmployeeRecycleViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthlyReportFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private MonthlyReportViewModel viewModel;
    private EmployeeRecycleViewAdapter adapter;
    private List<EmployeeEntity> employees;
    private int year;
    private int month;
    private int value = 0;

    public static MonthlyReportFragment newInstance() {
        return new MonthlyReportFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MonthlyReportViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_monthly_report, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        adapter = new EmployeeRecycleViewAdapter();
        recyclerView.setAdapter(adapter);

        sharedViewModel.getSelectedEmployee().observe(getViewLifecycleOwner(), employees -> {
            this.employees = employees;
            updateUi();
        });

        sharedViewModel.getSelectedYear().observe(getViewLifecycleOwner(), year -> {
            this.year = year;
            updateUi();
        });

        sharedViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), month -> {
            this.month = month;
            updateUi();
        });

        sharedViewModel.getSelectedResult().observe(getViewLifecycleOwner(), value -> {
            this.value = value;
            updateUi();
        });

        return v;
    }

    private void updateUi() {
        List<EmployeeRecycleViewModel> employeesList = new ArrayList<>();

        double hours = 0;
        if(employees != null) {
            for(EmployeeEntity ee : employees) {
                employeesList.add(new EmployeeRecycleViewModel(ee, hours));
                if(year != 0) {
                    LiveData<MonthSummary> mld = viewModel.getMonthSummary(ee.id, year, month);
                    if(mld != null) {
                        mld.observe(getViewLifecycleOwner(), ms -> {
                            for (EmployeeRecycleViewModel em : employeesList) {
                                if (em.getEmployee().id == ee.id) {

                                    List<DayRecycleViewModel> dv = new ArrayList<>();
                                    Calendar cldr = Calendar.getInstance(Locale.ROOT);
                                    cldr.set(Calendar.YEAR, year);
                                    cldr.set(Calendar.MONTH, month);
                                    int daysInMonth = cldr.getActualMaximum(Calendar.DAY_OF_MONTH);

                                    if(ms != null) {
                                        if(value == 0)
                                            em.setTotalHours(ms.hours);
                                        else
                                            em.setTotalHours(ms.salary);

                                        for(int i = 1; i <= daysInMonth; i++) {
                                            DayEntity de = null;
                                            for(DayEntity tmp : ms.days) {
                                                if(tmp.day == i) {
                                                    de = tmp;
                                                    break;
                                                }
                                            }
                                            if (de != null) {
                                                dv.add(new DayRecycleViewModel(de, value));
                                            } else {
                                                de = new DayEntity();
                                                de.day = i;
                                                de.hours = 0;
                                                de.price = 0;
                                                de.monthId = ms.id;
                                                dv.add(new DayRecycleViewModel(de, value));
                                            }
                                        }
                                    } else {
                                        em.setTotalHours(0);
                                        for(int i = 1; i <= daysInMonth; i++) {
                                            DayEntity de = new DayEntity();
                                            de.day = i;
                                            de.hours = 0;
                                            de.price = 0;
                                            de.monthId = 0;
                                            dv.add(new DayRecycleViewModel(de, value));
                                        }
                                    }

                                    em.setDays(dv);

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        }

        adapter.setEmployees(employeesList);
        adapter.notifyDataSetChanged();
    }

}