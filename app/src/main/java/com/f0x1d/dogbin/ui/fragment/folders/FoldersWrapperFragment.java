package com.f0x1d.dogbin.ui.fragment.folders;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.fragment.base.BaseFragment;
import com.f0x1d.dogbin.utils.fragments.FragmentNavigator;
import com.f0x1d.dogbin.utils.fragments.MyFragmentBuilder;

public class FoldersWrapperFragment extends BaseFragment<AndroidViewModel> {

    private FragmentNavigator mFragmentNavigator;

    public static FoldersWrapperFragment newInstance() {
        Bundle args = new Bundle();

        FoldersWrapperFragment fragment = new FoldersWrapperFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_folders_wrapper;
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
            mFragmentNavigator.switchTo("folders_wrapped");
    }

    public boolean onBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() != 0) {
            getChildFragmentManager().popBackStack();
            return false;
        }
        return true;
    }

    public FragmentNavigator getFragmentNavigator() {
        return mFragmentNavigator;
    }
}
