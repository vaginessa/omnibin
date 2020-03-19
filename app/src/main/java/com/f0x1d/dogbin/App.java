package com.f0x1d.dogbin;

import android.app.Application;

import androidx.room.Room;

import com.f0x1d.dogbin.db.MyDatabase;
import com.f0x1d.dogbin.utils.PreferencesUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class App extends Application {

    private static App sInstance;
    private static PreferencesUtils sPrefsUtil;

    private static MyDatabase sMyDatabase;

    private FirebaseAnalytics mFirebaseAnalytics;


    public static App getInstance() {
        return sInstance;
    }

    public static PreferencesUtils getPrefsUtil() {
        return sPrefsUtil;
    }

    public static MyDatabase getMyDatabase() {
        return sMyDatabase;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        sPrefsUtil = new PreferencesUtils(this);
        sMyDatabase = Room.databaseBuilder(this, MyDatabase.class, "dogbin_database").build();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
