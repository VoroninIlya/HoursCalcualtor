package com.svilvo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.svilvo.hourscalculator.R;

public class DayButton extends MaterialCardView {
    private TextView topLeftText;
    private TextView topRightText;
    private TextView bottomLeftText;
    private TextView bottomRightText;
    private TextView centerText;
    private ConstraintLayout constraintLayout;
    private static final int[] STATE_INSIDE_DB = { R.attr.stateInsideDb };
    private static final int[] STATE_IS_WEEKEND = { R.attr.stateIsWeekend };
    private boolean isInsideDb = false;
    private boolean isWeekend = false;

    public DayButton(Context context) {
        super(context);
        init(null);
    }

    public DayButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DayButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.item_card_day, this, true);

        topLeftText = findViewById(R.id.topLeftText);
        topRightText = findViewById(R.id.topRightText);
        bottomLeftText = findViewById(R.id.bottomLeftText);
        bottomRightText = findViewById(R.id.bottomRightText);
        centerText = findViewById(R.id.centerText);
        constraintLayout = findViewById(R.id.constraint);

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.DayButton,
                    0, 0);
            try {
                isInsideDb = a.getBoolean(R.styleable.DayButton_stateInsideDb, false);
                isWeekend = a.getBoolean(R.styleable.DayButton_stateIsWeekend, false);

                String center = a.getString(R.styleable.DayButton_centerText);
                String topLeft = a.getString(R.styleable.DayButton_topLeftText);
                String topRight = a.getString(R.styleable.DayButton_topRightText);
                String bottomLeft = a.getString(R.styleable.DayButton_bottomLeftText);
                String bottomRight = a.getString(R.styleable.DayButton_bottomRightText);

                if (center != null) {
                    centerText.setText(center);
                }
                if (topLeft != null) {
                    topLeftText.setText(topLeft);
                }
                if (topRight != null) {
                    topRightText.setText(topRight);
                }
                if (bottomLeft != null) {
                    bottomLeftText.setText(bottomLeft);
                }
                if (bottomRight != null) {
                    bottomRightText.setText(bottomRight);
                }
            } finally {
                a.recycle();
            }
        }
        refreshBackGround();
        refreshDrawableState();
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
        refreshBackGround();
        refreshDrawableState();
    }

    public void setIsWeekend(boolean weekend) {
        isWeekend = weekend;
        refreshBackGround();
        refreshDrawableState();
    }

    public void setTopLeftText(String text) {
        topLeftText.setText(text);
    }

    public void setTopRightText(String text) {
        topRightText.setText(text);
    }

    public void setBottomLeftText(String text) {
        bottomLeftText.setText(text);
    }

    public void setBottomRightText(String text) {
        bottomRightText.setText(text);
    }

    public void setCenterText(String text) {
        centerText.setText(text);
    }

    private void refreshBackGround() {
        setForeground(ContextCompat.getDrawable(getContext(),R.drawable.day_overlay_drawable));
        setBackground(ContextCompat.getDrawable(getContext(),R.drawable.day_background_drawable));
    }
}
