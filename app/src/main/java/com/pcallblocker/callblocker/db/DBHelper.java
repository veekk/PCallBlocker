package com.pcallblocker.callblocker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Crafted by veek on 18.06.16 with love ♥
 */
public class DBHelper extends SQLiteOpenHelper {

    // Define the SQLite database name
    private static final String DATABASE_NAME = "call_blocker.db";

    // Define the SQLite database version
    private static final int DATABASE_VERSION = 1;

    // Define the SQLite Table name to create
    public static final String TABLE_BLACKLIST = "blacklist";

    // Table creation SQL statement
    private static final String TABLE_CREATE = "create table "  + TABLE_BLACKLIST + "( id "
            + " integer primary key autoincrement, phone_number  text not null, " + "phone_name text);";

    // Define the SQLite Table name to create
    public static final String TABLE_REJECTED_CALLS = "rejected_calls";

    // Table creation SQL statement
    private static final String TABLE_CREATE_REJECTED = "create table "  + TABLE_REJECTED_CALLS + "( id "
            + " integer primary key autoincrement, phone_number  text not null, " + "phone_name text, " + "amount_calls integer," + "time integer," +"reject_type text);";

    // Define the SQLite Table name to create
    public static final String TABLE_UNKNOWN = "unknown";

    // Table creation SQL statement
    private static final String TABLE_CREATE_UNKNOWN = "create table "  + TABLE_UNKNOWN + "( id "
            + " integer primary key autoincrement, phone_my  text not null, " + "phone_unknown text not null);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This method will execute once in the application entire life cycle
    // All table creation code should put here
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE_REJECTED);
        db.execSQL(TABLE_CREATE_UNKNOWN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

}
