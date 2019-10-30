package com.example.mymusic.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.mymusic.R;

public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_preference);
    }
}
