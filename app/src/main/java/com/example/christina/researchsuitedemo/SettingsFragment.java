package com.example.christina.researchsuitedemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import com.example.christina.researchsuitedemo.notificationManagement.NotificationTime;
import com.example.christina.researchsuitedemo.studyManagement.RSActivityManager;
import com.example.christina.researchsuitedemo.studyManagement.RSFileAccess;
import com.example.christina.researchsuitedemo.studyManagement.RSGeofenceManager;
import com.example.christina.researchsuitedemo.studyManagement.RSTaskBuilderManager;

import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.utils.LogExt;
import org.researchsuite.rstb.RSTBStateHelper;

import java.io.UnsupportedEncodingException;

/**
 * Created by christinatsangouri on 3/26/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, StorageAccessListener {
    private Preference setNotificationTime;
    private Preference participantSince;
    private Preference setHomeLocation;
    private Preference setWorkLocation;

    public static final String KEY_HOME_LOCATION = "user_input_home";
    public static final String KEY_WORK_LOCATION = "user_input_work";

    public static final String KEY_SET_NOTIFICATION_TIME = "set_notification_time";
    public static final String KEY_PARTICIPANT_SINCE = "participant_since";
    public static final String KEY_SIGN_OUT = "sign_out";
    public static final String KEY_YADL_FULL_ASSESSMENT = "yadl_full_assessment";
    public static final String KEY_YADL_SPOT_ASSESSMENT = "yadl_spot_assessment";
    public static final String KEY_DEMOGRAPHICS_ASSESSMENT = "demographics_assessment";
    public static final String KEY_PAM_ASSESSMENT = "pam_assessment";
    public static final String KEY_SET_HOME_LOCATION = "set_home_location";
    public static final String KEY_SET_WORK_LOCATION = "set_work_location";

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.addPreferencesFromResource(R.xml.rs_settings);
        RSTBStateHelper stateHelper = RSTaskBuilderManager.getBuilder().getStepBuilderHelper().getStateHelper();

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();
        screen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        setNotificationTime = screen.findPreference(KEY_SET_NOTIFICATION_TIME);
        participantSince = screen.findPreference(KEY_PARTICIPANT_SINCE);
        setHomeLocation = screen.findPreference(KEY_SET_HOME_LOCATION);
        setWorkLocation = screen.findPreference(KEY_SET_WORK_LOCATION);

        Preference homePreference = (Preference) findPreference(KEY_SET_HOME_LOCATION);

        byte[] homeLocation = stateHelper.valueInState(getContext(),"address_home");
        try {
            String homeLocationString = new String(homeLocation, "UTF-8");
            homePreference.setSummary(homeLocationString);
        } catch (UnsupportedEncodingException e) {
            homePreference.setSummary("unsaved location");
            e.printStackTrace();
        }

        Preference workPreference = (Preference) findPreference(KEY_SET_WORK_LOCATION);

        byte[] workLocation = stateHelper.valueInState(getContext(),"address_work");
        try {
            String workLocationString = new String(workLocation, "UTF-8");
            workPreference.setSummary(workLocationString);
        } catch (UnsupportedEncodingException e) {
            workPreference.setSummary("unsaved location");
            e.printStackTrace();
        }


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

            else if (key.equals(KEY_SET_HOME_LOCATION)){
                RSActivityManager.get().queueActivity(getActivity(), "HomeLocation", true);
                return true;
            }


            else if (key.equals(KEY_SET_WORK_LOCATION)){
                RSActivityManager.get().queueActivity(getActivity(), "WorkLocation", true);
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onStart(){

        super.onStart();

        stopMonitoringGeofences();
        ((SettingsActivity)getActivity()).updateGeofences();
        updateUI();
    }

    @Override
    public void onNavigateToScreen(PreferenceScreen preferenceScreen){

        super.onNavigateToScreen(preferenceScreen);

        stopMonitoringGeofences();

        ((SettingsActivity)getActivity()).updateGeofences();

        // updateUI();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        this.updateUI();
    }

    public void updateUI() {
        //load proper morning and evening survey times

        RSTBStateHelper stateHelper = RSTaskBuilderManager.getBuilder().getStepBuilderHelper().getStateHelper();

        NotificationTime notificationTime = new NotificationTime(getActivity());
        this.setNotificationTime.setSummary(notificationTime.getNotificationTimeString(getActivity()));
        this.participantSince.setSummary(RSFileAccess.getInstance().getParticipantSinceDate(getActivity()));

        Preference homePreference = (Preference) findPreference(KEY_SET_HOME_LOCATION);

        byte[] homeLocation = stateHelper.valueInState(getContext(),"address_home");
        try {
            String homeLocationString = new String(homeLocation, "UTF-8");
            homePreference.setSummary(homeLocationString);
        } catch (UnsupportedEncodingException e) {
            homePreference.setSummary("unsaved location");
            e.printStackTrace();
        }


        Preference workPreference = (Preference) findPreference(KEY_SET_WORK_LOCATION);


        byte[] workLocation = stateHelper.valueInState(getContext(),"address_work");
        try {
            String workLocationString = new String(workLocation, "UTF-8");
            Log.d("testing updateUI(v): ",workLocationString);
            workPreference.setSummary(workLocationString);
        } catch (UnsupportedEncodingException e) {
            workPreference.setSummary("unsaved location");
            e.printStackTrace();
        }
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

    private void stopMonitoringGeofences() {
        RSGeofenceManager.getInstance().stopMonitoringGeofences(getActivity());

    }

    private void startMonitoringGeofences() {
        RSGeofenceManager.getInstance().startMonitoringGeofences(getActivity());

    }

}
