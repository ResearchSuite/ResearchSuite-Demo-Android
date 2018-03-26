package com.example.christina.researchsuitedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.christina.researchsuitedemo.studyManagement.RSActivity;
import com.example.christina.researchsuitedemo.studyManagement.RSActivityManager;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity extends RSActivity {

    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_IF_ROOM = 4;
    private static final int REQUEST_SETTINGS = 0xff31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RSActivityManager.get().queueActivity(this, "PAMSurvey", true);
        RSActivityManager.get().queueActivity(this, "YADLSpotSurvey", true);
    }

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
