package com.example.colin.receiver;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SetupActivity
        extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setup);
        addPreferencesFromResource(R.xml.preferences);
        EditTextPreference locationpref = (EditTextPreference)findPreference("location");
        locationpref.setSummary(locationpref.getText());
        EditTextPreference serverpref = (EditTextPreference)findPreference("server");
        serverpref.setSummary(serverpref.getText());
        EditTextPreference timeoutpref = (EditTextPreference)findPreference("timeout");
        timeoutpref.setSummary(timeoutpref.getText());
        EditTextPreference threshpref = (EditTextPreference)findPreference("rssi_thresh");
        threshpref.setSummary(threshpref.getText());

        locationpref.setOnPreferenceChangeListener(this);
        serverpref.setOnPreferenceChangeListener(this);
        timeoutpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String strValue = (String)newValue;
                for(char c : strValue.toCharArray())
                {
                    if(!Character.isDigit(c))
                    {
                        return false;
                    }
                }
                preference.setSummary(strValue);
                return true;
            }
        });
        threshpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String strValue = (String)newValue;
                for(char c : strValue.toCharArray())
                {
                    if(!Character.isDigit(c))
                    {
                        return false;
                    }
                }
                preference.setSummary(strValue);
                return true;
            }
        });

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary(newValue.toString());
        return true;

    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_setup, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
