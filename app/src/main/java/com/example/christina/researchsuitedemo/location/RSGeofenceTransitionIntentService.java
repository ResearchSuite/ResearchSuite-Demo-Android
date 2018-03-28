package com.example.christina.researchsuitedemo.location;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.christina.researchsuitedemo.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by jameskizer on 9/28/17.
 */

public abstract class RSGeofenceTransitionIntentService extends IntentService {

    private static final String TAG = "RSGeofenceTransitionIS";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public RSGeofenceTransitionIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = RSGeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        long timestamp = geofencingEvent.getTriggeringLocation().getTime();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);

            this.handleTransition(geofenceTransition, triggeringGeofences, timestamp);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
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

        // Log.d("geofence")

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);
        Log.d("geofence","geofence");
        Log.d("geofence",triggeringGeofencesIdsString);



        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
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

            this.handleTransitionEvent(transitionEvent);
        }

    }

    protected abstract void handleTransitionEvent(RSTransitionEvent event);

    private RSTransitionEvent.Action getTransitionAction(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d("geofence action: ","entered");
                return RSTransitionEvent.Action.ENTER;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return RSTransitionEvent.Action.EXIT;
            default:
                return RSTransitionEvent.Action.UNKNOWN;
        }
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
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

}
