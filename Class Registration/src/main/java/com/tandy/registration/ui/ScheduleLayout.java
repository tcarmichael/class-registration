package com.tandy.registration.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.tandy.registration.R;

public class ScheduleLayout extends ViewGroup {
    private int mColumns = 3;

    private TimeRulerView mRulerView;

    public ScheduleLayout(Context context) {
        this(context, null);
    }

    public ScheduleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BlocksLayout, defStyle, 0);

        mColumns = a.getInt(R.styleable.TimeRulerView_headerWidth, mColumns);

        a.recycle();
    }

    private void ensureChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof TimeRulerView) {
                mRulerView = (TimeRulerView) getChildAt(i);
                mRulerView.setDrawingCacheEnabled(true);
                return;
            }
        }

        throw new IllegalStateException("Must include a TimeRulerView.");
    }

    /**
     * Remove any {@link BlockView} instances, leaving only
     * {@link TimeRulerView} remaining.
     */
    public void removeAllBlocks() {
        ensureChildren();
        removeAllViews();
        addView(mRulerView);
    }

    public void addBlock(ClassView classView) {
        classView.setDrawingCacheEnabled(true);
        addView(classView, 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureChildren();

        mRulerView.measure(widthMeasureSpec, heightMeasureSpec);

        final int width = mRulerView.getMeasuredWidth();
        final int height = mRulerView.getMeasuredHeight();

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureChildren();

        final TimeRulerView rulerView = mRulerView;
        final int headerWidth = rulerView.getHeaderWidth();
        final int columnWidth = (getWidth() - headerWidth) / mColumns;

        rulerView.layout(0, 0, getWidth(), getHeight());

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            if (child instanceof ClassView) {
                final ClassView classView = (ClassView) child;
                final int top = rulerView.getTimeVerticalOffset(classView.getStartTime());
                final int bottom = rulerView.getTimeVerticalOffset(classView.getEndTime());
                final int left = headerWidth + (classView.getColumn() * columnWidth);
                final int right = left + columnWidth;
                child.layout(left, top, right, bottom);
            }
        }
    }
}
