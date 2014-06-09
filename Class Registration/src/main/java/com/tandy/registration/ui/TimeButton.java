package com.tandy.registration.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tandy.registration.R;
import com.tandy.registration.util.Utilities;

import java.util.Calendar;

public class TimeButton extends Button {
    
    private long mTime;
    private TimeButton mPair;
    private boolean mIsStartTime;
    private int id;

    public TimeButton(Context context) {
        super(context);
    }

    public TimeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeButton, defStyle, 0);
        
        //Set time for this button
        Calendar convertTime = Calendar.getInstance(Utilities.DEFAULT_TIMEZONE);
        convertTime.clear();
        convertTime.set(Calendar.HOUR_OF_DAY, a.getInteger(R.styleable.TimeButton_hour, 8));
        convertTime.set(Calendar.MINUTE, a.getInteger(R.styleable.TimeButton_minute, 0));
        setFinalTime(convertTime);
        
        mIsStartTime = a.getBoolean(R.styleable.TimeButton_isStartTime, false);
        
        id = a.getResourceId(R.styleable.TimeButton_pair, 0);
        
        //Sets background to pre-Honeycomb Spinner on pre-Honeycomb devices
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) setBackgroundResource(android.R.drawable.btn_dropdown);
        
        a.recycle();
    }
    
    public void setTime(long time) {
        //Find paired button
        if (mPair == null) {
            mPair = (TimeButton) ((LinearLayout) getParent()).findViewById(id);
        }
        
        //Convert time to Calendar object
        Calendar convertTime = Calendar.getInstance(Utilities.DEFAULT_TIMEZONE);
        convertTime.clear();
        convertTime.setTimeInMillis(time);
        
        //Set time for this button
        setFinalTime(convertTime);
        
        //Validate that paired button is in sync with this button
        if (mIsStartTime) {
            if (mPair.mTime < mTime) {
                convertTime.add(Calendar.MINUTE, 55);
                
                mPair.setFinalTime(convertTime);
            }
        } else {
            if (mPair.mTime > mTime) {
                convertTime.add(Calendar.MINUTE, -55);
                
                mPair.setFinalTime(convertTime);
            }
        }
    }
    
    private void setFinalTime(Calendar time) {
        //Validate start time
        if (time.get(Calendar.HOUR_OF_DAY) < 8) {
            time.set(Calendar.HOUR_OF_DAY, 8);
            time.set(Calendar.MINUTE, 0);
        }
        
        //Validate end time
        if (time.get(Calendar.HOUR_OF_DAY) >= 21) {
            time.set(Calendar.HOUR_OF_DAY, 21);
            time.set(Calendar.MINUTE, 0);
        }
        
        //Save time for this button
        mTime = time.getTimeInMillis();
        
        //Display time for this button
        if (DateFormat.is24HourFormat(getContext())) {
            setText(DateFormat.format("k:mm", time));
        } else {
            setText(DateFormat.format("h:mmaa", time));
        }
    }
    
    public long getTime() {
        return mTime;
    }
    
    public boolean isStartTime() {
        return mIsStartTime;
    }

}
