package com.pcallblocker.callblocker.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.pcallblocker.callblocker.db.BlacklistDAO;
import com.pcallblocker.callblocker.db.RejectedCallsDAO;
import com.pcallblocker.callblocker.db.UnknownDAO;
//import com.pcallblocker.callblocker.receiver.CallBarring;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;
import com.pcallblocker.callblocker.util.PhoneCallStateListener;

/**
 * Crafted by veek on 10.08.16 with love ♥
 */
public class ReceiverService extends Service {

    CustomPreferenceManager pm = CustomPreferenceManager.getInstance();
    TelephonyManager tm;
    AudioManager am;
    static PhoneCallStateListener customPhoneListener;
    Context ctx;
    RejectedCallsDAO rejectedCallsDAO;
    UnknownDAO unknownDAO;
    BlacklistDAO blacklistDAO;
    //CallBarring callBarring;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if (customPhoneListener == null) {
//            customPhoneListener = new PhoneCallStateListener(this, tm, am);
//            tm.listen(customPhoneListener,  PhoneStateListener.LISTEN_CALL_STATE);
//        }
//        pm.init(this, "settings");
//
//        rejectedCallsDAO =  new RejectedCallsDAO(this);
////        unknownDAO = new UnknownDAO(this);
////        blacklistDAO = new BlacklistDAO(this);
//          receiver = new Receiver();
////
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, ReceiverService.class));
    }

}
