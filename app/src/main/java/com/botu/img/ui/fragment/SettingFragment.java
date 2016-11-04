package com.botu.img.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.botu.img.R;

/**
 * @author: swolf
 * @date : 2016-11-04 11:22
 */
public class SettingFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
