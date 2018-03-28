package com.example.christina.researchsuitedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.christina.researchsuitedemo.studyManagement.RSActivity;
import com.example.christina.researchsuitedemo.studyManagement.RSActivityManager;
import com.example.christina.researchsuitedemo.studyManagement.RSApplication;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity extends RSActivity {

    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_IF_ROOM = 4;
    private static final int REQUEST_SETTINGS = 0xff31;
  //  private RSuiteGeofenceManager.PendingGeofenceTask mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.NONE;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RSApplication app = (RSApplication) getApplication();
        app.initializeGeofenceManager();

        RSActivityManager.get().queueActivity(this, "PAMSurvey", true);
        RSActivityManager.get().queueActivity(this, "YADLSpotSurvey", true);

       // this.startMonitoringGeofences();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        if (!checkPermissions()) {
//            requestPermissions();
//        } else {
//            performPendingGeofenceTask();
//        }
//    }
//
//    private void startMonitoringGeofences() {
//        if (!checkPermissions()) {
//            mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.ADD;
//            requestPermissions();
//        } else {
//            RSuiteGeofenceManager.getInstance().startMonitoringGeofences(this);
//        }
//    }
//
//    private void stopMonitoringGeofences() {
//        if (!checkPermissions()) {
//            mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.REMOVE;
//            requestPermissions();
//        } else {
//            RSuiteGeofenceManager.getInstance().stopMonitoringGeofences(this);
//        }
//    }
//
//    /**
//     * Shows a {@link Snackbar} using {@code text}.
//     *
//     * @param text The Snackbar text.
//     */
//    private void showSnackbar(final String text) {
//        View container = findViewById(android.R.id.content);
//        if (container != null) {
//            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
//        }
//    }
//
//    /**
//     * Shows a {@link Snackbar}.
//     *
//     * @param mainTextStringId The id for the string resource for the Snackbar text.
//     * @param actionStringId   The text of the action item.
//     * @param listener         The listener associated with the Snackbar action.
//     */
//    private void showSnackbar(final int mainTextStringId, final int actionStringId,
//                              View.OnClickListener listener) {
//        Snackbar.make(
//                findViewById(android.R.id.content),
//                getString(mainTextStringId),
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(getString(actionStringId), listener).show();
//    }
//
//    /**
//     * Performs the geofencing task that was pending until location permission was granted.
//     */
//    private void performPendingGeofenceTask() {
//        if (mPendingGeofenceTask == RSuiteGeofenceManager.PendingGeofenceTask.ADD) {
//            RSuiteGeofenceManager.getInstance().startMonitoringGeofences(this);
//        } else if (mPendingGeofenceTask == RSuiteGeofenceManager.PendingGeofenceTask.REMOVE) {
//            RSuiteGeofenceManager.getInstance().stopMonitoringGeofences(this);
//        }
//    }
//
//    /**
//     * Return the current state of the permissions needed.
//     */
//    private boolean checkPermissions() {
//        int permissionState = ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        return permissionState == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                REQUEST_PERMISSIONS_REQUEST_CODE);
//
//        boolean shouldProvideRationale =
//                ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (shouldProvideRationale) {
//            showSnackbar(R.string.permission_rationale, android.R.string.ok,
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(MainActivity.this,
//                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    });
//        } else {
//
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_PERMISSIONS_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.length <= 0) {
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                performPendingGeofenceTask();
//            } else {
//
//                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
//                mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.NONE;
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add("Settings");
        menuItem.setIcon(android.R.drawable.ic_menu_preferences);
        menuItem.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        menuItem.setIntent(new Intent(this, SettingsActivity.class));
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_SETTINGS);

                return true;
            }
        });



        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_SETTINGS) {
                Boolean signedOut = (Boolean) data.getSerializableExtra(SettingsActivity.EXTRA_DID_SIGN_OUT);
                if (signedOut) {
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                }
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
