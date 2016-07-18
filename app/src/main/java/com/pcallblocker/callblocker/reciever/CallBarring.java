package com.pcallblocker.callblocker.reciever;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.db.BlacklistDAO;
import com.pcallblocker.callblocker.db.RejectedCallsDAO;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.model.RejectedCall;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

/**
 * Crafted by veek on 18.06.16 with love ♥
 */
public class CallBarring extends BroadcastReceiver
{
    // This String will hold the incoming phone number
    private String number;
    Date date = new Date();
    String CountryID="";
    String CountryZipCode="";
    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    RejectedCallsDAO rejectedCallsDAO;
    List<RejectedCall> rejectedCalls;
    AudioManager am;






    @Override
    public void onReceive(Context context, Intent intent) {

            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //getNetworkCountryIso
            CountryID= manager.getSimCountryIso().toUpperCase();
            String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
            for(int i=0;i<rl.length;i++) {
                String[] g = rl[i].split(",");
                if (g[1].trim().equals(CountryID.trim())) {
                    CountryZipCode = g[0];
                    break;
                }
            }

        // If, the received action is not a type of "Phone_State", ignore it
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;

            // Else, try to do some action
        else {
            // Fetch the number of incoming call
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            preferenceManager.init(context, "settings");
            if (preferenceManager.getState("block_enabled")) {
                switch (tm.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                        if (number != null) {
                            if (preferenceManager.getState("all_numbers")) {
                                blockCall(context, "all_numbers");
                                break;
                            }


                            if (preferenceManager.getState("international")) {
                                    if (!number.substring(0, CountryZipCode.length()+1).contains(CountryZipCode)) {
                                        blockCall(context, "international");
                                        break;
                                    }
                            }

                            if (preferenceManager.getState("not_contacts")) {
                                int i = 0;
                                ContactsProvider contactsProvider = new ContactsProvider(context);
                                List<Contact> contacts = contactsProvider.getContacts().getList();
                                for (Contact contact : contacts) {
                                    if (PhoneNumberUtils.compare(contact.normilizedPhone, number)) {
                                        i++;
                                    }
                                }
                                if (i == 0) {
                                    blockCall(context, "not_contacts");
                                    break;
                                }
                            }


                            if (MainActivity.blockList.contains(new Blacklist(number, ""))) {
                                AudioManager am;
                                am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                disconnectPhoneItelephony(context);
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                for (Blacklist blacklist : MainActivity.blockList) {
                                    if (blacklist.equals(new Blacklist(number, ""))) {
                                        blockCall(context, "blacklist");
                                        break;
                                    }
                                }


                                break;
                            }
                        } else
                        if (preferenceManager.getState("hidden")) {
                            if (number == null){
                                AudioManager am;
                                am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                disconnectPhoneItelephony(context);
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                break;
                            }
                        }

                        for (Blacklist blacklist:  MainActivity.blockList){
                            if (blacklist.phoneNumber.contains("x")){
                                String pNum = blacklist.phoneNumber.replace("x", "");
                                if (number != null) {
                                    if (number.contains("+")) {
                                        if (number.substring(0, 6).contains(pNum)) {
                                            blockCall(context, "blacklist");
                                        }
                                    } else if (number.substring(0, (pNum.length()+1)).contains(pNum)){
                                        blockCall(context, "blacklist");
                                    }
                                }
                            }
                    }
                }
            }
        }

    }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context)
    {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try
        {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void blockCall(Context context, String type){
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
            if (MainActivity.rejectedCalls != null)
            MainActivity.rejectedCalls = MainActivity.rejectedCallsDAO.getAllRejectedCalls();

        }

    }


}