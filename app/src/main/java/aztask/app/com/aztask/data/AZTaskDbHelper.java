package aztask.app.com.aztask.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static aztask.app.com.aztask.data.AZTaskContract.*;

/**
 * Created by javed.ahmed on 4/14/2017.
 */

public class AZTaskDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "aztask.db";

    private static final String SQL_CREATE_NEAR_BY_TASKS_TABLE =
            "CREATE TABLE " + NearByTaskEntry.TABLE_NAME + " (" +
                    NearByTaskEntry._ID + " INTEGER PRIMARY KEY autoincrement," +
                    NearByTaskEntry.COLUMN_NAME_TASK_ID + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_DESC + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_TIME + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_CATEGORY + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_LOCATION + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_MIN_MAX_BUDGET + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_OWNER_NAME + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_OWNER_CONTACT + " TEXT," +
                    NearByTaskEntry.COLUMN_NAME_TASK_LIKED + " TEXT" +")";

    private static final String SQL_DELETE_NEAR_BY_TASKS_TABLE =
            "DROP TABLE IF EXISTS " + NearByTaskEntry.TABLE_NAME;


    private static final String SQL_CREATE_ASSIGNED_TASKS_TABLE =
            "CREATE TABLE " + AssignedTaskEntry.TABLE_NAME + " (" +
                    NearByTaskEntry._ID + " INTEGER PRIMARY KEY autoincrement," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_ID + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_DESC + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_TIME + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_CATEGORY + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_LOCATION + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_MIN_MAX_BUDGET + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_OWNER_NAME + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_OWNER_CONTACT + " TEXT," +
                    AssignedTaskEntry.COLUMN_NAME_TASK_LIKED + " TEXT" +")";

    private static final String SQL_DELETE_ASSIGNED_TASKS_TABLE=
            "DROP TABLE IF EXISTS " + AssignedTaskEntry.TABLE_NAME;

    private static final String SQL_CREATE_MY_TASKS_TABLE =
            "CREATE TABLE " + MyTaskEntry.TABLE_NAME + " (" +
                    MyTaskEntry._ID + " INTEGER PRIMARY KEY autoincrement," +
                    MyTaskEntry.COLUMN_NAME_TASK_ID + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_DESC + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_TIME + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_CATEGORY + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_LOCATION + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_MIN_MAX_BUDGET + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_OWNER_NAME + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_OWNER_CONTACT + " TEXT," +
                    MyTaskEntry.COLUMN_NAME_TASK_LIKED + " TEXT" +")";

    private static final String SQL_DELETE_MY_TASKS_TABLE =
            "DROP TABLE IF EXISTS " + MyTaskEntry.TABLE_NAME;



    public AZTaskDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NEAR_BY_TASKS_TABLE);
        db.execSQL(SQL_CREATE_ASSIGNED_TASKS_TABLE);
        db.execSQL(SQL_CREATE_MY_TASKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_NEAR_BY_TASKS_TABLE);
        db.execSQL(SQL_DELETE_ASSIGNED_TASKS_TABLE);
        db.execSQL(SQL_DELETE_MY_TASKS_TABLE);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
