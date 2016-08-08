package com.pcallblocker.callblocker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.pcallblocker.callblocker.model.UnknownNumber;

/**
 * Crafted by veek on 08.08.16 with love ♥
 */
public class UnknownDAO {

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    // Constructor initiates the DatabaseHelper to make sure, database creation is done
    public UnknownDAO(Context context) {
        dbHelper = new DBHelper(context);
        open();
    }

    private void open() throws SQLException {

        // Opens the database connection to provide the access
        database = dbHelper.getWritableDatabase();
    }

    public void close() {

        // Close it, once done
        dbHelper.close();
    }

    public UnknownNumber create(final UnknownNumber unknownNumbers) {

        // Steps to insert data into db (instead of using raw SQL query)
        // first, Create an object of ContentValues
        final ContentValues values = new ContentValues();

        // second, put the key-value pair into it
        values.put("phone_my", unknownNumbers.myNumber);
        values.put("phone_unknown", unknownNumbers.unNumber);

        // thirst. insert the object into the database
        final long id = database.insert(DBHelper.TABLE_UNKNOWN , null, values);

        // set the primary key to object and return back
        unknownNumbers.id = id;
        return unknownNumbers;
    }


    public void clear() {
        // Way to delete a record from database
        database.delete(DBHelper.TABLE_UNKNOWN, null , null);
    }

}
