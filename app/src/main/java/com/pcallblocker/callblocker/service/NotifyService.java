package com.pcallblocker.callblocker.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.R;

import java.util.concurrent.TimeUnit;

/**
 * Crafted by veek on 11.07.16 with love ♥
 */
public class NotifyService extends Service {


    NotificationManager nm;


    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sendNotif();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            TimeUnit.SECONDS.sleep(5);

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

        sendNotif();

        return super.onStartCommand(intent, flags, startId);
    }


    void sendNotif(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("title");
        builder.setContentText("text");
        builder.setOngoing(true);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

   /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(resultPendingIntent);

        nm.notify(1, builder.build());


    }

    @Override
    public boolean onUnbind(Intent intent) {
        nm.cancel(1);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        nm.cancel(1);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
