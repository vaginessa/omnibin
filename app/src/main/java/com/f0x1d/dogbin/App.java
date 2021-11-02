package com.f0x1d.dogbin;

import android.app.Application;

import androidx.room.Room;

import com.f0x1d.dogbin.db.MyDatabase;
import com.f0x1d.dogbin.utils.PreferencesUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class App extends Application {

    private static App sInstance;
    private static PreferencesUtils sPrefsUtil;
    private static MyDatabase sMyDatabase;

    private FirebaseAnalytics mFirebaseAnalytics;


    public static App getInstance() {
        return sInstance;
    }

    public static PreferencesUtils getPreferencesUtil() {
        return sPrefsUtil;
    }

    public static MyDatabase getMyDatabase() {
        return sMyDatabase;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //DynamicColors.applyToActivitiesIfAvailable(this); // it doesnt work anyway

        sInstance = this;

        Utils.initOnUiThread();

        sPrefsUtil = new PreferencesUtils(this);
        sMyDatabase = Room.databaseBuilder(this, MyDatabase.class, "dogbin_database")
                .addMigrations(
                        MyDatabase.MIGRATION_1_2,
                        MyDatabase.MIGRATION_2_3,
                        MyDatabase.MIGRATION_3_4,
                        MyDatabase.MIGRATION_4_5
                )
                .build();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
