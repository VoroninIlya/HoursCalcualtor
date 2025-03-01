package com.svilvo.hourscalculator.ui.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.svilvo.adapters.DayAdapter;
import com.svilvo.adapters.MonthFragmentSummaryCallBack;
import com.svilvo.dialogs.DayDialog;
import com.svilvo.hc_database.entities.DayEntity;
import com.svilvo.hc_database.entities.MonthEntity;
import com.svilvo.hc_database.entities.SettingsEntity;
import com.svilvo.hc_database.entities.YearEntity;
import com.svilvo.hourscalculator.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthFragment extends Fragment {
    private MonthViewModel monthViewModel;
    private int employeeId = -1;
    private int year = -1;
    private int month = -1;
    private int daysInMonth = 0;
    private int itemWidthPx = 70;
    private int numColumns = 6;
    private MonthEntity monthEntity;
    private YearEntity yearEntity;
    private List<DayEntity> days;
    private Calendar cldr;
    RecyclerView recyclerView;
    DayAdapter adapter;
    List<DayItem> daysList = new ArrayList<>();

    private static MonthFragmentSummaryCallBack sumCb;
    private static View.OnClickListener monthOnClick;

    public static MonthFragment newInstance(int employeeId, int year,
                                            int month, MonthFragmentSummaryCallBack cb,
                                            View.OnClickListener monthOnClick) {
        sumCb = cb;
        monthOnClick = monthOnClick;
        MonthFragment fragment = new MonthFragment();
        Bundle args = new Bundle();
        args.putInt("employeeId", employeeId);
        args.putInt("year", year);
        args.putInt("month", month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cldr = Calendar.getInstance();
        if (getArguments() != null) {
            employeeId = getArguments().getInt("employeeId");
            year = getArguments().getInt("year");
            month = getArguments().getInt("month");

            cldr.set(Calendar.MONTH, month);
            cldr.set(Calendar.YEAR, year);

            daysInMonth = cldr.getActualMaximum(Calendar.DAY_OF_MONTH);
            String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week_short);
            for (int i = 1; i <= 31; i++) {
                cldr.set(android.icu.util.Calendar.DATE, i);
                int dayOfWeek = cldr.get(android.icu.util.Calendar.DAY_OF_WEEK);
                int day = i;
                daysList.add(new DayItem(
                        i,
                        daysOfWeek[dayOfWeek - 1].trim(),
                        (dayOfWeek == android.icu.util.Calendar.SUNDAY || dayOfWeek == android.icu.util.Calendar.SATURDAY),
                        false,
                        0.0f,
                        0.0f,
                        itemWidthPx,
                        true,
                        null
                ));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        recyclerView = view.findViewById(R.id.daysContainer);
        String[] months = getResources().getStringArray(R.array.months);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        float itemWidthDp = 66; // Example item width in dp
        numColumns = (int) (screenWidthDp / itemWidthDp);
        itemWidthDp = screenWidthDp/numColumns;

        // Convert itemWidthDp to pixels for layout parameters
        itemWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                itemWidthDp,
                getResources().getDisplayMetrics()
        );

        TextView monthName = view.findViewById(R.id.monthName);
        monthName.setText(months[month]);
        monthName.setOnClickListener(monthOnClick);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int rippleColor = Color.parseColor("#66CCCCCC");

            RippleDrawable rippleDrawable = new RippleDrawable(
                    ColorStateList.valueOf(rippleColor),
                    null,
                    new ColorDrawable(Color.WHITE)
            );

            monthName.setForeground(rippleDrawable);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numColumns);
        recyclerView.setLayoutManager(layoutManager);
        List<DayItem> daysList = new ArrayList<>();
        adapter = new DayAdapter(daysList);
        recyclerView.setAdapter(adapter);

        monthViewModel = new ViewModelProvider(this).get(MonthViewModel.class);

        monthViewModel.getYearSummary(employeeId, year).observe(getViewLifecycleOwner(), ye -> {
            sumCb.onSummaryUpdated(ye);
        });

        monthViewModel.getYearLd(employeeId, year).observe(getViewLifecycleOwner(), ye -> {
            yearEntity = ye;
            if(yearEntity != null) {
                monthViewModel.getMonthLd(yearEntity.id, month).observe(getViewLifecycleOwner(), me -> {
                    monthEntity = me;
                    if(monthEntity != null) {
                        monthViewModel.getDaysLd(monthEntity.id).observe(getViewLifecycleOwner(), d -> {
                            days = d;
                        });
                    }
                });
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(getView());
    }

    private void updateUI(@NonNull View view) {

        if (yearEntity == null) {
            yearEntity = monthViewModel.getYear(employeeId, year);
        }
        if( yearEntity != null && monthEntity == null) {
            monthEntity = monthViewModel.getMonth(yearEntity.id, month);
        }

        List<DayEntity> daysEntity = null;
        if (days != null) {
            daysEntity = days;
            days = null;
        } else {
            if (monthEntity != null) {
                daysEntity = monthViewModel.getDays(monthEntity.id);
            }
        }

        for (int i = 1; i <= 31; i++) {
            cldr.set(android.icu.util.Calendar.DATE, i);
            int day = i;

            double hours = 0;
            double price = 0;
            if (i <= daysInMonth) {
                if (daysEntity != null) {
                    for (DayEntity d : daysEntity) {
                        if (d.day == i) {
                            hours = d.hours;
                            price = d.price;
                            break;
                        }
                    }
                }
                boolean isInsideDb = hours > 0;

                boolean changed = false;
                DayItem dayItem = daysList.get(i-1);

                if(!adapter.hasDay(i-1))
                    changed = true;

                if(dayItem.itemSize != itemWidthPx) {
                    dayItem.itemSize = itemWidthPx;
                    changed = true;
                }
                if(dayItem.hours != hours) {
                    dayItem.hours = hours;
                    changed = true;
                }
                if(dayItem.price != price) {
                    dayItem.price = price;
                    changed = true;
                }
                if(dayItem.isInsideDb != isInsideDb) {
                    dayItem.isInsideDb = isInsideDb;
                    changed = true;
                }
                if(!dayItem.isVisible) {
                    dayItem.isVisible = true;
                    changed = true;
                }
                if(dayItem.onClick == null) {
                    changed = true;
                    dayItem.onClick = v -> {
                        SettingsEntity se = getSettings();
                        initYearAndMonthEntities();

                        DayEntity dayEntity = monthViewModel.getDay(monthEntity.id, day);
                        if (dayEntity == null) {
                            double h = se.hours; // using default
                            double p = se.price; // using default

                            DayEntity de = new DayEntity();
                            de.monthId = monthEntity.id;
                            de.day = day;
                            de.hours = h;
                            de.price = p;

                            monthViewModel.addDay(de);

                            dayItem.hours = h;
                            dayItem.price = p;
                            dayItem.isInsideDb = true;
                            adapter.updateDay(dayItem);
                        } else {
                            DayDialog ed = new DayDialog();

                            ed.open(view.getContext(),
                                    d -> {
                                        monthViewModel.updateDay(d);
                                        dayItem.hours = d.hours;
                                        dayItem.price = d.price;
                                        adapter.updateDay(dayItem);
                                    },
                                    d -> {
                                        monthViewModel.deleteDay(d);
                                        dayItem.hours = 0;
                                        dayItem.price = 0;
                                        dayItem.isInsideDb = false;
                                        adapter.updateDay(dayItem);
                                    }, this::dialogDismissHandler, dayEntity);
                        }
                    };
                }
                if(!changed)
                    continue;

                if (adapter.hasDay(i-1)){
                    adapter.updateDay(dayItem);
                } else {
                    adapter.addDay(dayItem);
                }
            }
        }
    }

    private void dialogDismissHandler() {
        //SharedPreferences prefs = getSharedPreferences("hours_calculator_tutorial", MODE_PRIVATE);
        //boolean tutorialShown = prefs.getBoolean("tutorial_shown", false);

        //if(!tutorialShown) {
        //    showTutorial(2);
        //}
    }

    private SettingsEntity getSettings() {
        SettingsEntity se = monthViewModel.getSettings(employeeId);

        if (se == null) {
            se = monthViewModel.getSettings();

            if (se == null) {
                se = new SettingsEntity();
                se.hours = 8;
                se.price = 0;
                monthViewModel.addSettings(se);
            }
        }
        return se;
    }

    private void initYearAndMonthEntities() {
        // if year dosn't exist in db add it
        if (yearEntity == null) {
            yearEntity = new YearEntity();
            yearEntity.employeeId = employeeId;
            yearEntity.year = year;
            yearEntity.id = (int) monthViewModel.addYear(yearEntity);
        }

        // if month dosn't exist in db add it
        if (monthEntity == null) {
            monthEntity = new MonthEntity();
            monthEntity.yearId = yearEntity.id;
            monthEntity.month = month;
            monthEntity.id = (int) monthViewModel.addMonth(monthEntity);
        }
    }
}
