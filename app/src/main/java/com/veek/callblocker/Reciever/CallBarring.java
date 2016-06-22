package com.veek.callblocker.Reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.veek.callblocker.MainActivity;
import com.veek.callblocker.Model.Blacklist;

import java.lang.reflect.Method;

/**
 * Crafted by veek on 18.06.16 with love ♥
 */
public class CallBarring extends BroadcastReceiver
{
    // This String will hold the incoming phone number
    private String number;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // If, the received action is not a type of "Phone_State", ignore it
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;

            // Else, try to do some action
        else
        {
            // Fetch the number of incoming call
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            number = number.replace(" ", "");
            number = number.replace("-", "");
            number = number.replace("+", "");
            number = number.replace(")", "");
            number = number.replace("(", "");
            number = number.replace(".", "");
            number = "+"+number;

            // Check, whether this is a member of "Black listed" phone numbers stored in the database
            if(MainActivity.blockList.contains(new Blacklist(number)))
            {
                // If yes, invoke the method
                disconnectPhoneItelephony(context);
                return;
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
}
