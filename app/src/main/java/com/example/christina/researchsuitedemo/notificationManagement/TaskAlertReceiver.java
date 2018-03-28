package com.example.christina.researchsuitedemo.notificationManagement;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.LogExt;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jameskizer on 4/22/17.
 */

public class TaskAlertReceiver extends BroadcastReceiver {

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Intent Actions
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private static final String ALERT_CREATE_FROM_STATE     = "edu.cornell.tech.foundry.yadl.notification.ALERT_CREATE_FROM_STATE";

    public static Intent createSetNotificationIntent() {
        return new Intent(ALERT_CREATE_FROM_STATE);
    }

    public void onReceive(Context context, Intent intent)
    {
        Log.i("CreateAlertReceiver", "onReceive() _ " + intent.getAction());

        switch(intent.getAction())
        {
            case ALERT_CREATE_FROM_STATE:
                NotificationTime notificationTime = new NotificationTime(context);
                Integer hour = notificationTime.getNotificationTimeHour();
                Integer minute = notificationTime.getNotificationTimeMinute();
                if (hour != null && minute != null) {
                    this.setNotification(context, hour, minute);
                }

                break;
        }
    }

    //assume that this always returns times in the future (see assertion)
//    private Date computeNextNotificationTimeForDayOfWeek(int dayOfWeek, int hour, int minute) {
//        Calendar calendar = Calendar.getInstance();
//        Date now = calendar.getTime();
//
//        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//
//        DateFormat format = FormatHelper.getFormat(DateFormat.LONG, DateFormat.LONG);
//        LogExt.i(getClass(),
//                "Computed Date " + format.format(calendar.getTime()));
//
//        if (calendar.getTime().before(now)) {
//            calendar.add(Calendar.WEEK_OF_YEAR, 1);
//            LogExt.i(getClass(),
//                    "Updated Date " + format.format(calendar.getTime()));
//        }
//
//        assert(calendar.getTime().after(now));
//        return calendar.getTime();
//    }
//
//    private Date computeNextNotificationTime(int hour, int minute) {
//
//        Date nextMonday = this.computeNextNotificationTimeForDayOfWeek(Calendar.MONDAY, hour, minute);
//        Date nextWednesday = this.computeNextNotificationTimeForDayOfWeek(Calendar.WEDNESDAY, hour, minute);
//        Date nextFriday = this.computeNextNotificationTimeForDayOfWeek(Calendar.FRIDAY, hour, minute);
//
//        Calendar calendar = Calendar.getInstance();
//        Date now = calendar.getTime();
//
//        Date nearestDate = nextMonday;
//        if (nextWednesday.before(nearestDate)) {
//            nearestDate = nextWednesday;
//        }
//        if (nextFriday.before(nearestDate)) {
//            nearestDate = nextFriday;
//        }
//
//        DateFormat format = FormatHelper.getFormat(DateFormat.LONG, DateFormat.LONG);
//        LogExt.i(getClass(),
//                "Nearest Date " + format.format(nearestDate));
//
//
//        assert(nearestDate.after(now));
//        return nearestDate;
//    }

    private Date computeNextNotificationTime(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        DateFormat format = FormatHelper.getFormat(DateFormat.LONG, DateFormat.LONG);
        LogExt.i(getClass(),
                "Computed Date " + format.format(calendar.getTime()));

        if (calendar.getTime().before(now)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            LogExt.i(getClass(),
                    "Updated Date " + format.format(calendar.getTime()));
        }

        assert(calendar.getTime().after(now));
        return calendar.getTime();
    }


    private void setNotification(Context context, int hour, int minute) {
        Intent taskNotificationIntent = TaskNotificationReceiver.createIntent(context);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                taskNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Date nextExecuteTime = this.computeNextNotificationTime(hour, minute);

        // Create alert
        createAlert(context, pendingIntent, nextExecuteTime);
    }

    private void createAlert(Context context, PendingIntent pendingIntent, Date nextExecuteTime)
    {
        // Add Alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                nextExecuteTime.getTime(),
                pendingIntent);

        DateFormat format = FormatHelper.getFormat(DateFormat.LONG, DateFormat.LONG);
        LogExt.i(getClass(),
                "Alarm  Created. Executing on or near " + format.format(nextExecuteTime));
    }

}
