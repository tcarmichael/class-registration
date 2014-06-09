package com.tandy.registration.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ClassDatabase extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "Classes";
    private static final int DATABASE_VERSION = 1;
    private static final String DEFAULT_SCHEDULE_NAME = "Untitled Schedule";

    public static final String SCHEDULE_TABLE_NAME = "Schedules";
    public static final String CLASSES_TABLE_NAME = "Classes";
    
    public interface Columns {
        
        public interface Schedules {
            public static final String ID = BaseColumns._ID;
            public static final String SCHEDULE_NAME = "scheduleName";
        }
        
        public interface Classes {
            public static final String ID = BaseColumns._ID;
            public static final String SCHEDULE_ID = "scheduleId";
            public static final String SUBJECT = "subject";
            public static final String COURSE_TITLE = "courseTitle";
            public static final String COURSE_NUMBER = "courseNumber";
            public static final String CREDIT_HOURS = "creditHours";
            public static final String CRN = "crn";
            public static final String ONLINE = "online";
            
            public static final String MONDAY = "monday";
            public static final String TUESDAY = "tuesday";
            public static final String WEDNESDAY = "wednesday";
            public static final String THURSDAY = "thursday";
            public static final String FRIDAY = "friday";

            public static final String MONDAY_START = "mondayStart";
            public static final String TUESDAY_START = "tuesdayStart";
            public static final String WEDNESDAY_START = "wednesdayStart";
            public static final String THURSDAY_START = "thursdayStart";
            public static final String FRIDAY_START = "fridayStart";

            public static final String MONDAY_END = "mondayEnd";
            public static final String TUESDAY_END = "tuesdayEnd";
            public static final String WEDNESDAY_END = "wednesdayEnd";
            public static final String THURSDAY_END = "thursdayEnd";
            public static final String FRIDAY_END = "fridayEnd";
        }
        
        public interface Days {
            public static final String ID = BaseColumns._ID;
            public static final String CLASS_ID = "classId";
            
            public static final String MONDAY = "monday";
            public static final String TUESDAY = "tuesday";
            public static final String WEDNESDAY = "wednesday";
            public static final String THURSDAY = "thursday";
            public static final String FRIDAY = "friday";
        }
        
    }

    public ClassDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createSchedulesTable = "create table " +
                SCHEDULE_TABLE_NAME + "(" +
                BaseColumns._ID + " integer primary key autoincrement," +
                Columns.Schedules.SCHEDULE_NAME + " string)";
        
        final String createClassesTable = "create table " +
                CLASSES_TABLE_NAME + "(" +
                BaseColumns._ID + " integer primary key autoincrement," +
                Columns.Classes.SCHEDULE_ID + " integer references " + SCHEDULE_TABLE_NAME + "(" + Columns.Schedules.ID + ")," +
                Columns.Classes.SUBJECT + " integer," +
                Columns.Classes.COURSE_TITLE + " string," +
                Columns.Classes.COURSE_NUMBER + " integer," +
                Columns.Classes.CREDIT_HOURS + " integer," +
                Columns.Classes.CRN + " integer," +
                Columns.Classes.ONLINE + " integer," +

                Columns.Classes.MONDAY + " integer," +
                Columns.Classes.TUESDAY + " integer," +
                Columns.Classes.WEDNESDAY + " integer," +
                Columns.Classes.THURSDAY + " integer," +
                Columns.Classes.FRIDAY + " integer," +
                
                Columns.Classes.MONDAY_START + " integer," +
                Columns.Classes.TUESDAY_START + " integer," +
                Columns.Classes.WEDNESDAY_START + " integer," +
                Columns.Classes.THURSDAY_START + " integer," +
                Columns.Classes.FRIDAY_START + " integer," +

                Columns.Classes.MONDAY_END + " integer," +
                Columns.Classes.TUESDAY_END + " integer," +
                Columns.Classes.WEDNESDAY_END + " integer," +
                Columns.Classes.THURSDAY_END + " integer," +
                Columns.Classes.FRIDAY_END + " integer)";
        
        db.execSQL(createSchedulesTable);
        db.execSQL(createClassesTable);
        
        addDefaultSchedule(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
    
//    public void addClass(Subject subject, String courseTitle, int courseNumber, int creditHours, int crn,
//            boolean online, int scheduleId, boolean[] days, long[] startTimes, long[] endTimes) {
//        final SQLiteDatabase db = getWritableDatabase();
//        
//        final ContentValues classValues = new ContentValues();
//        classValues.put(Columns.Classes.SCHEDULE_ID, scheduleId);
//        classValues.put(Columns.Classes.SUBJECT, subject.ordinal());
//        classValues.put(Columns.Classes.COURSE_TITLE, courseTitle);
//        classValues.put(Columns.Classes.COURSE_NUMBER, courseNumber);
//        classValues.put(Columns.Classes.CREDIT_HOURS, creditHours);
//        classValues.put(Columns.Classes.CRN, crn);
//        classValues.put(Columns.Classes.ONLINE, online? 1 : 0);
//        
//        long classId = db.insert(CLASSES_TABLE_NAME, null, classValues);
//        
//        final ContentValues startTimeValues = new ContentValues();
//        startTimeValues.put(Columns.Days.CLASS_ID, classId);
//        startTimeValues.put(Columns.Days.MONDAY, startTimes[0]);
//        startTimeValues.put(Columns.Days.TUESDAY, startTimes[1]);
//        startTimeValues.put(Columns.Days.WEDNESDAY, startTimes[2]);
//        startTimeValues.put(Columns.Days.THURSDAY, startTimes[3]);
//        startTimeValues.put(Columns.Days.FRIDAY, startTimes[4]);
//        db.insert(START_TIMES_TABLE_NAME, null, startTimeValues);
//        
//        final ContentValues endTimeValues = new ContentValues();
//        endTimeValues.put(Columns.Days.CLASS_ID, classId);
//        endTimeValues.put(Columns.Days.MONDAY, endTimes[0]);
//        endTimeValues.put(Columns.Days.TUESDAY, endTimes[1]);
//        endTimeValues.put(Columns.Days.WEDNESDAY, endTimes[2]);
//        endTimeValues.put(Columns.Days.THURSDAY, endTimes[3]);
//        endTimeValues.put(Columns.Days.FRIDAY, endTimes[4]);
//        db.insert(END_TIMES_TABLE_NAME, null, endTimeValues);
//        
//        db.close();
//    }
//    
//    public void deleteClass(int classId) {
//        final SQLiteDatabase db = getWritableDatabase();
//        final String whereClause[] = {Integer.toString(classId)};
//        
//        db.delete(CLASSES_TABLE_NAME, BaseColumns._ID + "=?", whereClause);
//        db.delete(START_TIMES_TABLE_NAME, Columns.Days.CLASS_ID + "=?", whereClause);
//        db.delete(END_TIMES_TABLE_NAME, Columns.Days.CLASS_ID + "=?", whereClause);
//        
//        db.close();
//    }
//    
//    public Cursor getClasses(int scheduleId) {
//        final SQLiteDatabase db = getReadableDatabase();
//        final String query = "select " +
//                CLASSES_TABLE_NAME + "." + Columns.Classes.SUBJECT + "," +
//                CLASSES_TABLE_NAME + "." + Columns.Classes.COURSE_TITLE + "," +
//                CLASSES_TABLE_NAME + "." + Columns.Classes.COURSE_NUMBER + "," +
//                CLASSES_TABLE_NAME + "." + Columns.Classes.CREDIT_HOURS + "," +
//                CLASSES_TABLE_NAME + "." + Columns.Classes.CRN + "," +
//                CLASSES_TABLE_NAME + "." + Columns.Classes.ONLINE + "," +
//                CLASSES_TABLE_NAME + "." + Columns.Classes.DAYS + "," +
//                START_TIMES_TABLE_NAME + "." + Columns.Days.MONDAY + "," +
//                START_TIMES_TABLE_NAME + "." + Columns.Days.TUESDAY + "," +
//                START_TIMES_TABLE_NAME + "." + Columns.Days.WEDNESDAY + "," +
//                START_TIMES_TABLE_NAME + "." + Columns.Days.THURSDAY + "," +
//                START_TIMES_TABLE_NAME + "." + Columns.Days.FRIDAY + "," +
//                END_TIMES_TABLE_NAME + "." + Columns.Days.MONDAY + "," +
//                END_TIMES_TABLE_NAME + "." + Columns.Days.TUESDAY + "," +
//                END_TIMES_TABLE_NAME + "." + Columns.Days.WEDNESDAY + "," +
//                END_TIMES_TABLE_NAME + "." + Columns.Days.THURSDAY + "," +
//                END_TIMES_TABLE_NAME + "." + Columns.Days.FRIDAY + " " +
//                "from " + CLASSES_TABLE_NAME + " " +
//                "join " + START_TIMES_TABLE_NAME + " on (" + CLASSES_TABLE_NAME + "." + Columns.Classes.ID + "=" + START_TIMES_TABLE_NAME + "." + Columns.Days.CLASS_ID + ") " +
//                "join " + END_TIMES_TABLE_NAME + " on (" + CLASSES_TABLE_NAME + "." + Columns.Classes.ID + "=" + END_TIMES_TABLE_NAME + "." + Columns.Days.CLASS_ID + ") " +
//                "where " + Columns.Classes.SCHEDULE_ID + "=?;";
//        
//        return db.rawQuery(query, new String[]{Integer.toString(scheduleId)});
//    }
//    
    private long addDefaultSchedule(SQLiteDatabase db) {
        final ContentValues values = new ContentValues();
        values.put(Columns.Schedules.SCHEDULE_NAME, DEFAULT_SCHEDULE_NAME);
        return db.insert(SCHEDULE_TABLE_NAME, null, values);
    }
    
    public void checkForSchedule() {
        final SQLiteDatabase db = getReadableDatabase();
        
        if (db.query(SCHEDULE_TABLE_NAME, null, null, null, null, null, null).getColumnCount() == 0) {
            addDefaultSchedule(db);
        }
    }
//    
//    public void deleteSchedule(int scheduleId) {
//        final SQLiteDatabase db = getWritableDatabase();
//        db.delete(SCHEDULE_TABLE_NAME, BaseColumns._ID + "=?", new String[]{Integer.toString(scheduleId)});
//        db.close();
//    }
//    
//    public Cursor getAllSchedules() {
//        final SQLiteDatabase db = getReadableDatabase();
//        return db.query(SCHEDULE_TABLE_NAME, new String[]{BaseColumns._ID, Columns.Schedules.SCHEDULE_NAME}, null, null, null, null, null);
//    }
//    
//    public void renameSchedule(int scheduleId, CharSequence scheduleName) {
//        final SQLiteDatabase db = getWritableDatabase();
//        final ContentValues values = new ContentValues();
//        
//        values.put(Columns.Schedules.SCHEDULE_NAME, scheduleName.toString());
//        db.update(SCHEDULE_TABLE_NAME, values, BaseColumns._ID + "=?", new String[]{Integer.toString(scheduleId)});
//        db.close();
//    }
//

}
