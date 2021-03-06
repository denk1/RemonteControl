package com.example.den.remontecontrol;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    private EditTextPreference editTextPreferenceIp = null;
    private EditTextPreference editTextPreferencePort = null;
    private EditTextPreference editTextPreferenceIpVideo = null;
    private EditTextPreference getEditTextPreferencePortVideo = null;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String oldText;
    SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }
    */
    @Override
    public void onCreatePreferences(Bundle
                                            savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        createListener();
        editTextPreferenceIp = (EditTextPreference)findPreference(SettinsActivity.IP_ADDRESS_TEXT);
        editTextPreferencePort = (EditTextPreference)findPreference(SettinsActivity.PORT_TEXT);
        editTextPreferenceIpVideo = (EditTextPreference)findPreference(SettinsActivity.IP_ADDRESS_VIDEO_TEXT);
        getEditTextPreferencePortVideo = (EditTextPreference)findPreference(SettinsActivity.PORT_VIDEO_TEXT);
        oldText = editTextPreferenceIp.getText();
        sharedPreferences = getPreferenceManager().getSharedPreferences();
        String valueIp = sharedPreferences.getString(SettinsActivity.IP_ADDRESS_TEXT, "NULL");
        String valuePort = sharedPreferences.getString(SettinsActivity.PORT_TEXT, "NULL");
        String valueIpVideo = sharedPreferences.getString(SettinsActivity.IP_ADDRESS_VIDEO_TEXT, "NULL");
        String valuePortVideo = sharedPreferences.getString(SettinsActivity.PORT_VIDEO_TEXT, "NULL");
        editTextPreferenceIp.setSummary(valueIp);
        editTextPreferencePort.setSummary(valuePort);
        editTextPreferenceIpVideo.setSummary(valueIpVideo);
        getEditTextPreferencePortVideo.setSummary(valuePortVideo);
    }

    private void createListener() {
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String value = sharedPreferences.getString(key, "NULL");
                if(key.equals(SettinsActivity.IP_ADDRESS_TEXT)) {
                    oldText = value;
                    editTextPreferenceIp.setSummary(value);
                }
                else if(key.equals(SettinsActivity.PORT_TEXT)){
                    editTextPreferencePort.setSummary(value);
                }
                else if(key.equals(SettinsActivity.IP_ADDRESS_VIDEO_TEXT)) {
                    editTextPreferenceIpVideo.setSummary(value);
                }
                else if(key.equals(SettinsActivity.PORT_VIDEO_TEXT)) {
                    getEditTextPreferencePortVideo.setSummary(value);
                }

            }
        };
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(listener);
    }

}
