package com.cafateria.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.cafateria.R;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;


public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        addPreferencesFromResource(R.xml.settings);
        AppSettings.setLang(this, AppSettings.getLang(this));
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);
        //defult
        findPreference(getString(R.string.lang)).setSummary(((ListPreference) findPreference(getString(R.string.lang))).getEntry());
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            if (key.equalsIgnoreCase(getString(R.string.lang))) {
                AppSettings.setLang(this, AppSettings.getLang(this));
                recreate();
            }
            pref.setSummary(((ListPreference) pref).getEntry());//sharedPreferences.getString(key, "")
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            finish();
            startActivity(new Intent(this, Global.CLASS_TO_BACK));
        }
    }

    @Override
    protected void onResume() {
        Global.CURRENT_ACTIVITY = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Global.CURRENT_ACTIVITY = null;
        super.onPause();
    }
}
