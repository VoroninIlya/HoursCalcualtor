package com.svilvo.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.svilvo.hourscalculator.ui.fragments.MonthFragment;

public class MonthFragmentAdapter extends FragmentStateAdapter {
    private int employeeId = -1;
    private int year = -1;
    private MonthFragmentSummaryCallBack sumCb;
    private View.OnClickListener monthOnClick;

    public MonthFragmentAdapter(@NonNull FragmentActivity fragmentActivity,
                                int employeeId, int year, MonthFragmentSummaryCallBack sumCb,
                                View.OnClickListener monthOnClick) {
        super(fragmentActivity);
        this.employeeId = employeeId;
        this.year = year;
        this.sumCb = sumCb;
        this.monthOnClick = monthOnClick;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MonthFragment.newInstance(employeeId, year, position, sumCb, monthOnClick);
    }

    @Override
    public int getItemCount() {
        return 12;
    }

}
