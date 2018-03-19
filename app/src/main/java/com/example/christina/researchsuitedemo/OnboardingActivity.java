package com.example.christina.researchsuitedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.christina.researchsuitedemo.studyManagement.RSActivity;
import com.example.christina.researchsuitedemo.studyManagement.RSActivityManager;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class OnboardingActivity extends RSActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RSActivityManager.get().queueActivity(this, "DemographicsSurvey", true);
        RSActivityManager.get().queueActivity(this, "NotificationDateSurvey", true);
        RSActivityManager.get().queueActivity(this, "PAMSurvey", true);
        RSActivityManager.get().queueActivity(this, "YADLFullSurvey", true);
        RSActivityManager.get().queueActivity(this, "YADLSpotSurvey", true);
    }




}
