package com.f0x1d.dogbin.utils.fragments;

import androidx.fragment.app.Fragment;
import com.f0x1d.dogbin.ui.fragment.folders.FoldersFragment;
import com.f0x1d.dogbin.ui.fragment.folders.FoldersWrapperFragment;
import com.f0x1d.dogbin.ui.fragment.settings.SettingsFragment;
import com.f0x1d.dogbin.ui.fragment.settings.SettingsWrapperFragment;

public class MyFragmentBuilder implements FragmentNavigator.FragmentBuilder {
    @Override
    public Fragment getFragment(String tag) {
        switch (tag) {
            case "settings":
                return new SettingsWrapperFragment();
            case "settings_pref":
                return new SettingsFragment();
            case "folders":
                return new FoldersWrapperFragment();
            case "folders_wrapped":
                return new FoldersFragment();

            default:
                return null;
        }
    }
}
