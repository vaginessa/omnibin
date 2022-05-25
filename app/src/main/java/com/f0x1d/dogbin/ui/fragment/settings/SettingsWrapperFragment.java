package com.f0x1d.dogbin.ui.fragment.settings;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.fragment.base.BaseFragment;
import com.f0x1d.dogbin.utils.fragments.FragmentNavigator;
import com.f0x1d.dogbin.utils.fragments.MyFragmentBuilder;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsWrapperFragment extends BaseFragment<AndroidViewModel> {

    private MaterialToolbar mToolbar;

    private FragmentNavigator mFragmentNavigator;

    public static SettingsWrapperFragment newInstance() {
        Bundle args = new Bundle();

        SettingsWrapperFragment fragment = new SettingsWrapperFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_settings_wrapper;
    }

    @Override
    protected Class<AndroidViewModel> viewModel() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.settings);

        mFragmentNavigator = new FragmentNavigator(getChildFragmentManager(), R.id.wrapper_container, new MyFragmentBuilder());
        if (savedInstanceState == null)
            mFragmentNavigator.switchTo("settings_pref");
    }
}
