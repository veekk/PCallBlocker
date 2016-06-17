package com.veek.callblocker.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.veek.callblocker.Util.CallHelper;

import java.lang.reflect.Method;

/**
 * Crafted by veek on 17.06.16 with love ♥
 */
public class CallDetectService extends Service {
    private CallHelper callHelper;

    public CallDetectService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callHelper = new CallHelper(this);

        int res = super.onStartCommand(intent, flags, startId);
        callHelper.start();
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        callHelper.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not supporting binding
        return null;
    }


}
