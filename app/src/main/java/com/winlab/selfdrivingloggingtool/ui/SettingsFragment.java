package com.winlab.selfdrivingloggingtool.ui;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.winlab.selfdrivingloggingtool.R;


/**
 * Created by Luyang on 6/29/15.
 */
public class SettingsFragment extends PreferenceFragmentCompat  {

    public static final String FRAGMENT_TAG = "my_settings_fragment";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
        // test

    }
}