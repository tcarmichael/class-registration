package com.tandy.registration;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tandy.registration.database.*;
import com.tandy.registration.database.ClassLoader;

import java.util.Iterator;
import java.util.List;

public class SchedulePagerAdapter extends FragmentPagerAdapter {

    private static String sDayArray[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private final List<ClassLoader.Result> mResults;
    private int mCreditHours;

    public SchedulePagerAdapter(FragmentManager fm, List<ClassLoader.Result> classList) {
        super(fm);
        mResults = classList;
        if (mResults != null) {
        	refreshCreditHours();
        }
        DayFragment.setClasses(mResults);
    }

    @Override
    public Fragment getItem(int position) {
        return DayFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sDayArray[position];
    }

    @Override
    public int getCount() {
        return sDayArray.length;
    }

    private void refreshCreditHours() {
        mCreditHours = 0;
        if (mResults == null) return;
        for (ClassLoader.Result result : mResults) {
            mCreditHours += result.getCreditHours();
        }
    }

    public int getCreditHours() {
        return mCreditHours;
    }

    public int getClassCount() {
        return mResults.size();
    }

    public ClassLoader.Result getClassInfo(int position) {
        return mResults.get(position);
    }

    public CharSequence[] getCrns() {
        final CharSequence[] crnStrings = new CharSequence[mResults.size()];
        final Iterator<ClassLoader.Result> classIterator = mResults.iterator();
        for (int i = 0; i < mResults.size(); i++) {
            crnStrings[i] = Integer.toString(classIterator.next().getCrn());
        }

        return crnStrings;
    }
    
    public CharSequence[] getClassNames() {
        final CharSequence[] classNameStrings = new CharSequence[mResults.size()];
        final Iterator<ClassLoader.Result> classIterator = mResults.iterator();
        for (int i = 0; i < mResults.size(); i++) {
            classNameStrings[i] = classIterator.next().getShortName();
        }

        return classNameStrings;
    }
}