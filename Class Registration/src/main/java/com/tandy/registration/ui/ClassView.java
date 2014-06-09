package com.tandy.registration.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Button;

import com.tandy.registration.R;
import com.tandy.registration.util.Utilities;

import java.util.Calendar;

public class ClassView extends Button {

    private static final int TEXT_COLOR = Color.WHITE;

    private String mTitle = "Unknown Class";
    private long mStartTime;
    private long mEndTime;
    private boolean mContainsStarred = false;
    private int mColumn = 0;

    private TextPaint mTextPaint;
    
    public ClassView(Context context) {
        super(context);
        init(null, 0);
    }

    private ClassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    
    private ClassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        
        Calendar calendar = Calendar.getInstance(Utilities.DEFAULT_TIMEZONE);
        calendar.clear();
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.HOUR, 9);
        mStartTime = calendar.getTimeInMillis();
        
        calendar.add(Calendar.HOUR, 1);
        mEndTime = calendar.getTimeInMillis();

        LayerDrawable buttonDrawable = (LayerDrawable)
                getContext().getResources().getDrawable(R.drawable.btn_block);
        buttonDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.ttu_gold), PorterDuff.Mode.SRC_ATOP);
        buttonDrawable.getDrawable(1).setAlpha(mContainsStarred ? 255 : 0);

        setTextColor(TEXT_COLOR);
        setBackgroundDrawable(buttonDrawable);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    // BackgroundDrawable is implemented "properly"
    public ClassView(Context context, String title, long startTime,
            long endTime, boolean containsStarred, int color) {
        super(context);
        
        mTitle = title;
        mStartTime = startTime;
        mEndTime = endTime;
        mContainsStarred = containsStarred;

        setText(mTitle);

        int accentColor = getResources().getColor(R.color.ttu_gold);

        LayerDrawable buttonDrawable = (LayerDrawable)
                context.getResources().getDrawable(R.drawable.btn_block);
        buttonDrawable.getDrawable(0).setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
        buttonDrawable.getDrawable(1).setAlpha(mContainsStarred ? 255 : 0);

        setTextColor(TEXT_COLOR);
        setBackgroundDrawable(buttonDrawable);
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public int getColumn() {
        return mColumn;
    }
}
