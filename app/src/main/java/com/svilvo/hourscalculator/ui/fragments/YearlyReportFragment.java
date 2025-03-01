package com.svilvo.hourscalculator.ui.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

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
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.views.YearSummary;
import com.svilvo.hc_database.views.MonthSummary;
import com.svilvo.recycleviewmodels.EmployeeRecycleViewModel;
import com.svilvo.hourscalculator.R;
import com.svilvo.recycleviewmodels.MonthRecycleViewModel;

import java.util.ArrayList;
import java.util.List;

public class YearlyReportFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private YearlyReportViewModel viewModel;
    private EmployeeRecycleViewAdapter adapter;
    private List<EmployeeEntity> employees;
    private int year;
    private int value = 0;

    public static YearlyReportFragment newInstance() {
        return new YearlyReportFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(YearlyReportViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_yearly_report, container, false);

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
                    LiveData<YearSummary> hld = viewModel.getYearSummaries(ee.id, year);
                    if(hld != null) {
                        hld.observe(getViewLifecycleOwner(), ys -> {
                            for (EmployeeRecycleViewModel em : employeesList) {
                                if (em.getEmployee().id == ee.id) {

                                    List<MonthRecycleViewModel> mv = new ArrayList<>();

                                    if(ys != null) {
                                        if(value == 0)
                                            em.setTotalHours(ys.hours);
                                        else
                                            em.setTotalHours(ys.salary);

                                        for(int i = 0; i <= 11; i++) {
                                            MonthSummary ms = null;
                                            for(MonthSummary tmp : ys.months) {
                                                if(tmp.month == i) {
                                                    ms = tmp;
                                                    break;
                                                }
                                            }
                                            if (ms != null) {
                                                mv.add(new MonthRecycleViewModel(ms, value));
                                            } else {
                                                ms = new MonthSummary();
                                                ms.month = i;
                                                ms.hours = 0;
                                                ms.salary = 0;
                                                ms.yearId = ys.id;
                                                mv.add(new MonthRecycleViewModel(ms, value));
                                            }
                                        }
                                    } else {
                                        em.setTotalHours(0);
                                        for(int i = 0; i < 11; i++) {
                                            MonthSummary ms = new MonthSummary();
                                            ms.month = i;
                                            ms.hours = 0;
                                            ms.salary = 0;
                                            ms.yearId = 0;
                                            mv.add(new MonthRecycleViewModel(ms, value));
                                        }
                                    }

                                    em.setMonths(mv);

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