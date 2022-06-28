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

public class SettingsWrapperFragment extends BaseFragment<AndroidViewModel> {

    private FragmentNavigator mFragmentNavigator;

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

        mFragmentNavigator = new FragmentNavigator(getChildFragmentManager(), R.id.wrapper_container, new MyFragmentBuilder());
        if (savedInstanceState == null)
            mFragmentNavigator.switchTo("settings_pref");
    }
}
