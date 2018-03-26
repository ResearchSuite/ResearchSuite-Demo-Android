package com.example.christina.researchsuitedemo.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jameskizer on 4/22/17.
 */

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("DeviceBootReceiver", "onReceive()");
       // RSGeofenceActivityManager.get().queueActivity(context, "LocationOnboarding", true);



        //context.sendBroadcast(TaskAlertReceiver.createSetNotificationIntent());
    }
}
