package com.dcodestar.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import javax.xml.datatype.Duration;

public class AppProvider extends ContentProvider {

    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    public static final UriMatcher sURIMATCHER=buildUriMatcher();

    static final String CONTENT_AUTHORITY="com.dcodestar.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI=Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final int TASKS=100;
    public static final int TASKS_ID=101;

    public static final int TIMINGS=200;
    public static final int TIMINGS_ID=201;

    /*
        private static final int TASK_TIMINGS=300;
        private static final int TASK_TIMINGS_ID=301;
     */

    public static final int TASKS_DURATIONS=400;
    public static final int TASKS_DURATIONS_ID=401;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY,TasksContract.TABLE_NAME,TASKS);
        matcher.addURI(CONTENT_AUTHORITY,TasksContract.TABLE_NAME+"/#",TASKS_ID);

//        matcher.addURI(CONTENT_AUTHORITY,TimingsContract.TABLE_NAME,TIMINGS);
//        matcher.addURI(CONTENT_AUTHORITY,TimingsContract,TABLE_NAME+"/#",TIMINGS_ID);
//
//
//        matcher.addURI(CONTENT_AUTHORITY, DurationContract.TABLE_NAME,TASKS_DURATIONS);
//        matcher.addURI(CONTENT_AUTHORITY,DurationContract.TABLE_NAME+"/#",TASKS_DURATIONS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper= AppDatabase.getInstance(getContext());
;       return true;
    }

    @Override
    public Cursor query( Uri uri,   String[] projection,   String selection,   String[] selectionArgs,   String sortOrder) {
        Log.d(TAG, "query: called with URI "+uri);
        final int match=sURIMATCHER.match(uri);
        Log.d(TAG, "query: match is "+match);

        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();

        switch (match){
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId=TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID+"="+taskId);
                break;

//            case TIMINGS:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                break;

//            case TIMINGS_ID:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                long timingId=TimingsContract.getTimingId(uri);
//                queryBuilder.appendWhere(TimingsContract.Columns._ID+"="+timingId);
//                break;
//
//            case TASKS_DURATIONS:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;

//            case TASKS_DURATIONS_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationId=DurationsContract.getDurationId(uri);
//                queryBuilder.appendWhere(TasksContract.Columns._ID+"="+durationId);
//                break;

            default: throw new IllegalArgumentException("Unknown URI:"+uri);
        }

        SQLiteDatabase db=mOpenHelper.getReadableDatabase();
        Cursor cursor=queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder+"  COLLATE NOCASE");
        Log.d(TAG, "query: row in returned cursor "+cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        return null;
    }

    
    @Override
    public Uri insert( Uri uri,   ContentValues values) {
        Log.d(TAG, "insert: entering insert code with uri="+uri);
        final int match=sURIMATCHER.match(uri);
        Log.d(TAG, "insert: match is "+match);

        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;

        switch (match){
            case TASKS:
                db=mOpenHelper.getWritableDatabase();
                recordId=db.insert(TasksContract.TABLE_NAME,null,values);
                if(recordId>=0)
                    returnUri=TasksContract.buildTaskUri(recordId);
                else
                    throw new android.database.SQLException("Failed to insert into "+uri.toString());
                break;

//            case TIMINGS:
//                db=mOpenHelper.getWritableDatabase();
//                recordId=db.insert(TimingsContract.TABLE_NAME,null,values);
//                if(recordId>=0)
//                    returnUri=TimingsContract.buildTaskUri(recordId);
//                else
//                    throw new android.database.SQLException("Failed to insert into "+uri.toString());
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri:"+uri);
        }

        if(recordId>=0){
            Log.d(TAG, "insert: setting notifychanged with "+uri);
            getContext().getContentResolver().notifyChange(uri,null);
        }else{
            Log.d(TAG, "insert: nothing inserted");
        }
        Log.d(TAG, "insert: Exiting, returning "+returnUri);
        return returnUri;
    }

    @Override
    public int delete( Uri uri,   String selection,   String[] selectionArgs) {
        Log.d(TAG, "delete: called with uri "+uri);
        final int match=sURIMATCHER.match(uri);
        Log.d(TAG, "delete: match is"+match);

        final SQLiteDatabase db;
        int count=0;

        String selectionCriteria;

        switch(match){
            case TASKS:
                db=mOpenHelper.getWritableDatabase();
                count=db.delete(TasksContract.TABLE_NAME,selection,selectionArgs);
                break;

            case TASKS_ID:
                db=mOpenHelper.getWritableDatabase();
                long taskId=TasksContract.getTaskId(uri);
                selectionCriteria=TasksContract.Columns._ID+"="+taskId;
                if(selection!=null&&selection.length()>0){
                    selectionCriteria+=" and ("+selection+")";
                }
                count=db.delete(TasksContract.TABLE_NAME,selectionCriteria,selectionArgs);
                break;

//            case TIMINGS:
//                db=mOpenHelper.getWritableDatabase();
//                count=db.delete(TimingsContract.TABLE_NAME,selection,selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db=mOpenHelper.getWritableDatabase();
//                long timingsId=TimingsContract.getTaskId(uri);
//                selectionCriteria=TimingsContract.Columns._ID+"="+timingsId;
//                if(selection!=null&&selection.length()>0){
//                    selectionCriteria+=" and ("+selection+")";
//                }
//                count=db.delete(TimingsContract.TABLE_NAME,selectionCriteria,selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Unknown ur: "+uri);
        }
        if(count>0){
            Log.d(TAG, "delete: setting notifyChanged with "+uri);
            getContext().getContentResolver().notifyChange(uri,null);
        }else{
            Log.d(TAG, "delete: nothing deleted");
        }
        Log.d(TAG, "delete: exiting, returning"+count);
        return count;
    }

    @Override
    public int update( Uri uri,   ContentValues values,   String selection,   String[] selectionArgs) {
        Log.d(TAG, "update: called with uri "+uri);
        final int match=sURIMATCHER.match(uri);
        Log.d(TAG, "update: match is"+match);

        final SQLiteDatabase db;
        int count=0;

        String selectionCriteria;

        switch(match){
            case TASKS:
                db=mOpenHelper.getWritableDatabase();
                count=db.update(TasksContract.TABLE_NAME,values,selection,selectionArgs);
                break;

            case TASKS_ID:
                db=mOpenHelper.getWritableDatabase();
                long taskId=TasksContract.getTaskId(uri);
                selectionCriteria=TasksContract.Columns._ID+"="+taskId;
                if(selection!=null&&selection.length()>0){
                    selectionCriteria+=" and ("+selection+")";
                }
                count=db.update(TasksContract.TABLE_NAME,values,selectionCriteria,selectionArgs);
                break;

//            case TIMINGS:
//                db=mOpenHelper.getWritableDatabase();
//                count=db.update(TimingsContract.TABLE_NAME,values,selection,selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db=mOpenHelper.getWritableDatabase();
//                long timingsId=TimingsContract.getTaskId(uri);
//                selectionCriteria=TimingsContract.Columns._ID+"="+timingsId;
//                if(selection!=null&&selection.length()>0){
//                    selectionCriteria+=" and ("+selection+")";
//                }
//                count=db.update(TimingsContract.TABLE_NAME,values,selectionCriteria,selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Unknown ur: "+uri);
        }
        if(count>0){
            Log.d(TAG, "update: setting notifyChanged with "+uri);
            getContext().getContentResolver().notifyChange(uri,null);
        }else{
            Log.d(TAG, "update: nothing deleted");
        }
        Log.d(TAG, "update: exiting, returning"+count);
        return count;

    }
}
