package com.svilvo.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class YearMonthAdapter extends RecyclerView.Adapter<YearMonthAdapter.ViewHolder> {
    private List<Integer> items;
    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(int item);
    }

    public interface OnItemChangedListener {
        void onItemChanged(int newItem);
    }

    public YearMonthAdapter(List<Integer> items,
                            OnItemClickListener onClickListener) {
        this.items = items;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setClickable(true);
        textView.setFocusable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int rippleColor = Color.parseColor("#66CCCCCC");

            RippleDrawable rippleDrawable = new RippleDrawable(
                    ColorStateList.valueOf(rippleColor),
                    null,
                    new ColorDrawable(Color.WHITE)
            );

            textView.setForeground(rippleDrawable);
        }

        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Integer item = items.get(position);
        holder.textView.setText(String.valueOf(item));
        holder.textView.setOnClickListener(v -> onClickListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
