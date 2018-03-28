package com.example.christina.researchsuitedemo.location;


import android.util.Log;

import com.curiosityhealth.ls2sdk.core.manager.LS2Manager;

/**
 * Created by jameskizer on 9/28/17.
 */

public class DemoGeofenceTransisitionIntentService extends RSGeofenceTransitionIntentService {

    @Override
    protected void handleTransitionEvent(RSTransitionEvent event) {

        LogicalLocationSample sample = new LogicalLocationSample(
                this,
                event.getRegionIdentifier(),
                event.getAction(),
                event.getUuid(),
                event.getTimestamp()
        );

//        LS2Manager.getInstance().addDatapoint(sample, new LS2Manager.Completion() {
//            @Override
//            public void onCompletion(Exception e) {
//                Log.d("geofence","geofence posted");
//            }
//        });

//        OhmageOMHManager.getInstance().addDatapoint(sample, new OhmageOMHManager.Completion() {
//            @Override
//            public void onCompletion(Exception e) {
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

}
