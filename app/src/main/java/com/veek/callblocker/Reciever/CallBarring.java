package com.veek.callblocker.Reciever;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.veek.callblocker.Fragment.RejectedFragment;
import com.veek.callblocker.MainActivity;
import com.veek.callblocker.Model.Blacklist;
import com.veek.callblocker.Model.RejectedCall;
import com.veek.callblocker.R;
import com.veek.callblocker.Util.CustomPreferenceManager;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

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

                        if (preferenceManager.getState("international")) {
                            if (number != null) if (number.contains(CountryZipCode)) {
                                blockCall(context);
                                break;
                            }
                        }

                        if (preferenceManager.getState("hidden")) {
                            if (number != null){
                            if (Long.parseLong(number) < 0) {
                                blockCall(context);
                                break;
                            }
                            }
                        }

                        if (preferenceManager.getState("all_numbers")) {
                            blockCall(context);
                            break;
                        }

                            if (MainActivity.blockList.contains(new Blacklist(number, ""))) {
                                AudioManager am;
                                am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                disconnectPhoneItelephony(context);
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                for (Blacklist blacklist : MainActivity.blockList) {
                                    if (blacklist.equals(new Blacklist(number, ""))) {
                                        blockCall(context);
                                        break;
                                    }
                            }


                                break;
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

    private void blockCall(Context context){
        AudioManager am;
        am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        disconnectPhoneItelephony(context);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        int inc = 0;
        if (MainActivity.rejectedCalls.size() > 0) {
            for (RejectedCall rejectedCall : MainActivity.rejectedCalls) {
                if (rejectedCall.phoneNumber.equals(number)) {
                    rejectedCall.incAmount();
                    rejectedCall.updTime(date.getTime());
                    MainActivity.rejectedCallsDAO.update(rejectedCall);
                    MainActivity.rejectedCalls = MainActivity.rejectedCallsDAO.getAllRejectedCalls();
                    inc = 0;
                    break;
                } else inc++;
            }
        }
        if (inc > 0 || MainActivity.rejectedCalls.size() == 0) {
            MainActivity.rejectedCallsDAO.create(new RejectedCall(number, ""));
            MainActivity.rejectedCalls = MainActivity.rejectedCallsDAO.getAllRejectedCalls();

        }

    }


}
