package com.tandy.registration.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.tandy.registration.Subject;
import com.tandy.registration.util.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ClassLoader extends AsyncTaskLoader<List<ClassLoader.Result>> {
    
    private final Uri mUri;
    private final String mWhere;
    private final String mWhereArgs[];
    
    private interface ClassQuery {
        static final String COLUMNS[] = {
            ClassDatabase.Columns.Classes.ID,
            ClassDatabase.Columns.Classes.SCHEDULE_ID,
            ClassDatabase.Columns.Classes.SUBJECT,
            ClassDatabase.Columns.Classes.COURSE_TITLE,
            ClassDatabase.Columns.Classes.COURSE_NUMBER,
            ClassDatabase.Columns.Classes.CREDIT_HOURS,
            ClassDatabase.Columns.Classes.CRN,
            ClassDatabase.Columns.Classes.ONLINE,

            ClassDatabase.Columns.Classes.MONDAY,
            ClassDatabase.Columns.Classes.TUESDAY,
            ClassDatabase.Columns.Classes.WEDNESDAY,
            ClassDatabase.Columns.Classes.THURSDAY,
            ClassDatabase.Columns.Classes.FRIDAY,
            
            ClassDatabase.Columns.Classes.MONDAY_START,
            ClassDatabase.Columns.Classes.TUESDAY_START,
            ClassDatabase.Columns.Classes.WEDNESDAY_START,
            ClassDatabase.Columns.Classes.THURSDAY_START,
            ClassDatabase.Columns.Classes.FRIDAY_START,
            
            ClassDatabase.Columns.Classes.MONDAY_END,
            ClassDatabase.Columns.Classes.TUESDAY_END,
            ClassDatabase.Columns.Classes.WEDNESDAY_END,
            ClassDatabase.Columns.Classes.THURSDAY_END,
            ClassDatabase.Columns.Classes.FRIDAY_END
        };
        
        final static int ID = 0;
        final static int SCHEDULE_ID = 1;
        final static int SUBJECT = 2;
        final static int COURSE_TITLE = 3;
        final static int COURSE_NUMBER = 4;
        final static int CREDIT_HOURS = 5;
        final static int CRN = 6;
        final static int ONLINE = 7;
        
        final static int MONDAY = 8;
        final static int TUESDAY = 9;
        final static int WEDNESDAY = 10;
        final static int THURSDAY = 11;
        final static int FRIDAY = 12;
        
        final static int MONDAY_START = 13;
        final static int TUESDAY_START = 14;
        final static int WEDNESDAY_START = 15;
        final static int THURSDAY_START = 16;
        final static int FRIDAY_START = 17;

        final static int MONDAY_END = 18;
        final static int TUESDAY_END = 19;
        final static int WEDNESDAY_END = 20;
        final static int THURSDAY_END = 21;
        final static int FRIDAY_END = 22;
    }

    public static final class Result {

        private final int ID;
        private final int SCHEDULE_ID;
        private final Subject SUBJECT;
        private final int COURSE_NUMBER;
        private final String COURSE_TITLE;
        private final int CREDIT_HOURS;
        private final int CRN;
        private final boolean ONLINE;
        private final boolean DAYS[];
        private final long START_TIMES[];
        private final long END_TIMES[];
        private final Uri URI;

        private Result(int id, int scheduleId, Subject subject, int courseNumber,
                String courseTitle, int creditHours, int crn, boolean online, boolean[] days,
                long[] startTimes, long[] endTimes, Uri uri) {
            ID = id;
            SCHEDULE_ID = scheduleId;
            SUBJECT = subject;
            COURSE_NUMBER = courseNumber;
            COURSE_TITLE = courseTitle;
            CREDIT_HOURS = creditHours;
            CRN = crn;
            ONLINE = online;
            DAYS = days;
            START_TIMES = startTimes;
            END_TIMES = endTimes;
            URI = uri;
        }

        @Override
        public String toString() {
            return SUBJECT.name() + " " + Integer.toString(COURSE_NUMBER) + " - " + COURSE_TITLE;
        }
        
        public String getShortName() {
            return SUBJECT.name() + " " + Integer.toString(COURSE_NUMBER);
        }
        
        public Subject getSubject() {
            return SUBJECT;
        }
        
        public int getCourseNumber() {
            return COURSE_NUMBER;
        }
        
        public String getCourseTitle() {
            return COURSE_TITLE;
        }
        
        public int getCreditHours() {
            return CREDIT_HOURS;
        }
        
        public long getStartTime(int position) {
            return START_TIMES[position];
        }
        
        public long getEndTime(int position) {
            return END_TIMES[position];
        }
        
        public boolean hasDay(int day) {
            if (ONLINE) return false;
            return DAYS[day];
        }
        
        public int getCrn() {
            return CRN;
        }
        
        public boolean getOnline() {
            return ONLINE;
        }
        
        public Uri getUri() {
            return URI;
        }
        
        public String getTimeString() {
            if (ONLINE) {
                return "Online";
            }
            
            String timeString = "";
            char[] dayLabels = {'M', 'T', 'W', 'R', 'F'};
            long[][] times = new long[5][2];
            boolean unique;
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mmaa");
            timeFormat.setTimeZone(Utilities.DEFAULT_TIMEZONE);
            
            Calendar timeInfo = Calendar.getInstance(Utilities.DEFAULT_TIMEZONE);
            
            for (int i = 0; i < DAYS.length; i++) {
                
                if (DAYS[i]) {
                    unique = true;
                    
                    for (long[] compareTimes : times) {
                        if (compareTimes[0] == START_TIMES[i] && compareTimes[1] == END_TIMES[i]) {
                            unique = false;
                        }
                    }
                    
                    if (unique) {
                        if (!timeString.contentEquals("")) {
                            timeString += "\n";
                        }
                        
                        times[i] = new long[] {START_TIMES[i], END_TIMES[i]};
                        timeString  += dayLabels[i];
                        
                        for (int j = i + 1; j < DAYS.length; j++) {
                            
                            if (DAYS[j]) {
                                if (times[i][0] == START_TIMES[j] && times[i][1] == END_TIMES[j]) {
                                    timeString += dayLabels[j];
                                }
                            }
                        }

                        timeInfo.setTimeInMillis(START_TIMES[i]);
                        timeString += " " + timeFormat.format(timeInfo.getTime());
                        
                        timeInfo.setTimeInMillis(END_TIMES[i]);
                        timeString += " - " + timeFormat.format(timeInfo.getTime());
                    }
                    
                    
                }
            }
            
            return timeString;
        }
    }

    public ClassLoader(Context context, Uri uri, String where, String whereArgs[]) {
        super(context);
        mUri = uri;
        mWhere = where;
        mWhereArgs = whereArgs;
    }

    @Override
    public List<Result> loadInBackground() {
        final Cursor cursor = getContext().getContentResolver().query(mUri, ClassQuery.COLUMNS, mWhere, mWhereArgs, null);
        
        if (cursor == null) {
            return null;
        }
        
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }
            
            final List<Result> results = new LinkedList<Result>();
            for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                boolean days[] = new boolean[5];
                long startTimes[] = new long[5];
                long endTimes[] = new long[5];

                days[0] = cursor.getInt(ClassQuery.MONDAY) != 0;
                days[1] = cursor.getInt(ClassQuery.TUESDAY) != 0;
                days[2] = cursor.getInt(ClassQuery.WEDNESDAY) != 0;
                days[3] = cursor.getInt(ClassQuery.THURSDAY) != 0;
                days[4] = cursor.getInt(ClassQuery.FRIDAY) != 0;

                startTimes[0] = cursor.getLong(ClassQuery.MONDAY_START);
                startTimes[1] = cursor.getLong(ClassQuery.TUESDAY_START);
                startTimes[2] = cursor.getLong(ClassQuery.WEDNESDAY_START);
                startTimes[3] = cursor.getLong(ClassQuery.THURSDAY_START);
                startTimes[4] = cursor.getLong(ClassQuery.FRIDAY_START);

                endTimes[0] = cursor.getLong(ClassQuery.MONDAY_END);
                endTimes[1] = cursor.getLong(ClassQuery.TUESDAY_END);
                endTimes[2] = cursor.getLong(ClassQuery.WEDNESDAY_END);
                endTimes[3] = cursor.getLong(ClassQuery.THURSDAY_END);
                endTimes[4] = cursor.getLong(ClassQuery.FRIDAY_END);
                
                results.add(new Result(
                        cursor.getInt(ClassQuery.ID),
                        cursor.getInt(ClassQuery.SCHEDULE_ID),
                        Subject.values()[cursor.getInt(ClassQuery.SUBJECT)],
                        cursor.getInt(ClassQuery.COURSE_NUMBER),
                        cursor.getString(ClassQuery.COURSE_TITLE),
                        cursor.getInt(ClassQuery.CREDIT_HOURS),
                        cursor.getInt(ClassQuery.CRN),
                        cursor.getInt(ClassQuery.ONLINE) != 0,
                        days,
                        startTimes,
                        endTimes,
                        Uri.withAppendedPath(ClassProvider.CLASS_URI, Integer.toString(cursor.getInt(ClassQuery.ID)))));
            }
            
            return results;
        } finally {
            cursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
