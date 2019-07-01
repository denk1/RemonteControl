package com.example.den.remontecontrol;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;

public class SettinsActivity extends AppCompatActivity {
    public static final String IP_ADDRESS_TEXT = "ip_address_text";
    public static final String PORT_TEXT = "port_text";


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
