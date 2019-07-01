package com.example.den.remontecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettinsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.preferences, false);
    }
}
