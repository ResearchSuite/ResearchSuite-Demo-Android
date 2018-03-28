package com.example.christina.researchsuitedemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.christina.researchsuitedemo.notificationManagement.NotificationTime;
import com.example.christina.researchsuitedemo.studyManagement.RSActivityManager;
import com.example.christina.researchsuitedemo.studyManagement.RSFileAccess;

import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.utils.LogExt;

/**
 * Created by christinatsangouri on 3/26/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, StorageAccessListener {
    private Preference setNotificationTime;
    private Preference participantSince;

    public static final String KEY_SET_NOTIFICATION_TIME = "set_notification_time";
    public static final String KEY_PARTICIPANT_SINCE = "participant_since";
    public static final String KEY_SIGN_OUT = "sign_out";
    public static final String KEY_YADL_FULL_ASSESSMENT = "yadl_full_assessment";
    public static final String KEY_YADL_SPOT_ASSESSMENT = "yadl_spot_assessment";
    public static final String KEY_DEMOGRAPHICS_ASSESSMENT = "demographics_assessment";
    public static final String KEY_PAM_ASSESSMENT = "pam_assessment";

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.addPreferencesFromResource(R.xml.rs_settings);

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();
        screen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        setNotificationTime = screen.findPreference(KEY_SET_NOTIFICATION_TIME);
        participantSince = screen.findPreference(KEY_PARTICIPANT_SINCE);


        this.updateUI();
    }



    @Override
    public boolean onPreferenceTreeClick(Preference preference)
    {
        LogExt.i(getClass(), String.valueOf(preference.getTitle()));

        if(preference.hasKey()) {

            String key = preference.getKey();

            if (key.equals(KEY_SIGN_OUT)) {
                //TODO: Fix sign out pin code / encryption issue
                if(getActivity() instanceof SettingsActivity) {
                    ((SettingsActivity)getActivity()).signOut();
                }

                return true;
            }
            else if (key.equals(KEY_SET_NOTIFICATION_TIME)) {
                RSActivityManager.get().queueActivity(getActivity(), "NotificationDateSurvey", true);
                return true;
            }
            else if (key.equals(KEY_YADL_FULL_ASSESSMENT)){
                RSActivityManager.get().queueActivity(getActivity(), "YADLFullSurvey", true);
                return true;
            }
            else if (key.equals(KEY_YADL_SPOT_ASSESSMENT)){
                RSActivityManager.get().queueActivity(getActivity(), "YADLSpotSurvey", true);
                return true;
            }
            else if (key.equals(KEY_PAM_ASSESSMENT)){
                RSActivityManager.get().queueActivity(getActivity(), "PAMSurvey", true);
                return true;
            }
            else if (key.equals(KEY_DEMOGRAPHICS_ASSESSMENT)){
                RSActivityManager.get().queueActivity(getActivity(), "DemographicsSurvey", true);
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        this.updateUI();
    }

    public void updateUI() {
        //load proper morning and evening survey times
        NotificationTime notificationTime = new NotificationTime(getActivity());
        this.setNotificationTime.setSummary(notificationTime.getNotificationTimeString(getActivity()));
        this.participantSince.setSummary(RSFileAccess.getInstance().getParticipantSinceDate(getActivity()));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onDataReady() {

    }

    @Override
    public void onDataFailed() {

    }

    @Override
    public void onDataAuth() {

    }
}
