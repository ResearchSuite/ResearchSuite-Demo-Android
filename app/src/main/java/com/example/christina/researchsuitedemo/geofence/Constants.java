package com.example.christina.researchsuitedemo.geofence;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

/**
 * Created by christinatsangouri on 4/30/18.
 */

public class Constants {
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 300;
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
    static {
        LANDMARKS.put("Cornell Tech", new LatLng(40.755853718172524,-73.95614623194655));

    }

}
