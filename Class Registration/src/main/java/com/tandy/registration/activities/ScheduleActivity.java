package com.tandy.registration.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.tandy.registration.R;
import com.tandy.registration.SchedulePagerAdapter;
import com.tandy.registration.database.ClassDatabase;
import com.tandy.registration.database.ClassLoader;
import com.tandy.registration.database.ClassLoader.Result;
import com.tandy.registration.database.ClassProvider;

import java.util.List;

public class ScheduleActivity extends ActionBarActivity {
    
    private static final int SCHEDULE_LOADER = 0;
    private static final int CLASS_LOADER = 1;
    private static final String SELECTED_DAY = "selected_day";
    private static final String CURRENT_SCHEDULE = "current_schedule";
    static final int NUM_DAYS = 5;
    
    private SparseArray<String> mSchedules;
    private SchedulePagerAdapter mAdapter;
    private ViewPager mPager;
    private int mCurrentSchedule = -1;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.schedule_view);
        
		getSupportLoaderManager().initLoader(SCHEDULE_LOADER, null,
				mScheduleLoaderListener);

        mPager = (ViewPager) findViewById(R.id.schedule_frame);
        
        if (savedInstanceState != null) {
//            mPager.setCurrentItem(savedInstanceState.getInt(SELECTED_DAY, 0));
            mCurrentSchedule = savedInstanceState.getInt(CURRENT_SCHEDULE, -1);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_DAY, mPager.getCurrentItem());
        outState.putInt(CURRENT_SCHEDULE, mCurrentSchedule);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.schedule_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(this, AddClassActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.putExtra(ClassDatabase.Columns.Classes.SCHEDULE_ID, mCurrentSchedule);
                startActivity(intent);
                return true;

            case R.id.select_schedule:
                selectSchedule();
                return true;

            case R.id.credit_hours:
                AlertDialog.Builder creditHoursView = new AlertDialog.Builder(this);
                creditHoursView.setTitle(mSchedules.get(mCurrentSchedule));
                creditHoursView.setPositiveButton("OK", null);
                creditHoursView.setMessage(Integer.toString(mAdapter.getCreditHours()) + " credit hours");
                creditHoursView.create().show();
                return true;

            case R.id.view_crns:

                final AlertDialog.Builder crnView = new AlertDialog.Builder(this);
                crnView.setTitle("CRNs for " + mSchedules.get(mCurrentSchedule));
                crnView.setPositiveButton("OK", null);
                
                final Intent crnViewIntent = new Intent(getBaseContext(), ClassDetailActivity.class);
                crnViewIntent.setAction(Intent.ACTION_VIEW);
                crnView.setItems(mAdapter.getCrns(), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        crnViewIntent.setData(mAdapter.getClassInfo(item).getUri());
                        startActivity(crnViewIntent);
                    }
                });

                crnView.create().show();
                return true;
                
            case R.id.view_classes:
                final AlertDialog.Builder classView = new AlertDialog.Builder(this);
                classView.setTitle("Classes for " + mSchedules.get(mCurrentSchedule));
                classView.setPositiveButton("OK", null);
                
                final Intent classNameViewIntent = new Intent(getBaseContext(), ClassDetailActivity.class);
                classNameViewIntent.setAction(Intent.ACTION_VIEW);
                classView.setItems(mAdapter.getClassNames(), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        classNameViewIntent.setData(mAdapter.getClassInfo(item).getUri());
                        startActivity(classNameViewIntent);
                    }
                });

                classView.create().show();
                return true;
                
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectSchedule() {

        final CharSequence[] schedulesAdapter = new CharSequence[mSchedules.size()];
        for (int i = 0; i < mSchedules.size(); i++) {
            schedulesAdapter[i] = mSchedules.valueAt(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select schedule");
        builder.setSingleChoiceItems(schedulesAdapter, mSchedules.indexOfKey(mCurrentSchedule), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                pickSchedule(mSchedules.keyAt(item));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.setNeutralButton("New Schedule", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                newSchedule();
            }
        });

        builder.create().show();
    }

    public void pickSchedule(final int scheduleId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mSchedules.get(scheduleId));

        builder.setItems(R.array.schedule_actions, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        viewSchedule(scheduleId);
                        break;

                    case 1:
                        renameSchedule(scheduleId);
                        break;

                    case 2:
                        final AlertDialog.Builder confirm = new AlertDialog.Builder(ScheduleActivity.this);
                        
                        confirm.setTitle("Delete schedule");
                        confirm.setMessage("Delete " + mSchedules.get(scheduleId) + "?");
                        confirm.setNegativeButton("Cancel", null);
                        confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            
                            public void onClick(DialogInterface dialog, int which) {
                                if (scheduleId == mCurrentSchedule) mCurrentSchedule = -1;
                                getContentResolver().delete(Uri.withAppendedPath(ClassProvider.SCHEDULE_URI, Integer.toString(scheduleId)), null, null);
                            }
                        });
                        
                        confirm.create().show();
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    public void newSchedule() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Schedule");

        final View view = getLayoutInflater().inflate(R.layout.new_schedule_dialog, null);
        final EditText scheduleName = (EditText) view.findViewById(R.id.schedule_name);

        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", null);
        
        final AlertDialog shower = builder.create();
        
        shower.show();
        
        shower.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                mCurrentSchedule = -1;
                values.put(ClassDatabase.Columns.Schedules.SCHEDULE_NAME, scheduleName.getText().toString());
                Uri newSchedule = getContentResolver().insert(ClassProvider.SCHEDULE_URI, values);
                mCurrentSchedule = Integer.parseInt(newSchedule.getLastPathSegment());
                
                Bundle args = new Bundle();
                args.putString(ClassDatabase.Columns.Classes.SCHEDULE_ID, newSchedule.getLastPathSegment());
						getSupportLoaderManager().restartLoader(CLASS_LOADER,
								args, mClassLoaderListener);
                shower.dismiss();
            }
        });
    }

    public void viewSchedule(final int scheduleId) {
        if (scheduleId == mCurrentSchedule) return;
		getActionBar().setSubtitle(mSchedules.get(scheduleId));
        
        mCurrentSchedule = scheduleId;
        
        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(ClassDatabase.Columns.Classes.SCHEDULE_ID, Integer.toString(scheduleId));
		getSupportLoaderManager().destroyLoader(CLASS_LOADER);
		getSupportLoaderManager().initLoader(CLASS_LOADER, loaderArgs,
				mClassLoaderListener);
    }

    public void renameSchedule(final int scheduleId) {
        final View view = getLayoutInflater().inflate(R.layout.new_schedule_dialog, null);
        final EditText scheduleName = (EditText) view.findViewById(R.id.schedule_name);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Rename " + mSchedules.get(scheduleId));
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Done", null);
        
        final AlertDialog shower = builder.create();
        
        shower.show();
        
        shower.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(ClassDatabase.Columns.Schedules.SCHEDULE_NAME, scheduleName.getText().toString());
                getContentResolver().update(
                        Uri.withAppendedPath(ClassProvider.SCHEDULE_URI, Integer.toString(scheduleId)),
                        values,
                        null,
                        null);
                shower.dismiss();
            }
        });
    }
    
	private final LoaderManager.LoaderCallbacks<Cursor> mScheduleLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {

		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getBaseContext(),
					ClassProvider.SCHEDULE_URI, new String[] {
							ClassDatabase.Columns.Schedules.ID,
							ClassDatabase.Columns.Schedules.SCHEDULE_NAME },
					null, null, null);
		}

		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (cursor == null) {
				mSchedules = null;
				return;
			}

			if (!cursor.moveToFirst()) {
				cursor.close();
				return;
			}

			if (mCurrentSchedule == -1) {
				cursor.moveToLast();
				mCurrentSchedule = cursor.getInt(0);
				cursor.moveToFirst();
			}

			mSchedules = new SparseArray<String>(cursor.getCount());

			for (; !cursor.isAfterLast(); cursor.moveToNext()) {
				mSchedules.append(cursor.getInt(0), cursor.getString(1));
				if (cursor.getInt(0) == mCurrentSchedule) {
					getActionBar().setSubtitle(cursor.getString(1));

					Bundle args = new Bundle();
					args.putString(ClassDatabase.Columns.Classes.SCHEDULE_ID,
							Integer.toString(cursor.getInt(0)));
					getSupportLoaderManager().initLoader(CLASS_LOADER, args,
							mClassLoaderListener);
				}
			}
		}

		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private final LoaderManager.LoaderCallbacks<List<Result>> mClassLoaderListener = new LoaderManager.LoaderCallbacks<List<Result>>() {

		public void onLoaderReset(Loader<List<Result>> results) {
			mAdapter = null;
		}

		public void onLoadFinished(Loader<List<Result>> loader,
				List<Result> results) {
			int currentItem = mPager.getCurrentItem();

			mAdapter = new SchedulePagerAdapter(getSupportFragmentManager(),
					results);

			mPager.setAdapter(mAdapter);
			mPager.setCurrentItem(currentItem);
		}

		public Loader<List<Result>> onCreateLoader(int id, Bundle args) {
			return new ClassLoader(
					getBaseContext(),
					ClassProvider.CLASS_URI,
					ClassDatabase.Columns.Classes.SCHEDULE_ID + "=?",
					new String[] { args
							.getString(ClassDatabase.Columns.Classes.SCHEDULE_ID) });
		}
	};

}