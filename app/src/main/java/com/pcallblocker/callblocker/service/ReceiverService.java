package com.pcallblocker.callblocker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.db.BlacklistDAO;
import com.pcallblocker.callblocker.db.RejectedCallsDAO;
import com.pcallblocker.callblocker.db.UnknownDAO;
import com.pcallblocker.callblocker.receiver.CallBarring;
/**
 * Crafted by veek on 10.08.16 with love ♥
 */
public class ReceiverService extends Service {

    SharedPreferences pm;
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

        PreferenceManager.setDefaultValues(this, R.xml.prefrences, false);

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        pm = PreferenceManager.getDefaultSharedPreferences(this);

        rejectedCallsDAO =  new RejectedCallsDAO(this);
        unknownDAO = new UnknownDAO(this);
        blacklistDAO = new BlacklistDAO(this);
        BlacklistDAO blacklistDAO = new BlacklistDAO(this);
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
