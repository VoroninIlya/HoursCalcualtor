package com.svilvo.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.svilvo.hourscalculator.R;
import com.svilvo.hourscalculator.ui.fragments.DayItem;
import com.svilvo.views.DayButton;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
    private List<DayItem> daysList;

    public DayAdapter(List<DayItem> daysList) {
        this.daysList = daysList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayItem day = daysList.get(position);
        holder.button.setCenterText(String.format("%.2f", day.hours)); // или String.valueOf(day.hours)
        holder.button.setTopLeftText(day.dayOfWeek);
        holder.button.setTopRightText(String.valueOf(day.dayNumber));
        holder.button.setBottomRightText(String.format("%.2f", day.hours * day.price));
        holder.button.setBottomLeftText("");
        holder.button.setBackgroundColor(Color.TRANSPARENT);
        holder.cardView.setOnClickListener(day.onClick);

        CardView.LayoutParams params = new CardView.LayoutParams(
                day.itemSize,
                day.itemSize
        );

        holder.button.setLayoutParams(params);

        if (day.isWeekend) {
            holder.button.setIsWeekend(true);
        } else {
            holder.button.setIsWeekend(false);
        }

        if (day.isInsideDb) {
            holder.button.setIsInsideDb(true);
        } else {
            holder.button.setIsInsideDb(false);
        }

        if(!day.isVisible) {
            holder.button.setVisibility(View.INVISIBLE);
        } else {
            holder.button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        DayButton button;
        CardView cardView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_card);
            button = itemView.findViewById(R.id.item_button);
        }
    }

    public void addDay(DayItem day) {
        daysList.add(day);
        notifyItemInserted(daysList.size() - 1);
    }

    public boolean hasDay(int position) {
        if(position >= 0 && position < daysList.size())
            return daysList.get(position) != null;
        return false;
    }

    public DayItem getDay(int position) {
        if(position >= 0 && position < daysList.size())
            return daysList.get(position);
        return null;
    }

    public void updateDay(DayItem day) {
        int index = daysList.indexOf(day);
        if (index != -1) {
            daysList.set(index, day);
            notifyItemChanged(index);
        }
    }
}
