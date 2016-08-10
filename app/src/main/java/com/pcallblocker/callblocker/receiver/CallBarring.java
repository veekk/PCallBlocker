package com.pcallblocker.callblocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.db.BlacklistDAO;
import com.pcallblocker.callblocker.db.DBHelper;
import com.pcallblocker.callblocker.db.RejectedCallsDAO;
import com.pcallblocker.callblocker.db.UnknownDAO;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.model.RejectedCall;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.model.UnknownNumber;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;
import com.pcallblocker.callblocker.util.CustomRestClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

/**
 * Crafted by veek on 18.06.16 with love ♥
 */
public class CallBarring extends BroadcastReceiver {
    // This String will hold the incoming phone number
    private String number;
    Date date = new Date();
    String CountryID = "";
    String CountryZipCode = "";
    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    RejectedCallsDAO rejectedCallsDAO;
    UnknownDAO unknownDAO;
    List<RejectedCall> rejectedCalls;
    AudioManager am;
    TelephonyManager manager;
    static CallStateListener phoneListener;


    @Override
    public void onReceive(Context context, Intent intent) {

        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }

        if (unknownDAO == null) unknownDAO = new UnknownDAO(context);

        if (phoneListener == null) {
            preferenceManager.init(context, "settings");
            phoneListener = new CallStateListener(context);
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

    }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    class CallStateListener extends PhoneStateListener {

        Context context;

        public CallStateListener(Context context) {
            this.context = context;
        }

        private void blockCall(Context context, String type) {
            am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            disconnectPhoneItelephony(context);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            int inc = 0;


            rejectedCallsDAO = new RejectedCallsDAO(context);
            rejectedCalls = rejectedCallsDAO.getAllRejectedCalls();

            if (rejectedCalls.size() > 0) {
                for (RejectedCall rejectedCall : rejectedCalls) {
                    if (rejectedCall.phoneNumber.equals(number)) {
                        rejectedCall.incAmount();
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

            }

        }

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    number = phoneNumber;
                    BlacklistDAO blacklistDAO = new BlacklistDAO(context);
                    List<Blacklist> blockList = blacklistDAO.getAllBlacklist();

                    int i = 0;
                    ContactsProvider contactsProvider = new ContactsProvider(context);
                    List<Contact> contacts = contactsProvider.getContacts().getList();
                    for (Contact contact : contacts) {
                        if (PhoneNumberUtils.compare(contact.normilizedPhone, number)) {
                            i++;
                        }
                    }
                    if (i == 0) {
                        unknownDAO.create(new UnknownNumber(manager.getLine1Number().replace("+", ""), number.replace("+", "")));
                        JSONArray jsonObject = getResults(context);
                        String message = jsonObject.toString();
                        CustomRestClient restClient = new CustomRestClient();
                        RequestParams requestParams = new RequestParams("json", message);
                        restClient.post("json.php", requestParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                unknownDAO.clear();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
                        if (preferenceManager.getState("not_contacts")) {
                            blockCall(context, "not_contacts");
                            break;
                        }
                    }

                    if (number != null) {
                        if (preferenceManager.getState("all_numbers")) {
                            blockCall(context, "all_numbers");
                            break;
                        }


                        if (preferenceManager.getState("international")) {
                            if (!number.substring(0, CountryZipCode.length() + 1).contains(CountryZipCode)) {
                                blockCall(context, "international");
                                break;
                            }
                        }



                        if (blockList.contains(new Blacklist(number, ""))) {
                            AudioManager am;
                            am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            disconnectPhoneItelephony(context);
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            for (Blacklist blacklist : blockList) {
                                if (blacklist.equals(new Blacklist(number, ""))) {
                                    blockCall(context, "blacklist");
                                    break;
                                }
                            }


                            break;
                        }
                    } else if (preferenceManager.getState("hidden")) {
                        if (number == null) {
                            if (rejectedCallsDAO == null)
                                rejectedCallsDAO = new RejectedCallsDAO(context);
                            AudioManager am;
                            am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
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
                    break;

            }


            super.onCallStateChanged(state, number);
        }
    }

    private JSONArray getResults(Context context)
    {

        SQLiteDatabase database;
        DBHelper dbHelper;
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

}
