package com.pcallblocker.callblocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.pcallblocker.callblocker.util.PhoneCallStateListener;

/**
 * Crafted by veek on 10.08.16 with love ♥
 */



public class PhoneCallReceiver extends BroadcastReceiver {



    static PhoneCallStateListener customPhoneListener;
    static TelephonyManager telephony;

    @Override
    public void onReceive(Context context, Intent intent) {
        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (customPhoneListener == null) {
            customPhoneListener = new PhoneCallStateListener(context);
            telephony.listen(customPhoneListener,  android.telephony.PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}
