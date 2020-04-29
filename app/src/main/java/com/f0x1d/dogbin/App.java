package com.f0x1d.dogbin;

import android.app.Application;

import androidx.room.Room;

import com.f0x1d.dogbin.db.MyDatabase;
import com.f0x1d.dogbin.db.dao.SavedNoteDao;
import com.f0x1d.dogbin.db.entity.SavedNote;
import com.f0x1d.dogbin.utils.PreferencesUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

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

        Utils.initOnUiThread();

        sPrefsUtil = new PreferencesUtils(this);
        sMyDatabase = Room.databaseBuilder(this, MyDatabase.class, "dogbin_database")
                .addMigrations(
                        MyDatabase.MIGRATION_1_2,
                        MyDatabase.MIGRATION_2_3
                )
                .build();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Utils.getExecutor().execute(this::clearCacheIfNeeded);
    }

    private void clearCacheIfNeeded() {
        if (!getPrefsUtil().autoClearCache())
            return;

        SavedNoteDao savedNoteDao = getMyDatabase().getSavedNoteDao();

        List<SavedNote> savedNotes = savedNoteDao.getAllSync();
        if (savedNotes.size() < 100)
            return;

        int indexesToDelete = savedNotes.size() - 100;
        for (int i = 0; i < indexesToDelete; i++) {
            savedNoteDao.delete(savedNotes.get(i).getSlug());
        }
    }
}
