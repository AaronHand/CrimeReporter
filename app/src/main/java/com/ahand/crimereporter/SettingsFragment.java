package com.ahand.crimereporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        //needs work, reset to default
        checkDate(sharedPreferences.getString(key,""));


    }


    private boolean checkDate(String in){
        SimpleDateFormat fmt = new SimpleDateFormat("mm/dd/yyyy",java.util.Locale.getDefault());

        fmt.setLenient(false);
        try {
            Date d = fmt.parse(in);
            if(d!=null){
            //Toast.makeText(getActivity(),"Date read as: "+fmt.parse(in),Toast.LENGTH_SHORT).show();
            }
            return true;

        } catch (ParseException e) {
            //Toast.makeText(getActivity(),"Bad date format.",Toast.LENGTH_SHORT).show();
            return false;
        }


    }
}