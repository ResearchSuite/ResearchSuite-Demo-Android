package com.example.christina.researchsuitedemo.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jameskizer on 9/28/17.
 */

public abstract class RSGeofenceManagerBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = RSGeofenceManagerBroadcastReceiver.class.getSimpleName();

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Intent Actions
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private static final String START_MONITORING_SAVED_REGIONS     = "io.smalldata.ancile.common.location.START_MONITORING_SAVED_REGIONS";
    private static final String STOP_MONITORING_ALL_REGIONS     = "io.smalldata.ancile.common.location.STOP_MONITORING_ALL_REGIONS";
    private static final String RESTART_MONITORING_SAVED_REGIONS     = "io.smalldata.ancile.common.location.RESTART_MONITORING_SAVED_REGIONS";

    static final int LOITERING_DELAY = 1000;
    static final int RESPONSIVENESS = 1000;

    interface Completion {
        void onCompletion();
    }

//    private static RSGeofenceManagerBroadcastReceiver manager = null;
//    private static Object managerLock = new Object();

    /**
     * Provides access to the Geofencing API.
     */
//    private GeofencingClient mGeofencingClient;

    /**
     * Used when requesting to add or remove geofences.
     */
//    private PendingIntent mGeofencePendingIntent = null;
//    private Context mContext;

//    @Nullable
//    public static RSGeofenceManagerBroadcastReceiver getInstance() {
//        synchronized (managerLock) {
//            return manager;
//        }
//    }

    public static Intent startMonitoringSavedRegionsIntent() {
        return new Intent(START_MONITORING_SAVED_REGIONS);
    }

    public static Intent stopMonitoringSavedRegionsIntent() {
        return new Intent(STOP_MONITORING_ALL_REGIONS);
    }

    public static Intent restartMonitoringSavedRegionsIntent() {
        return new Intent(RESTART_MONITORING_SAVED_REGIONS);
    }


    public void onReceive(final Context context, Intent intent)
    {
        Log.i(TAG, "onReceive() _ " + intent.getAction());

        switch(intent.getAction())
        {
            case START_MONITORING_SAVED_REGIONS:
                startMonitoringSavedRegions(context, null);
                break;

            case STOP_MONITORING_ALL_REGIONS:
                stopMonitoringAllRegions(context, null);
                break;

            case RESTART_MONITORING_SAVED_REGIONS:
                stopMonitoringAllRegions(context, new Completion() {
                    @Override
                    public void onCompletion() {
                        startMonitoringSavedRegions(context, null);
                    }
                });

                break;
        }
    }

    public void startMonitoringSavedRegions(Context context, final Completion completion) {

        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //do some toasting / error handling here
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //this can return null if there are no regions to monitor
        GeofencingRequest request = generateGeofencingRequest(context);
        if (request == null) {
            return;
        }

        PendingIntent pendingIntent = getGeofencePendingIntent(context);

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);

        Executor executor = Executors.newSingleThreadExecutor();

        geofencingClient.addGeofences(request, pendingIntent)
                .addOnSuccessListener(executor, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Add Geofence Successful");
                        if (completion != null) {
                            completion.onCompletion();
                        }
                    }
                })
                .addOnFailureListener(executor, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (completion != null) {
                            completion.onCompletion();
                        }
                    }
                });
    }

    protected void stopMonitoringAllRegions(Context context, final Completion completion) {

        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);

        PendingIntent pendingIntent = getGeofencePendingIntent(context);

        Executor executor = Executors.newSingleThreadExecutor();

        geofencingClient.removeGeofences(pendingIntent)
                .addOnSuccessListener(executor, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Removed Geofence Successful");
                        if (completion != null) {
                            completion.onCompletion();
                        }
                    }
                })
                .addOnFailureListener(executor, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (completion != null) {
                            completion.onCompletion();
                        }
                    }
                });
    }


    /**
     * Generates a list of geofences from the saved regions
     */
    protected List<Geofence> getGeofences(Context context) {
        ArrayList<Geofence> geofences = new ArrayList<>();

        SavedRegions savedRegions = new SavedRegions(context);

        for (RSRegion region : savedRegions.getRSRegions()) {

            Geofence.Builder geofenceBuilder = new Geofence.Builder()
                    .setRequestId(region.getIdentifier())
                    .setCircularRegion(region.getLatitude(),region.getLongitude(),region.getRadius())
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setLoiteringDelay(LOITERING_DELAY)
                    .setNotificationResponsiveness(RESPONSIVENESS);


            geofences.add(geofenceBuilder.build());

        }

        return geofences;
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    @Nullable
    protected GeofencingRequest generateGeofencingRequest(Context context) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);

        // Add the geofences to be monitored by geofencing service.
        List<Geofence> geofences = getGeofences(context);
        if (geofences.size() == 0) {
            return null;
        }

        builder.addGeofences(geofences);

        // Return a GeofencingRequest.
        return builder.build();
    }



    //the transition intent service is abstract
    //make the concrete implementation of this class
    //create the pending intent
    protected abstract Class getTransitionIntentServiceClass();

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    protected PendingIntent getGeofencePendingIntent(Context context) {
        Intent intent = new Intent(context, getTransitionIntentServiceClass());
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
