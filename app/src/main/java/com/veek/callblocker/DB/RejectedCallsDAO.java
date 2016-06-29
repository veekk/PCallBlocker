package com.veek.callblocker.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.veek.callblocker.Model.RejectedCall;

import java.util.ArrayList;
import java.util.List;

/**
 * Crafted by veek on 29.06.16 with love ♥
 */
public class RejectedCallsDAO {


    private SQLiteDatabase database;
    private DBHelper dbHelper;

    // Constructor initiates the DatabaseHelper to make sure, database creation is done
    public RejectedCallsDAO(Context context) {
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

    public RejectedCall create(final RejectedCall rejectedCall) {

        // Steps to insert data into db (instead of using raw SQL query)
        // first, Create an object of ContentValues
        final ContentValues values = new ContentValues();

        // second, put the key-value pair into it
        values.put("phone_number", rejectedCall.phoneNumber);
        values.put("phone_name", rejectedCall.phoneName);
        values.put("amount_calls", rejectedCall.amountCalls);
        values.put("time", rejectedCall.time);

        // thirst. insert the object into the database
        final long id = database.insert(DBHelper.TABLE_REJECTED_CALLS , null, values);

        // set the primary key to object and return back
        rejectedCall.id = id;
        return rejectedCall;
    }

    public void update (final RejectedCall rejectedCall){
        final ContentValues values = new ContentValues();
        values.put("phone_number", rejectedCall.phoneNumber);
        values.put("phone_name", rejectedCall.phoneName);
        values.put("amount_calls", rejectedCall.amountCalls);
        values.put("time", rejectedCall.time);
        database.update(DBHelper.TABLE_REJECTED_CALLS, values, "id = ?", new String[] {String.valueOf(rejectedCall.id)});

    }

    public void delete(final RejectedCall rejectedCall) {

        // Way to delete a record from database
        database.delete(DBHelper.TABLE_REJECTED_CALLS, "phone_number = '" + rejectedCall.phoneNumber + "'", null);
    }

    public List<RejectedCall> getAllRejectedCalls() {

        // Steps to fetch all records from a database table
        // first, create the desired object
        final List<RejectedCall> rejectedCalls = new ArrayList<RejectedCall>();

        // second, Query the database and set the result into Cursor
        final Cursor cursor = database.query(DBHelper.TABLE_REJECTED_CALLS, new String[]{"id","phone_number", "phone_name", "amount_calls", "time"}, null, null, null, null, null);

        // Move the Cursor pointer to the first
        cursor.moveToFirst();

        //Iterate over the cursor
        while (!cursor.isAfterLast()) {
            final RejectedCall rejectedCall = new RejectedCall();

            // Fetch the desired value from the Cursor by column index
            rejectedCall.id = cursor.getLong(0);
            rejectedCall.phoneNumber = cursor.getString(1);
            rejectedCall.phoneName = cursor.getString(2);
            rejectedCall.amountCalls = cursor.getLong(3);
            rejectedCall.time = cursor.getString(4);

            // Add the object filled with appropriate data into the list
            rejectedCalls.add(rejectedCall);

            // Move the Cursor pointer to next for the next record to fetch
            cursor.moveToNext();
        }
        return rejectedCalls;
    }
}
