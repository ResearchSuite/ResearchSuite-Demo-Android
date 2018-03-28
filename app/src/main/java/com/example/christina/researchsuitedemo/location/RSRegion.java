package com.example.christina.researchsuitedemo.location;

import java.io.Serializable;

/**
 * Created by jameskizer on 9/28/17.
 */

public class RSRegion implements Serializable {
    private String identifier;
    private double latitude;
    private double longitude;
    private float radius;

    public RSRegion(String identifier, double latitude, double longitude, float radius) {
        this.identifier = identifier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public String getIdentifier() {
        return identifier;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }
}
