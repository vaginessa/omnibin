package com.f0x1d.dogbin.ui.activity.base;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;

public abstract class BaseActivity<T extends AndroidViewModel> extends AppCompatActivity {

    protected T mViewModel;

    protected abstract Class<T> viewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(App.getPreferencesUtil().isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }

        switch (App.getPreferencesUtil().selectedAccent()) {
            default:
            case 0:
                setTheme(R.style.AppTheme);
                break;
            case 1:
                //setTheme(R.style.AppTheme_Pink);
                break;
            case 2:
                //setTheme(R.style.AppTheme_Lime);
                break;
            case 3:
                //setTheme(R.style.AppTheme_Gold);
                break;
        }

        setupNavBarAndStatusBar();
        super.onCreate(savedInstanceState);

        Class<T> viewModelClass = viewModel();
        if (viewModelClass != null) {
            ViewModelProvider.Factory factory = buildFactory();
            if (factory == null)
                mViewModel = new ViewModelProvider(this).get(viewModelClass);
            else
                mViewModel = new ViewModelProvider(this, factory).get(viewModelClass);
        }
    }

    protected ViewModelProvider.Factory buildFactory() {
        return null;
    }

    private void setupNavBarAndStatusBar() {
        boolean lightTheme = !App.getPreferencesUtil().isDarkTheme();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && lightTheme) {
            getWindow().setStatusBarColor(Color.GRAY);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1 && lightTheme) {
            getWindow().setNavigationBarColor(Color.BLACK);
        }
    }

    public boolean isNightTheme() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        }
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
