package aztask.app.com.aztask.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class AZTaskContentProvider extends ContentProvider {

    private AZTaskDbHelper azTaskDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI("com.aztask.app", AZTaskContract.PATH_NEARYBY_TASKS, 100);
        sUriMatcher.addURI("com.aztask.app", AZTaskContract.PATH_NEARYBY_TASKS+"/#", 101);

        sUriMatcher.addURI("com.aztask.app", AZTaskContract.PATH_ASSIGNED_TASKS, 200);
        sUriMatcher.addURI("com.aztask.app", AZTaskContract.PATH_ASSIGNED_TASKS+"/#", 201);

        sUriMatcher.addURI("com.aztask.app", AZTaskContract.PATH_MY_TASKS, 300);
        sUriMatcher.addURI("com.aztask.app", AZTaskContract.PATH_MY_TASKS+"/#", 301);
    }


    @Override
    public boolean onCreate() {
        azTaskDbHelper=new AZTaskDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = azTaskDbHelper.getReadableDatabase();
        Cursor cursor=null;

        switch (sUriMatcher.match(uri)){

            case 100:{
                cursor= db.query(AZTaskContract.NearByTaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_TIME+" DESC");
            }break;

            case 101:{

            }break;

            case 200:{
                cursor= db.query(AZTaskContract.AssignedTaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, AZTaskContract.AssignedTaskEntry.COLUMN_NAME_TASK_TIME+" DESC");
            }break;

            case 201:{

            }break;

            case 300:{
                cursor= db.query(AZTaskContract.MyTaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_TIME+" DESC");
            }break;

            case 301:{

            }break;

            default:
                throw new IllegalArgumentException("Unknown uri."+uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = azTaskDbHelper.getWritableDatabase();
        Uri insertedTaskURI=null;
        switch (sUriMatcher.match(uri)){
            case 100:{
                long recordId = db.insert(AZTaskContract.NearByTaskEntry.TABLE_NAME, null, values);
                insertedTaskURI=ContentUris.withAppendedId(AZTaskContract.NEARBY_TASKS_CONTENT_URI,recordId);
            }break;

            case 200:{
                long recordId = db.insert(AZTaskContract.AssignedTaskEntry.TABLE_NAME, null, values);
                insertedTaskURI=ContentUris.withAppendedId(AZTaskContract.ASSIGNED_TASKS_CONTENT_URI,recordId);
            }break;

            case 300:{
                long recordId = db.insert(AZTaskContract.MyTaskEntry.TABLE_NAME, null, values);
                insertedTaskURI=ContentUris.withAppendedId(AZTaskContract.MY_TASKS_CONTENT_URI,recordId);
            }break;

            default:
                throw new IllegalArgumentException("Unknown uri."+uri);

        }

        getContext().getContentResolver().notifyChange(uri,null);
        return insertedTaskURI;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long deleteRecordId=-1;
        SQLiteDatabase db = azTaskDbHelper.getWritableDatabase();
        String whereClause="task_id=?";
        String[] taskId={uri.getLastPathSegment()};

        switch (sUriMatcher.match(uri)){
            case 100:{
                deleteRecordId= db.delete(AZTaskContract.NearByTaskEntry.TABLE_NAME,null,null);
            }break;

            case 200:{
                deleteRecordId= db.delete(AZTaskContract.AssignedTaskEntry.TABLE_NAME,null,null);
            }break;

            case 201:{
                deleteRecordId=db.delete(AZTaskContract.AssignedTaskEntry.TABLE_NAME,whereClause,taskId);
            }break;

            case 300:{
                deleteRecordId = db.delete(AZTaskContract.MyTaskEntry.TABLE_NAME,null,null);
            }break;

            case 301:{
                deleteRecordId=db.delete(AZTaskContract.MyTaskEntry.TABLE_NAME,whereClause,taskId);
            }break;

            default:
                throw new IllegalArgumentException("Unknown uri."+uri);

        }

        getContext().getContentResolver().notifyChange(uri,null);
        return (int)deleteRecordId;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long updatedRecordId=-1;
        SQLiteDatabase db = azTaskDbHelper.getWritableDatabase();
        String whereClause="task_id=?";

        switch (sUriMatcher.match(uri)){
            case 100:{
            }break;

            case 101:{
                String[] taskId={uri.getLastPathSegment()};
                db.update(AZTaskContract.NearByTaskEntry.TABLE_NAME,values,whereClause,taskId);
            }break;

            case 200:{
            }break;

            case 201:{
                String[] taskId={uri.getLastPathSegment()};
                db.update(AZTaskContract.AssignedTaskEntry.TABLE_NAME,values, whereClause,taskId);
            }break;

            case 300:{
            }break;

            case 301:{
                String[] taskId={uri.getLastPathSegment()};
                db.update(AZTaskContract.MyTaskEntry.TABLE_NAME,values, whereClause,taskId);
            }break;

            default:
                throw new IllegalArgumentException("Unknown uri."+uri);

        }

        getContext().getContentResolver().notifyChange(uri,null);
        return (int)updatedRecordId;
    }


}
