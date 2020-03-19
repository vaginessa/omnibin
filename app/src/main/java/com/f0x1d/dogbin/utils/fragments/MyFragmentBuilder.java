package com.f0x1d.dogbin.utils.fragments;

import androidx.fragment.app.Fragment;

import com.f0x1d.dogbin.ui.fragment.HistoryFragment;
import com.f0x1d.dogbin.ui.fragment.MyNotesFragment;
import com.f0x1d.dogbin.ui.fragment.settings.SettingsFragment;
import com.f0x1d.dogbin.ui.fragment.settings.SettingsWrapperFragment;

public class MyFragmentBuilder implements FragmentNavigator.FragmentBuilder {
    @Override
    public Fragment getFragment(String tag) {
        switch (tag) {
            case "settings":
                return SettingsWrapperFragment.newInstance();
            case "settings_pref":
                return SettingsFragment.newInstance();
            case "my_notes":
                return MyNotesFragment.newInstance();
            case "history":
                return HistoryFragment.newInstance();

            default:
                return null;
        }
    }
}
