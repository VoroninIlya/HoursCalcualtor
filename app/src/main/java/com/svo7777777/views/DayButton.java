package com.svo7777777.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.svo7777777.hourscalculator.R;

public class DayButton extends ItemButton {
    private static final int[] STATE_INSIDE_DB = { R.attr.state_inside_db };
    private static final int[] STATE_IS_WEEKEND = { R.attr.state_is_weekend };

    private boolean isInsideDb = false;
    private boolean isWeekend = false;

    public DayButton(Context context) {
        super(context);
    }

    public DayButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DayButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (isInsideDb) {
            mergeDrawableStates(drawableState, STATE_INSIDE_DB);
        }
        if (isWeekend) {
            mergeDrawableStates(drawableState, STATE_IS_WEEKEND);
        }
        return drawableState;
    }

    public void setIsInsideDb(boolean insideDb) {
        isInsideDb = insideDb;
        refreshDrawableState();
    }

    public boolean isInsideDb() {
        return isInsideDb;
    }

    public void setIsWeekend(boolean weekend) {
        isWeekend = weekend;
        refreshDrawableState();
    }

    public boolean isWeekend() {
        return isWeekend;
    }
}
