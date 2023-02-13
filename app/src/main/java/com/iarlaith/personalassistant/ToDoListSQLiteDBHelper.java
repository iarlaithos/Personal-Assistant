package com.iarlaith.personalassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoListSQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "todo_database";
    public static final String TODO_TABLE = "todo";
    public static final String TODO_COLUMN_ID = "todo_id";
    public static final String TODO_COLUMN_NAME = "todo_name";
    public static final String TODO_COLUMN_ISCHECKED = "todo_checked";


    public ToDoListSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TODO_TABLE + " (" +
                TODO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TODO_COLUMN_NAME + " TEXT NOT NULL, " +
                TODO_COLUMN_ISCHECKED + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE + ";");
        onCreate(sqLiteDatabase);
    }

    public void deleteAll(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("delete from " + TODO_TABLE);
    }
}