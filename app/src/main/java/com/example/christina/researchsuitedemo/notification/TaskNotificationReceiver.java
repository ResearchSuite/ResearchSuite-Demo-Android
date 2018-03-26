package com.example.christina.researchsuitedemo.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.christina.researchsuitedemo.SplashActivity;


/**
 * Created by jameskizer on 4/22/17.
 */

public class TaskNotificationReceiver extends BroadcastReceiver {

    public static int NOTIFICATION_ID = 0x1337;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        return intent;
    }

    public void onReceive(Context context, Intent intent) {

        Log.i("TaskNotifReceiver", "onReceive()");
        Intent activityIntent = new Intent(context, SplashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationConfig config = new SimpleNotificationConfig();
        Notification notification = new NotificationCompat.Builder(context).setContentIntent(
                contentIntent)
                .setSmallIcon(config.getSmallIcon())
                .setColor(config.getLargeIconBackgroundColor(context))
                .setTicker(config.getTickerText(context))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(config.getContentTitle(context))
                .setContentText(config.getContentText(context))
                .build();

        // Execute Notification
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                NOTIFICATION_ID,
                notification);

        //reset notification
        context.sendBroadcast(TaskAlertReceiver.createSetNotificationIntent());
    }
}
