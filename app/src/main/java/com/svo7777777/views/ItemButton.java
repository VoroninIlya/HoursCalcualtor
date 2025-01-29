package com.svo7777777.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.google.android.material.button.MaterialButton;
import com.svo7777777.hourscalculator.R;

public class ItemButton extends MaterialButton {
    private String topLeftText = "";
    private String topRightText = "";
    private String bottomLeftText = "";
    private String bottomRightText = "";

    private Paint textPaint;

    private int subTextColor = getTextColors().getDefaultColor();
    private float subTextSize = 12f;
    private Typeface subTextFont = Typeface.DEFAULT;

    public ItemButton(Context context) {
        super(context);
        init(null, 0);
    }

    public ItemButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ItemButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ItemButton);

            topLeftText = typedArray.getString(R.styleable.ItemButton_topLeftText);
            topLeftText = topLeftText == null ? "" : topLeftText;
            topRightText = typedArray.getString(R.styleable.ItemButton_topRightText);
            topRightText = topRightText == null ? "" : topRightText;
            bottomLeftText = typedArray.getString(R.styleable.ItemButton_bottomLeftText);
            bottomLeftText = bottomLeftText == null ? "" : bottomLeftText;
            bottomRightText = typedArray.getString(R.styleable.ItemButton_bottomRightText);
            bottomRightText = bottomRightText == null ? "" : bottomRightText;

            subTextColor = typedArray.getColor(R.styleable.ItemButton_subTextColor, subTextColor);

            subTextSize = typedArray.getDimension(R.styleable.ItemButton_subTextSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, subTextSize, getResources().getDisplayMetrics()));

            subTextFont = getTypeface();

            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // drawing sub texts
        textPaint.setTypeface(subTextFont);
        textPaint.setTextSize(subTextSize);
        textPaint.setColor(subTextColor);
        textPaint.setTextAlign(Paint.Align.LEFT);

        if(!topLeftText.isEmpty()) {
            Rect topLeftTextBounds = new Rect();
            textPaint.getTextBounds(topLeftText, 0, topLeftText.length(), topLeftTextBounds);

            canvas.drawText(topLeftText,
                    getPaddingLeft(),
                    topLeftTextBounds.height() + getPaddingTop(), textPaint);
        }

        if(!bottomLeftText.isEmpty()) {
            Rect bottomLeftTextBounds = new Rect();
            textPaint.getTextBounds(bottomLeftText, 0, bottomLeftText.length(), bottomLeftTextBounds);

            canvas.drawText(bottomLeftText,
                    getPaddingLeft(),
                    getHeight() - getPaddingBottom(), textPaint);
        }

        if(!topRightText.isEmpty()) {
            Rect topRightTextBounds = new Rect();
            textPaint.getTextBounds(topRightText, 0, topRightText.length(), topRightTextBounds);

            canvas.drawText(topRightText,
                    getWidth() - topRightTextBounds.width() - getPaddingRight(),
                    topRightTextBounds.height() + getPaddingTop(), textPaint);
        }

        if(!bottomRightText.isEmpty()) {
            Rect bottomRightTextBounds = new Rect();
            textPaint.getTextBounds(bottomRightText, 0, bottomRightText.length(), bottomRightTextBounds);

            canvas.drawText(bottomRightText,
                    getWidth() - bottomRightTextBounds.width() - getPaddingRight(),
                    getHeight() - getPaddingBottom(), textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setTopLeftText(String text) {
        this.topLeftText = text;
        invalidate();
    }

    public void setTopRightText(String text) {
        this.topRightText = text;
        invalidate();
    }

    public void setBottomLeftText(String text) {
        this.bottomLeftText = text;
        invalidate();
    }

    public void setBottomRightText(String text) {
        this.bottomRightText = text;
        invalidate();
    }

    public void setSubTextColor(int color) {
        this.subTextColor = color;
        invalidate();
    }

    public void setSubTextSize(float size) {
        this.subTextSize = size;
        invalidate();
    }

    public void setSubTextFont(Typeface subTextFont) {
        this.subTextFont = subTextFont;
        invalidate();
    }
}
