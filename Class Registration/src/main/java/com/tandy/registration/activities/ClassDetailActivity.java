package com.tandy.registration.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tandy.registration.R;
import com.tandy.registration.database.ClassLoader;

import java.util.List;

public class ClassDetailActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<ClassLoader.Result>> {
	
    private Uri mClassUri;
	TextView mClassSubject, mClassName, mCreditHours, mTimes, mCrn;
	View mTitleWrapper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.view_class);
	    
	    //Load views
	    mClassSubject = (TextView) findViewById(R.id.class_subject);
	    mClassName = (TextView) findViewById(R.id.class_name);
	    mCreditHours = (TextView) findViewById(R.id.credit_hours);
	    mTimes = (TextView) findViewById(R.id.times);
	    mCrn = (TextView) findViewById(R.id.crn);
	    
	    mTitleWrapper = findViewById(R.id.title_wrapper);
	    
	    //Get class info
	    Intent intent = getIntent();
	    mClassUri = intent.getData();

	    getSupportLoaderManager().initLoader(0, null, this);
        
        //Required to marquee text
        mClassName.setSelected(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.view_class, menu);
    	
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
			
		case R.id.delete:
		    getContentResolver().delete(
		            mClassUri,
		            null,
		            null);
			finish();
			break;
            
        case R.id.edit:
            Intent intent = new Intent(this, AddClassActivity.class);
            intent.setAction(Intent.ACTION_EDIT);
            intent.setData(mClassUri);
            startActivity(intent);
            break;
        }
        
        return true;
	}

    public Loader<List<ClassLoader.Result>> onCreateLoader(int id, Bundle args) {
        return new ClassLoader(getBaseContext(), mClassUri, null, null);
    }

    @Override
    public void onLoadFinished(Loader<List<ClassLoader.Result>> loader, List<ClassLoader.Result> data) {
        ClassLoader.Result result = data.get(0);

        mClassSubject.setText(result.getShortName());
        mClassName.setText(result.getCourseTitle());
        mCreditHours.setText(Integer.toString(result.getCreditHours()));
        mTimes.setText(result.getTimeString());
        mCrn.setText(Integer.toString(result.getCrn()));

        getActionBar().setTitle(result.getShortName());
    }

    @Override
    public void onLoaderReset(Loader<List<ClassLoader.Result>> loader) {

    }
}
