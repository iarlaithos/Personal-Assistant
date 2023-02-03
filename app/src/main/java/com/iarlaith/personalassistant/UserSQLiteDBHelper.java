package com.iarlaith.personalassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "user_database";
    public static final String USER_TABLE = "user";
    public static final String USER_COLUMN_ID = "user_id";
    public static final String USER_COLUMN_NAME = "user_name";
    public static final String USER_COLUMN_EMAIL = "user_email";
    public UserSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE + " (" +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COLUMN_NAME + " TEXT NOT NULL, " +
                USER_COLUMN_EMAIL + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE + ";");
        onCreate(sqLiteDatabase);
    }
}