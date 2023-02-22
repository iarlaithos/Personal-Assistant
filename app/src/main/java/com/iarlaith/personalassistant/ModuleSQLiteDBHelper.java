package com.iarlaith.personalassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ModuleSQLiteDBHelper extends SQLiteOpenHelper {
    //DATABASE
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "modules_database";
    //MODULES
    public static final String MODULES_TABLE = "module";
    public static final String MODULE_COLUMN_ID = "module_id";
    public static final String MODULE_COLUMN_NAME = "module_name";
    public static final String MODULE_COLUMN_COLOUR = "colour";
    //MODULE SESSIONS
    public static final String MODULE_SESSIONS_TABLE = "module_sessions";
    public static final String SESSION_COLUMN_ID = "session_id";
    public static final String SESSION_COLUMN_LOCATION = "location";
    public static final String SESSION_COLUMN_TYPE = "type";
    public static final String SESSION_COLUMN_DAY = "day";
    public static final String SESSION_COLUMN_START_TIME = "start_time";
    public static final String SESSION_COLUMN_END_TIME = "end_time";
    //TASKS
    public static final String TASKS_TABLE = "tasks";
    public static final String TASK_COLUMN_ID = "task_id";
    public static final String TASKS_COLUMN_TITLE = "task_title";
    public static final String TASKS_COLUMN_TYPE = "type";
    public static final String TASKS_COLUMN_DATE = "due_date";
    public static final String TASKS_COLUMN_NOTE = "note";
    public static final String TASKS_COLUMN_ISCHECKED = "is_checked";


    public ModuleSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MODULES_TABLE + " (" +
                MODULE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MODULE_COLUMN_NAME + " TEXT NOT NULL, " +
                MODULE_COLUMN_COLOUR + " TEXT NOT NULL);");

        sqLiteDatabase.execSQL("CREATE TABLE " + MODULE_SESSIONS_TABLE + " (" +
                SESSION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SESSION_COLUMN_LOCATION + " TEXT NOT NULL, " +
                SESSION_COLUMN_TYPE + " TEXT NOT NULL, " +
                SESSION_COLUMN_DAY + " TEXT NOT NULL, " +
                SESSION_COLUMN_START_TIME + " TEXT NOT NULL, " +
                SESSION_COLUMN_END_TIME + " TEXT NOT NULL, " +
                MODULE_COLUMN_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + MODULE_COLUMN_ID + ") REFERENCES " + MODULES_TABLE + "(" + MODULE_COLUMN_ID + "));");

        sqLiteDatabase.execSQL("CREATE TABLE " + TASKS_TABLE + " (" +
                TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASKS_COLUMN_TITLE + " TEXT NOT NULL, " +
                TASKS_COLUMN_TYPE + " TEXT NOT NULL, " +
                TASKS_COLUMN_DATE + " TEXT NOT NULL, " +
                TASKS_COLUMN_NOTE + " TEXT NOT NULL, " +
                TASKS_COLUMN_ISCHECKED + " TEXT NOT NULL, " +
                MODULE_COLUMN_ID + " INTEGER NOT NULL);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MODULES_TABLE + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MODULE_SESSIONS_TABLE + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE + ";");
        onCreate(sqLiteDatabase);
    }

    public void deleteAll(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("delete from "+ MODULES_TABLE);
        sqLiteDatabase.execSQL("delete from "+ MODULE_SESSIONS_TABLE);
        sqLiteDatabase.execSQL("delete from "+ TASKS_TABLE);
    }
}