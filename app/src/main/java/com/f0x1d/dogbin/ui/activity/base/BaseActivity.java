package com.f0x1d.dogbin.ui.activity.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.f0x1d.dogbin.App;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(App.getPrefsUtil().isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
        setupNavBarAndStatusBar();
        super.onCreate(savedInstanceState);
    }

    private void setupNavBarAndStatusBar() {
        boolean lightTheme = !App.getPrefsUtil().isDarkTheme();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && lightTheme) {
            getWindow().setStatusBarColor(Color.GRAY);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1 && lightTheme) {
            getWindow().setNavigationBarColor(Color.BLACK);
        }
    }
}
