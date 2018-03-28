package com.example.christina.researchsuitedemo.notificationManagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

/**
 * Created by jameskizer on 4/22/17.
 */

public class NotificationTime {
    private static final String PREFERENCES_FILE  = "notificationTime";
    private static final String KEY_NOTIFICATION_TIME_HOUR = "NotificationTime.HOUR";
    private static final String KEY_NOTIFICATION_TIME_MINUTE = "NotificationTime.MINUTE";

    protected final SharedPreferences sharedPreferences;

    private Gson gson;

    public NotificationTime(Context applicationContext) {

        this.gson = new Gson();
        sharedPreferences = applicationContext
                .getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

    }

    protected <T> T getValue(String key, Class<? extends T> klass) {
        String json = sharedPreferences.getString(key, null);
        return this.gson.fromJson(json, klass);
    }

    protected <T> void setValue(String key, T value, Class<? super T> klass) {
        String json = this.gson.toJson(value, klass);
        sharedPreferences.edit().putString(key, json).apply();
    }

    @Nullable
    public Integer getNotificationTimeHour() {
        return this.getValue(KEY_NOTIFICATION_TIME_HOUR, Integer.class);
    }

    @Nullable
    public Integer getNotificationTimeMinute() {
        return this.getValue(KEY_NOTIFICATION_TIME_MINUTE, Integer.class);
    }

    public void setNotificationTime(Integer hour, Integer minute) {
        this.setValue(KEY_NOTIFICATION_TIME_HOUR, hour, Integer.class);
        this.setValue(KEY_NOTIFICATION_TIME_MINUTE, minute, Integer.class);
    }

    public String getNotificationTimeString(Context context) {
        StringBuilder sb = new StringBuilder();

        Integer hour = this.getNotificationTimeHour();
        if (hour == null) {
            return "";
        }

        Integer minute = this.getNotificationTimeMinute();
        if (minute == null) {
            return "";
        }

        boolean am = true;
        if (hour >= 12) {
            am = false;
        }

        return String.format("%d:%02d %s", hour%12 == 0 ? 12 : hour%12, minute, am ? "AM" : "PM");
    }
}
