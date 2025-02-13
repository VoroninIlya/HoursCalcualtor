package com.svilvo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.entities.EmployeeEntity;
import com.svilvo.hc_database.views.MonthSummary;
import com.svilvo.recycleviewmodels.DayRecycleViewModel;
import com.svilvo.recycleviewmodels.EmployeeRecycleViewModel;
import com.svilvo.recycleviewmodels.ListItem;
import com.svilvo.recycleviewmodels.MonthRecycleViewModel;
import com.svilvo.hourscalculator.R;

public class EmployeeRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ListItem> items = new ArrayList<>();

    private static final int TYPE_EMPLOYEE = 0;
    private static final int TYPE_MONTH = 1;
    private static final int TYPE_DAY = 2;

    public EmployeeRecycleViewAdapter() {
    }

    public void setEmployees(List<EmployeeRecycleViewModel> employees) {
        updateList(employees);
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof EmployeeRecycleViewModel) {
            return TYPE_EMPLOYEE;
        } else if  (items.get(position) instanceof MonthRecycleViewModel) {
            return TYPE_MONTH;
        } else {
            return TYPE_DAY;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_EMPLOYEE) {
            View view = inflater.inflate(R.layout.item_employee, parent, false);
            return new EmployeeViewHolder(view);
        } else if (viewType == TYPE_MONTH) {
            View view = inflater.inflate(R.layout.item_month, parent, false);
            return new MonthViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_month, parent, false);
            return new DayViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof EmployeeViewHolder) {
            ((EmployeeViewHolder) holder).bind((EmployeeRecycleViewModel) item);
        } else if (holder instanceof MonthViewHolder) {
            ((MonthViewHolder) holder).bind((MonthRecycleViewModel) item);
        } else if (holder instanceof DayViewHolder) {
            ((DayViewHolder) holder).bind((DayRecycleViewModel) item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, totalHoursTextView;
        ImageView expandIcon;

        EmployeeViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            totalHoursTextView = itemView.findViewById(R.id.totalHoursTextView);
            expandIcon = itemView.findViewById(R.id.expandIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    EmployeeRecycleViewModel employee = (EmployeeRecycleViewModel) items.get(position);
                    employee.setExpanded(!employee.isExpanded());
                    updateList(getEmployeesFromItems());
                }
            });
        }

        void bind(EmployeeRecycleViewModel employee) {
            EmployeeEntity ee = employee.getEmployee();
            nameTextView.setText(ee.lastName + " " + ee.firstName + " (" + ee.age + ")");
            totalHoursTextView.setText(String.valueOf(employee.getTotalHours()));
            expandIcon.setImageResource(employee.isExpanded() ? android.R.drawable.ic_menu_view : android.R.drawable.ic_menu_more);
        }
    }

    class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView monthTextView, hoursTextView;

        String[] months = null;

        MonthViewHolder(View itemView) {
            super(itemView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            hoursTextView = itemView.findViewById(R.id.hoursTextView);
            months = itemView.getContext().getResources().getStringArray(R.array.months);
        }

        void bind(MonthRecycleViewModel month) {
            MonthSummary me = month.getMonth();
            monthTextView.setText(months[me.month]);
            hoursTextView.setText(String.valueOf(me.hours));
        }
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView, hoursTextView;

        String[] months = null;

            DayViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.monthTextView);
            hoursTextView = itemView.findViewById(R.id.hoursTextView);
            months = itemView.getContext().getResources().getStringArray(R.array.months);
        }

        void bind(DayRecycleViewModel day) {
            DayEntity da = day.getDay();
            dayTextView.setText(String.valueOf(da.day));
            hoursTextView.setText(String.valueOf(da.hours));
        }
    }

    private void updateList(List<EmployeeRecycleViewModel> employees) {
        items.clear();
        for (EmployeeRecycleViewModel employee : employees) {
            items.add(employee);
            if (employee.isExpanded()) {
                List<MonthRecycleViewModel> months = employee.getMonths();
                if(months != null)
                    items.addAll(months);
                else {
                    List<DayRecycleViewModel> days = employee.getDays();
                    if(days != null)
                        items.addAll(days);
                }
            }
        }
        notifyDataSetChanged();
    }

    private List<EmployeeRecycleViewModel> getEmployeesFromItems() {
        List<EmployeeRecycleViewModel> employees = new ArrayList<>();
        for (ListItem item : items) {
            if (item instanceof EmployeeRecycleViewModel) {
                employees.add((EmployeeRecycleViewModel) item);
            }
        }
        return employees;
    }
}
