package com.example.christina.researchsuitedemo.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

//import com.example.christina.researchsuitedemo.location.RSGeofenceManagerBroadcastReceiver;
//import com.example.christina.researchsuitedemo.location.RSRegion;
//import com.example.christina.researchsuitedemo.location.SavedRegions;
import com.example.christina.researchsuitedemo.notificationManagement.NotificationTime;
import com.example.christina.researchsuitedemo.notificationManagement.TaskAlertReceiver;
import com.google.android.gms.maps.model.LatLng;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.utils.LogExt;
import org.researchsuite.rstb.RSTBStateHelper;
import org.researchsuite.rsuiteextensionscore.LocationResult;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by jameskizer on 4/21/17.
 */

public class RSActivityManager {

    private static final String TAG = "ActivityManager";

    private static RSActivityManager sActivityManager;

    private Boolean completedOnboarding = false;

    @NonNull
    public static RSActivityManager get() {
        if (sActivityManager == null) {
            sActivityManager = new RSActivityManager();
        }
        return sActivityManager;
    }


    private Random rand;
    private ConcurrentLinkedQueue<CTFActivityRun> activityQueue;

    private Object delegateLock = new Object();
    private RSActivityManagerDelegate delegate = null;

    public RSActivityManager() {

        this.rand = new Random();
        this.activityQueue = new ConcurrentLinkedQueue<>();
    }

    public void setDelegate(Context context, RSActivityManagerDelegate delegate) {
        synchronized (this.delegateLock) {
            this.delegate = delegate;
        }
        this.tryToLaunchActivity(context);

    }

    public void clearDelegate(RSActivityManagerDelegate delegate) {
        synchronized (this.delegateLock) {
            if (this.delegate.equals(delegate)) {
                this.delegate = null;
            }
        }
    }

    @Nullable
    private CTFScheduleItem getScheduleItem(Context context, String filename) {
        ResourcePathManager.Resource resource = new ResourcePathManager.Resource(ResourcePathManager.Resource.TYPE_JSON,
                RSResourcePathManager.BASE_PATH_JSON,
                filename,
                CTFScheduleItem.class);

        return resource.create(context);
    }

    public void readyToLaunchActivity(Context context) {
        this.tryToLaunchActivity(context);
    }

    private void tryToLaunchActivity(Context context) {

        RSActivityManagerDelegate delegate;
        synchronized (this.delegateLock) {
            if (this.delegate == null) {
                return;
            }
            delegate = this.delegate;
        }


        if (!this.activityQueue.isEmpty()) {

            CTFActivityRun activityRun = this.activityQueue.peek();

            boolean launched = delegate.tryToLaunchRSActivity(this, activityRun);
            if (launched) {
                this.activityQueue.poll();
            }

            Task task = loadTask(context, activityRun);

            if (task == null) {
                Toast.makeText(context,
                        org.researchstack.skin.R.string.rss_local_error_load_task,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void queueActivity(Context context, String filename, boolean tryToLaunch) {

        CTFScheduleItem scheduleItem = this.getScheduleItem(context, filename);
        CTFActivityRun activityRun = activityRunForItem(scheduleItem);

        this.activityQueue.add(activityRun);
        this.tryToLaunchActivity(context);
    }

    public CTFActivityRun activityRunForItem(CTFScheduleItem item) {

        CTFActivityRun activityRun = new CTFActivityRun(
                item.identifier,
                UUID.randomUUID(),
                0,
                item.activity,
                item.resultTransforms
        );

        return activityRun;
    }

    @Nullable
    public Task loadTask(Context context, CTFActivityRun activityRun) {

        List<Step> stepList = null;
        try {
            stepList = RSTaskBuilderManager.getBuilder().stepsForElement(activityRun.activity);
        }
        catch(Exception e) {
            Log.w(this.TAG, "could not create steps from task json", e);
            return null;
        }
        if (stepList != null && stepList.size() > 0) {
            return new OrderedTask(activityRun.identifier, stepList);
        }
        else {
            return null;
        }
    }

    public void completeActivity(Context context, TaskResult taskResult, CTFActivityRun activityRun) {

        assert(activityRun != null);
        RSFileAccess state = RSFileAccess.getInstance();


        if (activityRun.identifier.equals("notification_date")) {
            StepResult stepResult = taskResult.getStepResult("notification_time_picker");
            if (stepResult != null) {
                long notificationTimeInMS = (long) stepResult.getResult();
                Calendar notificationCalendar = Calendar.getInstance();
                notificationCalendar.setTimeInMillis(notificationTimeInMS);

                NotificationTime notificationTime = new NotificationTime(context);
                notificationTime.setNotificationTime(notificationCalendar.get(Calendar.HOUR_OF_DAY), notificationCalendar.get(Calendar.MINUTE));

                context.sendBroadcast(TaskAlertReceiver.createSetNotificationIntent());
            }
        }

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        RSTBStateHelper stateHelper = RSTaskBuilderManager.getBuilder().getStepBuilderHelper().getStateHelper();

        if(Objects.equals(activityRun.identifier, "location_survey")){

            StepResult stepResultHome = taskResult.getStepResult("home_location_step");
            Map stepResultsHome = stepResultHome.getResults();
            LocationResult locationStepResultHome= (LocationResult) stepResultsHome.get("answer");
            Double latitudeHome = locationStepResultHome.getLatitude();
            Double longitudeHome = locationStepResultHome.getLongitute();
            String userInputHome = locationStepResultHome.getUserInput();
            String addressHome = locationStepResultHome.getAddress();

            Log.d("location result:",String.valueOf(latitudeHome));

            StepResult stepResultWork = taskResult.getStepResult("work_location_step");
            Map stepResultsWork = stepResultWork.getResults();
            LocationResult locationStepResultWork= (LocationResult) stepResultsWork.get("answer");
            Double latitudeWork = locationStepResultWork.getLatitude();
            Double longitudeWork = locationStepResultWork.getLongitute();
            String userInputWork = locationStepResultWork.getUserInput();
            String addressWork = locationStepResultWork.getAddress();

            if(stateHelper != null){
                stateHelper.setValueInState(context,"latitude_home",String.valueOf(latitudeHome).getBytes());
                stateHelper.setValueInState(context,"latitude_work",String.valueOf(latitudeWork).getBytes());
                stateHelper.setValueInState(context,"longitude_home",String.valueOf(longitudeHome).getBytes());
                stateHelper.setValueInState(context,"longitude_work",String.valueOf(longitudeWork).getBytes());
                stateHelper.setValueInState(context,"user_input_home",String.valueOf(userInputHome).getBytes());
                stateHelper.setValueInState(context,"user_input_work",String.valueOf(userInputWork).getBytes());
                stateHelper.setValueInState(context,"address_home",String.valueOf(addressHome).getBytes());
                stateHelper.setValueInState(context,"address_work",String.valueOf(addressWork).getBytes());
            }

            this.completedOnboarding = true;


            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("user_input_home", userInputHome);
            editor.putString("user_input_work", userInputWork);
            editor.commit();

//            SavedRegions savedRegions = new SavedRegions(context);
//
//            StepResult homeResult = taskResult.getStepResult("home_location_step");
//            if (homeResult != null && homeResult.getResult() != null) {
//
//
//                LocationResult home = (LocationResult)homeResult.getResult();
//                RSRegion region = new RSRegion(
//                        "home",
//                        home.getLatitude(),
//                        home.getLongitute(),
//                        RSApplication.GEOFENCE_RADIUS
//                );
//
//                savedRegions.addRSRegion(region);
//
//                RSFileAccess.getInstance().setHomeLocation(context, new LatLng(home.getLatitude(), home.getLongitute()));
//
//            }
//
//
//            state.setConfiguredLocations(context, true);
//
//
//            //restart monitoring location
//            Intent restartMonitoringGeofences = RSGeofenceManagerBroadcastReceiver.restartMonitoringSavedRegionsIntent();
//            context.sendBroadcast(restartMonitoringGeofences);
//            completion.onCompletion(null);


//            StepResult stepResultHome = taskResult.getStepResult("home_location_step");
//            Map stepResultsHome = stepResultHome.getResults();
//            LocationStepResult locationStepResultHome= (LocationStepResult) stepResultsHome.get("answer");
//            Double latitudeHome = locationStepResultHome.getLatitude();
//            Double longitudeHome = locationStepResultHome.getLongitute();
//            String userInputHome = locationStepResultHome.getUserInput();
//            String addressHome = locationStepResultHome.getAddress();
//
//
//            if(stateHelper != null){
//                stateHelper.setValueInState(context,"latitude_home",String.valueOf(latitudeHome).getBytes());
//                stateHelper.setValueInState(context,"longitude_home",String.valueOf(longitudeHome).getBytes());
//                stateHelper.setValueInState(context,"user_input_home",String.valueOf(userInputHome).getBytes());
//                stateHelper.setValueInState(context,"address_home",String.valueOf(addressHome).getBytes());
//            }
//
//            SharedPreferences.Editor editor = mSharedPreferences.edit();
//            editor.putString("user_input_home", userInputHome);
//            editor.commit();

        }

        if(Objects.equals(activityRun.identifier, "location_survey_home")){

            StepResult stepResultHome = taskResult.getStepResult("home_location_step");
            Map stepResultsHome = stepResultHome.getResults();
            LocationResult locationStepResultHome= (LocationResult) stepResultsHome.get("answer");
            Double latitudeHome = locationStepResultHome.getLatitude();
            Double longitudeHome = locationStepResultHome.getLongitute();
            String userInputHome = locationStepResultHome.getUserInput();
            String addressHome = locationStepResultHome.getAddress();

            if(stateHelper != null){
                stateHelper.setValueInState(context,"latitude_home",String.valueOf(latitudeHome).getBytes());
                stateHelper.setValueInState(context,"longitude_home",String.valueOf(longitudeHome).getBytes());
                stateHelper.setValueInState(context,"user_input_home",String.valueOf(userInputHome).getBytes());
                stateHelper.setValueInState(context,"address_home",String.valueOf(addressHome).getBytes());

            }

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("user_input_home", userInputHome);
            editor.commit();



        }

        if(Objects.equals(activityRun.identifier, "location_survey_work")){

            StepResult stepResultWork = taskResult.getStepResult("work_location_step");
            Map stepResultsWork = stepResultWork.getResults();
            LocationResult locationStepResultWork= (LocationResult) stepResultsWork.get("answer");
            Double latitudeWork = locationStepResultWork.getLatitude();
            Double longitudeWork = locationStepResultWork.getLongitute();
            String userInputWork = locationStepResultWork.getUserInput();
            String addressWork = locationStepResultWork.getAddress();

            if(stateHelper != null){
                stateHelper.setValueInState(context,"latitude_work",String.valueOf(latitudeWork).getBytes());
                stateHelper.setValueInState(context,"longitude_work",String.valueOf(longitudeWork).getBytes());
                stateHelper.setValueInState(context,"user_input_work",String.valueOf(userInputWork).getBytes());
                stateHelper.setValueInState(context,"address_work",String.valueOf(addressWork).getBytes());

            }

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("user_input_work", userInputWork);
            editor.commit();
        }

        RSResultsProcessorManager.getResultsProcessor().processResult(
                context,
                taskResult,
                activityRun.taskRunUUID,
                activityRun.resultTransforms
        );

        this.tryToLaunchActivity(context);

    }



}
