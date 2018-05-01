package com.example.christina.researchsuitedemo.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.curiosityhealth.ls2sdk.core.manager.LS2Manager;
import com.example.christina.researchsuitedemo.MainActivity;
import com.example.christina.researchsuitedemo.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by christinatsangouri on 4/30/18.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionIS";

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()){
            Log.d("error", String.valueOf(event.getErrorCode()));
            return;
        }
        String description = getGeofenceTransitionDetails(event);

        int geofenceTransition = event.getGeofenceTransition();

        long timestamp = event.getTriggeringLocation().getTime();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = event.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            Log.i(TAG, geofenceTransitionDetails);

            this.handleTransition(geofenceTransition, triggeringGeofences, timestamp);
        } else {
            // Log the error.
            //Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }

    }

    private void handleTransition(
            int geofenceTransition,
            List<Geofence> triggeringGeofences,
            long timestamp) {

        RSTransitionEvent.Action action = getTransitionAction(geofenceTransition);

        for (Geofence geofence : triggeringGeofences) {
            String identifier = geofence.getRequestId();
            final RSTransitionEvent transitionEvent = new RSTransitionEvent(
                    identifier,
                    action,
                    UUID.randomUUID(),
                    timestamp
            );

            handleTransitionEvent(transitionEvent);
        }

    }

    private RSTransitionEvent.Action getTransitionAction(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return RSTransitionEvent.Action.ENTER;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return RSTransitionEvent.Action.EXIT;
            default:
                return RSTransitionEvent.Action.UNKNOWN;
        }
    }

    protected void handleTransitionEvent(RSTransitionEvent event) {

        LogicalLocationSample sample = new LogicalLocationSample(
                this,
                event.getRegionIdentifier(),
                event.getAction(),
                event.getUuid(),
                event.getTimestamp()
        );

        LS2Manager.getInstance().addDatapoint(sample, new LS2Manager.Completion() {
            @Override
            public void onCompletion(Exception e) {
                Log.d("geofence","geofence posted");
            }
        });


    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);
        Log.d("geofence",triggeringGeofencesIdsString);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_entered);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    private static String getGeofenceTransitionDetails(GeofencingEvent event){
        String transitionString = GeofenceStatusCodes.getStatusCodeString(event.getGeofenceTransition());
        List triggeringIDs = new ArrayList();
        for (Geofence geofence:event.getTriggeringGeofences()){
            triggeringIDs.add(geofence.getRequestId());
        }
        return String.format("%s:%s",transitionString, TextUtils.join(",",triggeringIDs));

    }



}
