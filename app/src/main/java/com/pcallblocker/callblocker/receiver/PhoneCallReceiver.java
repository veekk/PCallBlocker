package com.pcallblocker.callblocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
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
import com.pcallblocker.callblocker.util.CustomPreferenceManager;
import com.pcallblocker.callblocker.util.CustomRestClient;
import com.pcallblocker.callblocker.util.PhoneCallStateListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

/**
 * Crafted by veek on 10.08.16 with love ♥
 */



public class PhoneCallReceiver extends BroadcastReceiver {



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


    SQLiteDatabase database;
    DBHelper dbHelper;


    static PhoneCallStateListener customPhoneListener;
    static TelephonyManager tm;
    static AudioManager am;

    @Override
    public void onReceive(Context context, Intent intent) {
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (customPhoneListener == null) {
            customPhoneListener = new PhoneCallStateListener(context);
            tm.listen(customPhoneListener,  android.telephony.PhoneStateListener.LISTEN_CALL_STATE);
            pm.init(context, "settings");
            rejectedCallsDAO =  new RejectedCallsDAO(context);
            unknownDAO = new UnknownDAO(context);
            blacklistDAO = new BlacklistDAO(context);
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
                switch (tm.getCallState()){
                    case TelephonyManager.CALL_STATE_RINGING:
                        number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);;
                        if (number != null && !contactExists(context, number)) sendUnknown(context, number);
                        if (number == null) sendUnknown(context, "unknown");
                        blockList = blacklistDAO.getAllBlacklist();




                        //if (pm.getState("block_enabled")) {
                        if (pm.getState("block_enabled")){
                            //  am.setStreamMute(AudioManager.STREAM_RING, true);
                            if (number != null) {



                                if (pm.getState("all_numbers")) {
                                    blockCall(context, "all_numbers");
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
                                if (pm.getState("not_contacts")){
                                    if (!contactExists(context, number)) {
                                        blockCall(context, "not_contacts");
                                        break;
                                    }
                                }
                            } else {
                                if (pm.getState("hidden")) {
                                    if (rejectedCallsDAO == null)
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                    disconnectPhoneItelephony(context);
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    rejectedCallsDAO.create(new RejectedCall("1", null, "hidden"));
                                }
                            }


                            for (Blacklist blacklist : blockList) {
                                if (blacklist.phoneNumber.contains("x")) {
                                    String pNum = blacklist.phoneNumber.replace("x", "");
                                    if (number != null) {
                                        if (number.contains("+")) {
                                            if (number.substring(0, 6).contains(pNum)) {
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
                        break;
                }
            }

        }
    }


    private void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(tm);
            //telephonyService.silenceRinger();
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
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    private void blockCall(Context context, String type) {
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_RING, true);
        disconnectPhoneItelephony(context);


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

            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamMute(AudioManager.STREAM_RING, false);
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
