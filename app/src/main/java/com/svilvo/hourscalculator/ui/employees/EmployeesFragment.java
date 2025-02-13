package com.svilvo.hourscalculator.ui.employees;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hourscalculator.R;

import java.util.List;


public class EmployeesFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private EmployeesViewModel viewModel;

    public static EmployeesFragment newInstance() {
        return new EmployeesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EmployeesViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employees, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getEmployees().observe(getViewLifecycleOwner(), employees -> {

            LinearLayout ec = view.findViewById(R.id.employees_container);
            ec.clearDisappearingChildren();

            List<EmployeeEntity> selected = viewModel.getSelectedEmployees();

            for (EmployeeEntity ee : employees) {
                // Inflate the custom LinearLayout from XML
                LayoutInflater inflater = LayoutInflater.from(this.getContext());
                View newEmployeeItem = inflater.inflate(R.layout.item_card_2, ec, false);

                CardView newEmplButton = newEmployeeItem.findViewById(R.id.item_button);

                TextView tc = newEmployeeItem.findViewById(R.id.textCenter);
                tc.setText(ee.lastName + " " + ee.firstName);
                TextView tlt = newEmployeeItem.findViewById(R.id.textLeftTop);
                tlt.setText(this.getString(R.string.age) + ": " + ee.age);
                TextView tlb = newEmployeeItem.findViewById(R.id.textLeftBottom);
                tlb.setVisibility(View.INVISIBLE);
                TextView trc = newEmployeeItem.findViewById(R.id.textRightTop);
                trc.setVisibility(View.INVISIBLE);
                TextView trb = newEmployeeItem.findViewById(R.id.textRightBottom);
                trb.setVisibility(View.INVISIBLE);

                CheckBox newEmplCheckbox = newEmployeeItem.findViewById(R.id.checkBox);

                if(selected.contains(ee)) {
                    newEmplCheckbox.setChecked(true);
                }

                newEmplCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        viewModel.addSelectedEmployee(ee);
                    } else {
                        viewModel.removeSelectedEmployee(ee);
                    }
                    sharedViewModel.setSelectedEmployee(viewModel.getSelectedEmployees());
                });

                ec.addView(newEmployeeItem);
            }
        });
    }

}