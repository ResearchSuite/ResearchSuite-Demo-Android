package com.example.christina.researchsuitedemo.notificationManagement;

import android.content.Context;

import com.example.christina.researchsuitedemo.R;


/**
 * Created by jameskizer on 4/22/17.
 */

public class SimpleNotificationConfig extends NotificationConfig {

    @Override
    public int getSmallIcon() {
        return org.researchstack.skin.R.drawable.rss_ic_notification_24dp;
    }

    @Override
    public int getLargeIconBackgroundColor(Context context) {
        return context.getResources().getColor(R.color.colorAccent);
    }

    @Override
    public CharSequence getTickerText(Context context) {
        return "Survey Reminder";
    }

    @Override
    public CharSequence getContentTitle(Context context) {
        return "Survey Reminder";
    }

    @Override
    public CharSequence getContentText(Context context) {
        return "You have a new survey to complete.";
    }
}
