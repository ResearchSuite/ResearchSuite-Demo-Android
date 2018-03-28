package com.example.christina.researchsuitedemo;

import android.content.Intent;

import com.curiosityhealth.ls2sdk.core.manager.LS2Manager;
import com.example.christina.researchsuitedemo.notificationManagement.NotificationTime;
import com.example.christina.researchsuitedemo.notificationManagement.TaskAlertReceiver;
import com.example.christina.researchsuitedemo.studyManagement.RSActivityManager;
import com.example.christina.researchsuitedemo.studyManagement.RSFileAccess;
import com.example.christina.researchsuitedemo.studyManagement.RSFirstRun;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.ui.PinCodeActivity;

/**
 * Created by christinatsangouri on 3/24/18.
 */

public class SplashActivity extends PinCodeActivity {

    private boolean isSignedIn() {
        return LS2Manager.getInstance().isSignedIn();
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        this.sendBroadcast(TaskAlertReceiver.createSetNotificationIntent());

        RSFirstRun firstRun = new RSFirstRun(this);
        if (firstRun.getFirstRun() == null || !firstRun.getFirstRun()) {
            RSFileAccess.getInstance().clearFileAccess(this);
        }

        LS2Manager.getInstance().setCredentialStoreUnlocked(true);

        if (firstRun.getFirstRun() != null &&
                firstRun.getFirstRun() &&
                this.isSignedIn()) {
            this.launchMainActivity();
        }
        else {
            firstRun.setFirstRun(true);
            this.launchLoginActivity();
        }



    }

    @Override
    public void onDataAuth()
    {
        if(StorageAccess.getInstance().hasPinCode(this))
        {
            super.onDataAuth();
        }
        else // allow them through to onboarding if no pincode
        {
            onDataReady();
        }
    }

    @Override
    public void onDataFailed()
    {
        super.onDataFailed();
        finish();
    }

    private void launchLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        //RSActivityManager.get().queueActivity(this, "NotificationDateSurvey", true);


        finish();


    }



    private void launchMainActivity()
    {
        NotificationTime notificationTime = new NotificationTime(this);
        if(notificationTime.getNotificationTimeHour() == null) {
            RSActivityManager.get().queueActivity(this, "NotificationDateSurvey", true);
        }

       // RSActivityManager.get().queueActivity(this, "YADLFullSurvey", true);
        startActivity(new Intent(this, MainActivity.class));
        finish();


    }

}

