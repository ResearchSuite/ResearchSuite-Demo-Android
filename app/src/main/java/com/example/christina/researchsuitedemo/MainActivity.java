package com.example.christina.researchsuitedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.christina.researchsuitedemo.studyManagement.RSActivity;
import com.example.christina.researchsuitedemo.studyManagement.RSActivityManager;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity extends RSActivity {

    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    private static final int REQUEST_SETTINGS = 0xff31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RSActivityManager.get().queueActivity(this, "YADLFull", true);
        RSActivityManager.get().queueActivity(this, "YADLSpot", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add("settings");
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
}
