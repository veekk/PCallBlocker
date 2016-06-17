package com.veek.callblocker.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Crafted by veek on 17.06.16 with love ♥
 */
public class CallHelper {

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone
                    //if (incomingNumber == "380508275807")
                        endCall();
                    Toast.makeText(ctx,
                            "Incoming: "+incomingNumber,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            Toast.makeText(ctx,
                    "Outgoing: "+number,
                    Toast.LENGTH_LONG).show();

        }

    }

    private Context ctx;
    private TelephonyManager tm;
    private CallStateListener callStateListener;
    private OutgoingReceiver outgoingReceiver;

    public CallHelper(Context ctx) {
        this.ctx = ctx;
        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();

    }

    /**
     * Start calls detection.
     */
    public void start() {
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        ctx.registerReceiver(outgoingReceiver, intentFilter);
    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        ctx.unregisterReceiver(outgoingReceiver);
    }

    private void endCall() {
        TelephonyManager tm = (TelephonyManager) ctx.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            telephonyService.endCall();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
