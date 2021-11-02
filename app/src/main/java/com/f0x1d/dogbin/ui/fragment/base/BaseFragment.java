package com.f0x1d.dogbin.ui.fragment.base;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseFragment<T extends AndroidViewModel> extends Fragment {

    protected T mViewModel;

    @LayoutRes
    protected abstract int layoutId();
    protected abstract Class<T> viewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Class<T> viewModelClass = viewModel();
        if (viewModelClass != null) {
            ViewModelProvider.Factory factory = buildFactory();

            if (factory == null)
                mViewModel = new ViewModelProvider(this).get(viewModelClass);
            else
                mViewModel = new ViewModelProvider(this, factory).get(viewModelClass);
        }
    }

    protected <G> G findViewById(@IdRes int id) {
        return (G) getView().findViewById(id);
    }

    protected boolean isNightTheme() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        }
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    protected ViewModelProvider.Factory buildFactory() {
        return null;
    }
}
