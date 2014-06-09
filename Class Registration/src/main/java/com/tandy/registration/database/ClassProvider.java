package com.tandy.registration.database;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ClassProvider extends ContentProvider {
    
    private static final String AUTHORITY = "com.tandy.registration.froyo";
    
    private static final int CLASSES = 10;
    private static final int CLASS_IDS = 20;
    private static final int SCHEDULES = 30;
    private static final int SCHEDULE_IDS = 40;
    
    private static final String CLASSES_BASE_PATH = "Classes";
    private static final String SCHEDULES_BASE_PATH = "Schedules";
    
    public static final Uri CLASS_URI = Uri.parse("content://" + AUTHORITY + "/" + CLASSES_BASE_PATH);
    public static final Uri SCHEDULE_URI = Uri.parse("content://" + AUTHORITY + "/" + SCHEDULES_BASE_PATH);
    
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(AUTHORITY, CLASSES_BASE_PATH, CLASSES);
        URI_MATCHER.addURI(AUTHORITY, CLASSES_BASE_PATH + "/#", CLASS_IDS);
        URI_MATCHER.addURI(AUTHORITY, SCHEDULES_BASE_PATH, SCHEDULES);
        URI_MATCHER.addURI(AUTHORITY, SCHEDULES_BASE_PATH + "/#", SCHEDULE_IDS);
    }
    
    private ClassDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = new ClassDatabase(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        int rowsDeleted = 0;
        
        switch (URI_MATCHER.match(uri)) {
            case CLASSES:
                rowsDeleted = db.delete(ClassDatabase.CLASSES_TABLE_NAME, selection, selectionArgs);
                break;
                
            case CLASS_IDS:
                final String classId[] = { uri.getLastPathSegment() };
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ClassDatabase.CLASSES_TABLE_NAME,
                            ClassDatabase.Columns.Classes.ID + "=?",
                            classId);
                } else {
                    // Make new array to hold id in the selection arguments
                    // TODO: Update to Array.copy when API Level >= 9
                    String modifiedSelectionArgs[] = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, modifiedSelectionArgs, 0, selectionArgs.length);
                    modifiedSelectionArgs[selectionArgs.length] = classId[0];
                    rowsDeleted = db.delete(
                            ClassDatabase.CLASSES_TABLE_NAME,
                            selection + " and " + ClassDatabase.Columns.Classes.ID + "=?",
                            modifiedSelectionArgs);
                }
                getContext().getContentResolver().notifyChange(CLASS_URI, null);
                break;
                
            case SCHEDULES:
                rowsDeleted = db.delete(ClassDatabase.SCHEDULE_TABLE_NAME, selection, selectionArgs);
                mDatabase.checkForSchedule();
                break;
                
            case SCHEDULE_IDS:
                final String scheduleId[] = { uri.getLastPathSegment() };
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(
                            ClassDatabase.SCHEDULE_TABLE_NAME,
                            ClassDatabase.Columns.Schedules.ID + "=?",
                            scheduleId);
                } else {
                    // Make new array to hold id in the selection arguments
                    // TODO: Update to Array.copy when API Level >= 9
                    String modifiedSelectionArgs[] = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, modifiedSelectionArgs, 0, selectionArgs.length);
                    modifiedSelectionArgs[selectionArgs.length] = scheduleId[0];
                    rowsDeleted = db.delete(
                            ClassDatabase.CLASSES_TABLE_NAME,
                            selection + " and " + ClassDatabase.Columns.Schedules.ID + "=?",
                            modifiedSelectionArgs);
                }

                mDatabase.checkForSchedule();
                getContext().getContentResolver().notifyChange(SCHEDULE_URI, null);
                break;
                
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        long id = 0;
        String baseUriPath;
        
        switch (URI_MATCHER.match(uri)) {
            case CLASSES:
                baseUriPath = CLASSES_BASE_PATH;
                id = db.insert(ClassDatabase.CLASSES_TABLE_NAME, null, values);
                break;
                
            case SCHEDULES:
                baseUriPath = SCHEDULES_BASE_PATH;
                id = db.insert(ClassDatabase.SCHEDULE_TABLE_NAME, null, values);
                break;
                
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(baseUriPath + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        
        switch (URI_MATCHER.match(uri)) {
            case CLASSES:
                queryBuilder.setTables(ClassDatabase.CLASSES_TABLE_NAME);
                break;
                
            case CLASS_IDS:
                queryBuilder.setTables(ClassDatabase.CLASSES_TABLE_NAME);
                queryBuilder.appendWhere(ClassDatabase.Columns.Classes.ID + "=" + uri.getLastPathSegment());
                break;
                
            case SCHEDULES:
                queryBuilder.setTables(ClassDatabase.SCHEDULE_TABLE_NAME);
                Cursor cursor = queryBuilder.query(mDatabase.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                if (cursor.getColumnCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put(ClassDatabase.Columns.Schedules.SCHEDULE_NAME, "Untitled Schedule");
                    insert(uri, values);
                }
                cursor.close();
                break;
                
            case SCHEDULE_IDS:
                queryBuilder.setTables(ClassDatabase.SCHEDULE_TABLE_NAME);
                queryBuilder.appendWhere(ClassDatabase.Columns.Schedules.ID + "=" + uri.getLastPathSegment());
                break;
                
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(mDatabase.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        int rowsUpdated = 0;
        
        switch (URI_MATCHER.match(uri)) {
            case CLASSES:
                rowsUpdated = db.update(
                        ClassDatabase.CLASSES_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
                
            case CLASS_IDS:
                final String classId[] = { uri.getLastPathSegment() };
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(
                            ClassDatabase.CLASSES_TABLE_NAME,
                            values,
                            ClassDatabase.Columns.Classes.ID + "=?",
                            classId);
                } else {
                    String modifiedSelectionArgs[] = new String[selectionArgs.length];
                    System.arraycopy(selectionArgs, 0, modifiedSelectionArgs, 0, selectionArgs.length);
                    modifiedSelectionArgs[selectionArgs.length] = classId[0];
                    rowsUpdated = db.update(
                            ClassDatabase.CLASSES_TABLE_NAME,
                            values,
                            selection + " and " + ClassDatabase.Columns.Days.ID + "=?",
                            modifiedSelectionArgs);
                }
                
                getContext().getContentResolver().notifyChange(CLASS_URI, null);
                break;
                
            case SCHEDULES:
                rowsUpdated = db.update(
                        ClassDatabase.SCHEDULE_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
                
            case SCHEDULE_IDS:
                final String scheduleId[] = { uri.getLastPathSegment() };
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(
                            ClassDatabase.SCHEDULE_TABLE_NAME,
                            values,
                            ClassDatabase.Columns.Schedules.ID + "=?",
                            scheduleId);
                } else {
                    String modifiedSelectionArgs[] = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, modifiedSelectionArgs, 0, selectionArgs.length);
                    modifiedSelectionArgs[selectionArgs.length] = scheduleId[0];
                    rowsUpdated = db.update(
                            ClassDatabase.SCHEDULE_TABLE_NAME,
                            values,
                            selection + " and " + ClassDatabase.Columns.Schedules.ID + "=?",
                            modifiedSelectionArgs);
                }
                getContext().getContentResolver().notifyChange(SCHEDULE_URI, null);
                break;
                
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
