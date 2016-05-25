package gmu.cs.cs477.alarmproj;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;


public class MyDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "alarms";
    public static final String COL_TYPE = "Type";
    public static final String COL_HOUR = "Hour";
    public static final String COL_MINUTE = "Minute";
    public static final String COL_REPEAT = "Repeat";
    public static final String COL_INTERVAL = "Interval";
    public static final String COL_NAME = "Name";
    public static final String COL_INTENT = "Intent";
    public List alarmList;
    private static final String STRING_CREATE =
            "CREATE TABLE " +TABLE_NAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT, " + COL_TYPE + " TEXT, " + COL_INTENT + " TEXT, " + COL_HOUR + " TEXT, "+ COL_MINUTE + " TEXT, "+ COL_REPEAT + " TEXT, " + COL_INTERVAL + " TEXT);";
    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STRING_CREATE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }


}