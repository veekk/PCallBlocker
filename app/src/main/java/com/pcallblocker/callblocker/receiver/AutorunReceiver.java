package com.pcallblocker.callblocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pcallblocker.callblocker.service.NotifyService;
import com.pcallblocker.callblocker.service.SyncService;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

/**
 * Crafted by veek on 10.08.16 with love ♥
 */
public class AutorunReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SyncService.class));
        CustomPreferenceManager pm = CustomPreferenceManager.getInstance();
        pm.init(context, "settings");
        if (pm.getState("notification_on")) context.startService(new Intent(context, NotifyService.class));
    }
}
