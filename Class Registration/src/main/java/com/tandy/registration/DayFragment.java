package com.tandy.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tandy.registration.activities.ClassDetailActivity;
import com.tandy.registration.database.ClassLoader;
import com.tandy.registration.ui.ClassView;
import com.tandy.registration.ui.ScheduleLayout;

import java.util.List;

public class DayFragment extends Fragment {
	
	private static final String DAY = "day";

    //0 = Monday, 1 = Tuesday, etc. 
    private int mDay;
    private static List<ClassLoader.Result> sClassList;

    static DayFragment newInstance(int day) {
        DayFragment newFragment = new DayFragment();

        Bundle args = new Bundle();
        args.putInt(DAY, day);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDay = getArguments() != null ? getArguments().getInt(DAY) : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View parentView = inflater.inflate(R.layout.schedule_fragment, container, false);
        if (sClassList == null) return parentView;
        final ScheduleLayout scheduleView = (ScheduleLayout) parentView.findViewById(R.id.schedule);

        for (ClassLoader.Result result : sClassList) {
            if (result.hasDay(mDay)) {
                Intent intent = new Intent(getActivity().getBaseContext(), ClassDetailActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(result.getUri());
                
                final ClassView view = new ClassView(getActivity().getBaseContext(),
                        result.toString(),
                        result.getStartTime(mDay),
                        result.getEndTime(mDay),
                        false,
                        mDay);
                
                view.setTag(intent);
                view.setOnClickListener(LISTENER);
                scheduleView.addBlock(view);
            }
        }

        return parentView;
    }
    
    public static void setClasses(List<ClassLoader.Result> classList) {
    	sClassList = classList;
    }
    
    private final OnClickListener LISTENER = new OnClickListener() {
		
		public void onClick(View v) {
            Intent intent = (Intent) v.getTag();
            startActivity(intent);
		}
	};

}