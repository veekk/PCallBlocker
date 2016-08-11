package com.pcallblocker.callblocker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.pcallblocker.callblocker.db.BlacklistDAO;
import com.pcallblocker.callblocker.db.RejectedCallsDAO;
import com.pcallblocker.callblocker.db.UnknownDAO;
import com.pcallblocker.callblocker.receiver.CallBarring;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

/**
 * Crafted by veek on 10.08.16 with love ♥
 */
public class ReceiverService extends Service {

    CustomPreferenceManager pm = CustomPreferenceManager.getInstance();
    TelephonyManager tm;
    AudioManager am;
    RejectedCallsDAO rejectedCallsDAO;
    UnknownDAO unknownDAO;
    BlacklistDAO blacklistDAO;
    CallBarring callBarring;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        pm.init(this, "settings");

        rejectedCallsDAO =  new RejectedCallsDAO(this);
        unknownDAO = new UnknownDAO(this);
        blacklistDAO = new BlacklistDAO(this);
        callBarring = new CallBarring(rejectedCallsDAO, unknownDAO, blacklistDAO, pm, am, tm);

        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callBarring, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(callBarring);
        super.onDestroy();
    }
}
