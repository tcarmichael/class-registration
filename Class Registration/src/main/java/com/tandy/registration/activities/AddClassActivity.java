package com.tandy.registration.activities;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.tandy.registration.R;
import com.tandy.registration.Subject;
import com.tandy.registration.database.*;
import com.tandy.registration.database.ClassLoader;
import com.tandy.registration.ui.TimeButton;
import com.tandy.registration.ui.TimePickerFragment;

import java.util.List;

public class AddClassActivity extends ActionBarActivity {

    private static final int DAY_CHECKBOX_IDS[] = {R.id.monday_check, R.id.tuesday_check, R.id.wednesday_check, R.id.thursday_check, R.id.friday_check};
    private static final int DAY_WRAPPER_IDS[] = {R.id.monday_wrapper, R.id.tuesday_wrapper, R.id.wednesday_wrapper, R.id.thursday_wrapper, R.id.friday_wrapper};
    private static final int START_TIME_IDS[] = {R.id.monday_start, R.id.tuesday_start, R.id.wednesday_start, R.id.thursday_start, R.id.friday_start};
    private static final int END_TIME_IDS[] = {R.id.monday_end, R.id.tuesday_end, R.id.wednesday_end, R.id.thursday_end, R.id.friday_end};

    public static final String NEW_CLASS = "NEW";
    public static final String EDIT_CLASS = "EDIT";

    private String mAction;
    private Uri mUri;
    private int mScheduleId = -1;
    private Spinner mSubject;
    private EditText mCourseNumber, mCourseTitle, mCreditHours, mCrn;
    private CheckBox mOnline, mDays[];
    private TimeButton mStartTimes[], mEndTimes[];

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Display custom actionbar
        final ActionBar actionBar = getSupportActionBar();
        
        final LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View actionBarButtons = inflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
        actionBarButtons.findViewById(R.id.actionbar_cancel).setOnClickListener(mActionBarListener);
        actionBarButtons.findViewById(R.id.actionbar_done).setOnClickListener(mActionBarListener);

        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(actionBarButtons, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        
        setContentView(R.layout.add_class);

        mSubject = (Spinner) findViewById(R.id.subjects);
        mCourseNumber = (EditText) findViewById(R.id.course_number);
        mCourseTitle = (EditText) findViewById(R.id.course_title);
        mCreditHours = (EditText) findViewById(R.id.credit_hours);
        mCrn = (EditText) findViewById(R.id.crn);
        mOnline = (CheckBox) findViewById(R.id.online);
        mDays = new CheckBox[DAY_CHECKBOX_IDS.length];
        mStartTimes = new TimeButton[START_TIME_IDS.length];
        mEndTimes = new TimeButton[START_TIME_IDS.length];
        for (int i = 0; i < mDays.length; i++) {
            mDays[i] = (CheckBox) findViewById(DAY_CHECKBOX_IDS[i]);
            mStartTimes[i] = (TimeButton) findViewById(START_TIME_IDS[i]);
            mEndTimes[i] = (TimeButton) findViewById(END_TIME_IDS[i]);
        }

        if (savedInstanceState != null) {
            boolean isDayChecked[] = savedInstanceState.getBooleanArray("isDayChecked");

            for (int i = 0; i < isDayChecked.length; i++) {
                if (isDayChecked[i]) {
                    findViewById(DAY_WRAPPER_IDS[i]).setVisibility(View.VISIBLE);
                }
            }
        }
        
        Intent intent = getIntent();
        mUri = intent.getData();
        mAction = intent.getAction();
        
        if (mAction.equals(Intent.ACTION_EDIT)) {
            getSupportLoaderManager().initLoader(0, null, mClassLoader);
        } else if (Intent.ACTION_INSERT.equals(mAction)) {
            mScheduleId = intent.getExtras().getInt(ClassDatabase.Columns.Classes.SCHEDULE_ID, -1);
            if (mScheduleId == -1) throw new IllegalArgumentException("Unknown schedule ID");
        }

        //Set spinner values
        mSubject.setAdapter(new ArrayAdapter<Subject>(this, R.layout.subject_spinner, Subject.values()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save state of checkboxes
        boolean isDayChecked[] = new boolean[mDays.length];

        for (int i = 0; i < mDays.length; i++) {
            isDayChecked[i] = mDays[i].isChecked();
        }

        outState.putBooleanArray("isDayChecked", isDayChecked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = mOnline.isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.online:

                for (int i = 0; i < mDays.length; i++) {
                    if (checked) {
                        //Deselect checkboxes
                        mDays[i].setChecked(false);

                        //Hide timepickers
                        findViewById(DAY_WRAPPER_IDS[i]).setVisibility(View.GONE);
                    }

                    //Enable or disable checkboxes
                    mDays[i].setEnabled(!checked);
                }
                break;


        }
    }

    public void toggleDay(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        int id = 0;

        switch(view.getId()) {
            case R.id.monday_check:
                id = R.id.monday_wrapper;
                break;

            case R.id.tuesday_check:
                id = R.id.tuesday_wrapper;
                break;

            case R.id.wednesday_check:
                id = R.id.wednesday_wrapper;
                break;

            case R.id.thursday_check:
                id = R.id.thursday_wrapper;
                break;

            case R.id.friday_check:
                id = R.id.friday_wrapper;
                break;
        }
        if (checked) {
            findViewById(id).setVisibility(View.VISIBLE);
        } else {
            findViewById(id).setVisibility(View.GONE);
        }
    }

    public void setTime(View view) {
        //Display TimePickerFragment
        TimeButton handler = (TimeButton) view;

        Bundle args = new Bundle();
        args.putLong(TimePickerFragment.ARG_TIME, handler.getTime());
        args.putInt(TimePickerFragment.ARG_HOST_ID, handler.getId());

        DialogFragment newFragment = new TimePickerFragment();

        newFragment.setArguments(args);

        newFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * Verifies that the input from the add_class form is in the correct format
     * @throws IllegalArgumentException
     */
    private void validate() throws IllegalArgumentException {
        IllegalArgumentException e = null;

        if (mCourseNumber.getText().length() != 4) {
            mCourseNumber.setError("Course number must be four digits");
            e = new IllegalArgumentException("Course number must be four digits");
        }

        if (mCourseTitle.getText().length() == 0) {
            mCourseTitle.setError("Course title is blank");

            if (e == null) {
                e = new IllegalArgumentException("Course title is blank");
            }
        }

        if (mCreditHours.getText().length() == 0 || Integer.parseInt(mCreditHours.getText().toString()) == 0) {
            mCreditHours.setError("No credit hours");

            if (e == null) {
                e = new IllegalArgumentException("No credit hours");
            }
        }

        if (mCrn.getText().length() != 5) {
            mCrn.setError("CRN must be five digits");

            if (e == null) {
                e = new IllegalArgumentException("CRN must be five digits");
            }
        }

        if (e != null) {
            throw e;
        }
    }

    /**
     * Validates the current form and ships the input into the calling Activity
     */
    public void done() {

        try {
            validate();
        } catch(IllegalArgumentException e) {
            return;
        }


        ContentValues values = new ContentValues();
        values.put(ClassDatabase.Columns.Classes.SUBJECT, mSubject.getSelectedItemPosition());
        values.put(ClassDatabase.Columns.Classes.COURSE_NUMBER, Integer.parseInt(mCourseNumber.getText().toString()));
        values.put(ClassDatabase.Columns.Classes.COURSE_TITLE, mCourseTitle.getText().toString());
        values.put(ClassDatabase.Columns.Classes.CREDIT_HOURS, Integer.parseInt(mCreditHours.getText().toString()));
        values.put(ClassDatabase.Columns.Classes.CRN, Integer.parseInt(mCrn.getText().toString()));

        boolean online = mOnline.isChecked();
        values.put(ClassDatabase.Columns.Classes.ONLINE, online);

        if (!online) {
            //Get boolean for each day
            boolean days[] = new boolean[5];
            long startTimes[] = new long[5];
            long endTimes[] = new long[5];

            for (int i = 0; i < mDays.length; i++) {
                days[i] = mDays[i].isChecked();
                
                if (mDays[i].isChecked()) {
                    startTimes[i] = mStartTimes[i].getTime();
                    endTimes[i] = mEndTimes[i].getTime();
                }
            }

            values.put(ClassDatabase.Columns.Classes.MONDAY, days[0]);
            values.put(ClassDatabase.Columns.Classes.TUESDAY, days[1]);
            values.put(ClassDatabase.Columns.Classes.WEDNESDAY, days[2]);
            values.put(ClassDatabase.Columns.Classes.THURSDAY, days[3]);
            values.put(ClassDatabase.Columns.Classes.FRIDAY, days[4]);

            values.put(ClassDatabase.Columns.Classes.MONDAY_START, mStartTimes[0].getTime());
            values.put(ClassDatabase.Columns.Classes.TUESDAY_START, mStartTimes[1].getTime());
            values.put(ClassDatabase.Columns.Classes.WEDNESDAY_START, mStartTimes[2].getTime());
            values.put(ClassDatabase.Columns.Classes.THURSDAY_START, mStartTimes[3].getTime());
            values.put(ClassDatabase.Columns.Classes.FRIDAY_START, mStartTimes[4].getTime());

            values.put(ClassDatabase.Columns.Classes.MONDAY_END, mEndTimes[0].getTime());
            values.put(ClassDatabase.Columns.Classes.TUESDAY_END, mEndTimes[1].getTime());
            values.put(ClassDatabase.Columns.Classes.WEDNESDAY_END, mEndTimes[2].getTime());
            values.put(ClassDatabase.Columns.Classes.THURSDAY_END, mEndTimes[3].getTime());
            values.put(ClassDatabase.Columns.Classes.FRIDAY_END, mEndTimes[4].getTime());
        }
        
        if (Intent.ACTION_EDIT.equals(mAction)) {
            getContentResolver().update(mUri, values, null, null);
        } else if (Intent.ACTION_INSERT.equals(mAction)) {
            values.put(ClassDatabase.Columns.Classes.SCHEDULE_ID, mScheduleId);
            getContentResolver().insert(ClassProvider.CLASS_URI, values);
        }

        finish();
    }

    private View.OnClickListener mActionBarListener = new View.OnClickListener() {

        public void onClick(View view) {
            onActionBarItemSelected(view.getId());
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_done:
                done();
                break;
            case R.id.actionbar_cancel:
                setResult(RESULT_CANCELED, getIntent());
                finish();
                break;
        }

        return true;
    }

    private boolean onActionBarItemSelected(int id) {
        switch (id) {
            case R.id.actionbar_done:
                done();
                break;
            case R.id.actionbar_cancel:
                setResult(RESULT_CANCELED, getIntent());
                finish();
                break;
        }
        return true;
    }
    
    private final LoaderManager.LoaderCallbacks<List<ClassLoader.Result>> mClassLoader = new LoaderManager.LoaderCallbacks<List<ClassLoader.Result>>() {

        public Loader<List<ClassLoader.Result>> onCreateLoader(int id, Bundle args) {
            return new ClassLoader(getBaseContext(), mUri, null, null);
        }

        public void onLoadFinished(Loader<List<ClassLoader.Result>> loader, List<ClassLoader.Result> results) {
            ClassLoader.Result result = results.get(0);
            
            mSubject.setSelection(result.getSubject().ordinal());
            mCourseNumber.setText(Integer.toString(result.getCourseNumber()));
            mCourseTitle.setText(result.getCourseTitle());
            mCreditHours.setText(Integer.toString(result.getCreditHours()));
            mCrn.setText(Integer.toString(result.getCrn()));
            mOnline.setChecked(result.getOnline());
            
            for (int i = 0; i < mDays.length; i++) {
                mDays[i].setChecked(result.hasDay(i));
                toggleDay(mDays[i]);
            }
            
            for (int i = 0; i < mStartTimes.length; i++) {
                mStartTimes[i].setTime(result.getStartTime(i));
            }
            
            for (int i = 0; i < mEndTimes.length; i++) {
                mEndTimes[i].setTime(result.getEndTime(i));
            }
        }

        public void onLoaderReset(Loader<List<ClassLoader.Result>> arg0) {
            // TODO Auto-generated method stub
            
        }
    };

}
