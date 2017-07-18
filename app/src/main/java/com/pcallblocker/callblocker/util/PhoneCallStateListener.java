package com.pcallblocker.callblocker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.db.BlacklistDAO;
import com.pcallblocker.callblocker.db.DBHelper;
import com.pcallblocker.callblocker.db.RejectedCallsDAO;
import com.pcallblocker.callblocker.db.UnknownDAO;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.model.RejectedCall;
import com.pcallblocker.callblocker.model.UnknownNumber;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

/**
 * Crafted by veek on 10.08.16 with love ♥
 */

public class PhoneCallStateListener extends PhoneStateListener {

    private Context context;

    AudioManager am;
    TelephonyManager tm;
    CustomPreferenceManager
            pm = CustomPreferenceManager.getInstance();

    RejectedCallsDAO rejectedCallsDAO;
    UnknownDAO unknownDAO;
    BlacklistDAO blacklistDAO;

    List<RejectedCall> rejectedCalls;
    List<Blacklist> blockList;

    private String number;

    Date date;

    String countryID = "";
    String zipCode = "";

    Calendar calendar = Calendar.getInstance();

   // SharedPreferences prefs;

    SQLiteDatabase database;
    DBHelper dbHelper;

    boolean enabled, hidden, international, notContacts, all;

    public PhoneCallStateListener(Context context){
        this.context = context;
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        pm.init(context, "settings");
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        rejectedCallsDAO =  new RejectedCallsDAO(context);
        unknownDAO = new UnknownDAO(context);
        blacklistDAO = new BlacklistDAO(context);
     //   prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }


    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, number);
//        pm = CustomPreferenceManager.getInstance();
        pm.init(context);
        enabled = pm.getState("block_enabled");
        all = pm.getState("all_numbers");
        international = pm.getState("international");
        notContacts = pm.getState("not_contacts");
        hidden = pm.getState("hidden");
     //   SharedPreferences preference = context.getSharedPreferences("settings", Context.MODE_PRIVATE);


        am.setStreamMute(AudioManager.STREAM_RING, false);

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                number = phoneNumber;
                if (number != null) {
                    if (!contactExists(context, number)) sendUnknown(context, number);
                } else sendUnknown(context, "unknown");
                blockList = blacklistDAO.getAllBlacklist();


                String currentDay = "";

                switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.MONDAY:
                        currentDay = "Monday";
                        break;
                    case Calendar.TUESDAY:
                        currentDay = "Tuesday";
                        break;
                    case Calendar.WEDNESDAY:
                        currentDay = "Wednesday";
                        break;
                    case Calendar.THURSDAY:
                        currentDay = "Thursday";
                        break;
                    case Calendar.FRIDAY:
                        currentDay = "Friday";
                        break;
                    case Calendar.SATURDAY:
                        currentDay = "Saturday";
                        break;
                    case Calendar.SUNDAY:
                        currentDay = "Sunday";
                        break;
                }
                boolean isDayFilter = pm.getState("calendar");
                Set<String> days = pm.getStringSet("day_of_week");


                if (pm.getState("block_enabled")) {

                    if (!isDayFilter || days.contains(currentDay)) {

                        if (number != null) {


                            if (pm.getState("all_numbers")) {
                                if (number != null) {
                                    if (number.equals("")){
                                        if (rejectedCallsDAO != null)
                                            rejectedCallsDAO.create(new RejectedCall("1", null, "hidden"));
                                        else {
                                            rejectedCallsDAO = new RejectedCallsDAO(context);
                                            rejectedCallsDAO.create(new RejectedCall("1", null, "hidden"));
                                        }
                                    } else
                                        blockCall(context, "all_numbers");
                                } else {
                                    if (rejectedCallsDAO != null)
                                        rejectedCallsDAO.create(new RejectedCall("1", null, "hidden"));
                                    else {
                                        rejectedCallsDAO = new RejectedCallsDAO(context);
                                        rejectedCallsDAO.create(new RejectedCall("1", null, "hidden"));
                                    }
                                }
                                break;
                            }


                            if (pm.getState("international")) {
                                countryID = tm.getSimCountryIso().toUpperCase();
                                String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
                                for (int j = 0; j < rl.length; j++) {
                                    String[] g = rl[j].split(",");
                                    if (g[1].trim().equals(countryID.trim())) {
                                        zipCode = g[0];
                                        break;
                                    }
                                }
                                if (!number.substring(0, zipCode.length() + 1).contains(zipCode)) {
                                    blockCall(context, "international");
                                    break;
                                }
                            }


                            if (blockList.contains(new Blacklist(number, ""))) {
                                for (Blacklist blacklist : blockList) {
                                    if (blacklist.equals(new Blacklist(number, ""))) {
                                        blockCall(context, "blacklist");
                                        break;
                                    }
                                }

                                break;
                            }

                            //if (pm.getState("not_contacts")) {
                            if (pm.getState("not_contacts")) {
                                if (!contactExists(context, number)) {
                                    blockCall(context, "not_contacts");
                                    break;
                                }
                            }
                        } else {
                            if (pm.getState("hidden")) {
                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                if (!pm.getState("mute_only"))
                                    disconnectPhoneItelephony(context);
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                if (rejectedCallsDAO != null)
                                    rejectedCallsDAO.create(new RejectedCall("1", null, "hidden"));
                                else {
                                    rejectedCallsDAO = new RejectedCallsDAO(context);
                                    rejectedCallsDAO.create(new RejectedCall("1", null, "hidden"));
                                }
                            }
                        }


                        for (Blacklist blacklist : blockList) {
                            if (blacklist.phoneNumber.contains("x")) {
                                String pNum = blacklist.phoneNumber.replace("x", "");
                                if (number != null) {
                                    if (number.contains("+")) {
                                        if (number.substring(0, (pNum.length() + 3)).contains(pNum)) {
                                            blockCall(context, "blacklist");
                                            break;
                                        }
                                    } else if (number.substring(0, (pNum.length() + 1)).contains(pNum)) {
                                        blockCall(context, "blacklist");
                                        break;
                                    }
                                }
                            }
                        }


                    }
                }
                    break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        am.setStreamMute(AudioManager.STREAM_RING, false);
                        break;

        }



    }

    private void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(tm);
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JSONArray getResults(Context context)
    {

        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        String myPath = database.getPath() + "call_blocker.db";

        String myTable = "unknown";//Set name of your table

//or you can use `context.getDatabasePath("my_db_test.db")`

        SQLiteDatabase myDataBase = dbHelper.getReadableDatabase();

        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        JSONArray resultSet     = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number
        if (!number.equals("")) {
            Uri lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return false;
    }

    private void blockCall(Context context, String type) {
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_RING, true);
        if (!pm.getState("mute_only")) disconnectPhoneItelephony(context);


        int inc = 0;


        rejectedCalls = rejectedCallsDAO.getAllRejectedCalls();

        if (rejectedCalls.size() > 0) {
            for (RejectedCall rejectedCall : rejectedCalls) {
                if (rejectedCall.phoneNumber.equals(number)) {
                    rejectedCall.incAmount();
                    date = new Date();
                    rejectedCall.updTime(date.getTime());
                    rejectedCall.type = type;
                    rejectedCallsDAO.update(rejectedCall);
                    rejectedCalls = rejectedCallsDAO.getAllRejectedCalls();
                    inc = 0;
                    break;
                } else inc++;
            }
        }
        if (inc > 0 || rejectedCalls.size() == 0) {
            String name = "";
            ContactsProvider contactsProvider = new ContactsProvider(context);
            List<Contact> contacts = contactsProvider.getContacts().getList();
            for (Contact contact : contacts) {
                if (PhoneNumberUtils.compare(contact.normilizedPhone, number)) {
                    name = contact.displayName;
                }
            }

            rejectedCallsDAO.create(new RejectedCall(number, name, type));
            MainActivity.rejectedCalls = rejectedCallsDAO.getAllRejectedCalls();

            if (!pm.getState("mute_only")) am.setStreamMute(AudioManager.STREAM_RING, false);
        }
    }

    private void sendUnknown(Context context, String number){
            String myNumber = tm.getLine1Number().replace("+", "");
            if (TextUtils.isEmpty(myNumber)) myNumber = "unknown";
            unknownDAO.create(new UnknownNumber(myNumber, number.replace("+", "")));
            JSONArray jsonObject = getResults(context);
            String message = jsonObject.toString();
            CustomRestClient restClient = new CustomRestClient();
            RequestParams requestParams = new RequestParams("json", message);
            restClient.post("unknown.php", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    unknownDAO.clear();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
    }

}
