package com.tandy.registration.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.tandy.registration.R;
import com.tandy.registration.util.Utilities;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    
    public static final String ARG_TIME = "time";
    public static final String ARG_HOST_ID = "host_id";
    
    final int startButtonIds[] = {R.id.monday_start, R.id.tuesday_start, R.id.wednesday_start, R.id.thursday_start, R.id.friday_start};
    final int endButtonIds[] = {R.id.monday_end, R.id.tuesday_end, R.id.wednesday_end, R.id.thursday_end, R.id.friday_end};

    private int mHour, mMinute;
    private TimeButton mHost;
    private boolean mSetAll = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments().containsKey(ARG_TIME)) {
            Calendar convertTime = Calendar.getInstance(Utilities.DEFAULT_TIMEZONE);
            convertTime.setTimeInMillis(getArguments().getLong(ARG_TIME));
            
            mHour = convertTime.get(Calendar.HOUR_OF_DAY);
            mMinute = convertTime.get(Calendar.MINUTE);
        } else {
            mHour = 8;
            mMinute = 0;
        }
        
        if (getArguments().containsKey(ARG_HOST_ID)) {
            mHost = (TimeButton) getActivity().findViewById(getArguments().getInt(ARG_HOST_ID));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new CustomTimeDialog(getActivity(), this, mHour, mMinute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        Calendar displayTime = Calendar.getInstance(Utilities.DEFAULT_TIMEZONE);
        displayTime.clear();
        displayTime.set(Calendar.HOUR_OF_DAY, hour);
        displayTime.set(Calendar.MINUTE, minute);
        
        if (mSetAll) {
            if (mHost.isStartTime()) {
                for (int i = 0; i < startButtonIds.length; i++) {
                    ((TimeButton) getActivity().findViewById(startButtonIds[i])).setTime(displayTime.getTimeInMillis());
                }
            } else {
                for (int i = 0; i < endButtonIds.length; i++) {
                    ((TimeButton) getActivity().findViewById(endButtonIds[i])).setTime(displayTime.getTimeInMillis());
                }
            }
        } else {
            mHost.setTime(displayTime.getTimeInMillis());
        }
    }

    public class CustomTimeDialog extends TimePickerDialog {
        
        TimePicker mTimePicker;

        public CustomTimeDialog(Context context, OnTimeSetListener callBack, int hourOfDay,
                int minute, boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
            setButton(BUTTON_NEUTRAL, "Set All", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    mSetAll = true;
                    CustomTimeDialog.this.onClick(dialog, which);
                }
            });
        }
    }
}
