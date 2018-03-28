package com.example.christina.researchsuitedemo.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jameskizer on 9/28/17.
 */

//Note: this object serializes the locations to plain text
//it is available to this app and any app w/ root priveleges
//we do this because we potentially need these regions available in the case
//that the phone is rebooted and we need to start monitoring again
public class SavedRegions {


    private static final String PREFERENCES_FILE  = "savedRegions";
    private static final String REGION_IDENTIFIERS  = "savedRegions.REGION_IDENTIFIERS";

    protected final SharedPreferences sharedPreferences;

//    protected ArrayList<String> regionIdentifiers;

    private Gson gson;

    public SavedRegions(Context applicationContext) {

        this.gson = new Gson();
        sharedPreferences = applicationContext
                .getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

//        regionIdentifiers = new ArrayList<String>();
//
//        List<String> ids = this.getRegionIdentifiers();
//        if (ids != null) {
//            regionIdentifiers.addAll(ids);
//        }

    }

    protected <T> T getValue(String key, Class<? extends T> klass) {
        String json = sharedPreferences.getString(key, null);
        return this.gson.fromJson(json, klass);
    }

    protected <T> void setValue(String key, T value, Class<? super T> klass) {
        String json = this.gson.toJson(value, klass);
        sharedPreferences.edit().putString(key, json).apply();
    }

    protected void removeValue(String key) {
        sharedPreferences.edit().remove(key);
    }

    public void clearSavedRegions() {
        sharedPreferences.edit().clear();
    }

    public Set<String> getRegionIdentifiers() {
        String json = sharedPreferences.getString(REGION_IDENTIFIERS, null);
        Set<String> regionIdentifiers = this.gson.fromJson(json, new TypeToken<Set<String>>(){}.getType());
        if (regionIdentifiers == null) {
            return new HashSet<>();
        }
        else {
            return regionIdentifiers;
        }
    }

    public void setRegionIdentifiers(Set<String> regionIdentifiers) {
        String json = this.gson.toJson(regionIdentifiers, new TypeToken<Set<String>>(){}.getType());
        sharedPreferences.edit().putString(REGION_IDENTIFIERS, json).apply();
    }

    @Nullable
    public RSRegion getRSRegion(String regionKey) {
        return this.getValue(regionKey, RSRegion.class);
    }

    public void setRSRegion(String regionKey, RSRegion region) {
        this.setValue(regionKey, region, RSRegion.class);
    }

    public void addRSRegion(RSRegion region) {
        HashSet<String> regionIdentifiers = new HashSet<>(this.getRegionIdentifiers());
        this.setRSRegion(region.getIdentifier(), region);
        regionIdentifiers.add(region.getIdentifier());
        this.setRegionIdentifiers(regionIdentifiers);
    }

    public void removeRSRegion(RSRegion region) {
        HashSet<String> regionIdentifiers = new HashSet<>(this.getRegionIdentifiers());
        this.removeValue(region.getIdentifier());
        regionIdentifiers.remove(region.getIdentifier());
        this.setRegionIdentifiers(regionIdentifiers);
    }

    public Set<RSRegion> getRSRegions() {
        HashSet<RSRegion> regions = new HashSet<>();

        for (String regionIdentifier : this.getRegionIdentifiers()) {
            RSRegion region = getRSRegion(regionIdentifier);
            if (region != null) {
                regions.add(region);
            }
        }

        return regions;
    }

}
