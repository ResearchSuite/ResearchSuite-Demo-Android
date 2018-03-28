package com.example.christina.researchsuitedemo.location;

/**
 * Created by jameskizer on 9/28/17.
 */

public class DemoGeofenceManagerBroadcastReceiver extends RSGeofenceManagerBroadcastReceiver {

    @Override
    protected Class getTransitionIntentServiceClass() {
        return RSGeofenceTransitionIntentService.class;
    }

}
