package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.TypedValue;

import androidx.annotation.AttrRes;

import com.f0x1d.dmsdk.model.CachedDocument;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.db.entity.SavedNote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Utils {

    private static Handler mUiHandler;
    private static Executor mExecutor = Executors.newCachedThreadPool();

    public static Executor getExecutor() {
        return mExecutor;
    }

    public static void runOnUiThread(Runnable runnable) {
        mUiHandler.post(runnable);
    }

    public static void initOnUiThread() {
        mUiHandler = new Handler();
    }

    public static boolean getBooleanFromAttr(Context c, @AttrRes int attrId) {
        TypedArray typedArray = c.obtainStyledAttributes(new int[]{attrId});
        boolean result = typedArray.getBoolean(0, false);
        typedArray.recycle();
        return result;
    }

    public static int getColorFromAttr(Context c, @AttrRes int attrId) {
        TypedValue typedValue = new TypedValue();
        c.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }

    public static String currentTimeToString() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        return formatter.format(date);
    }

    public static List<SavedNote> toSavedNotes(List<UserDocument> userDocuments) {
        List<SavedNote> savedNotes = new ArrayList<>();

        for (UserDocument userDocument : userDocuments) {
            savedNotes.add(SavedNote.createNote("", userDocument.getSlug(), userDocument.getTime()));
        }

        return savedNotes;
    }

    public static List<UserDocument> toUserNotes(List<SavedNote> savedNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (SavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime()));
        }

        return userDocuments;
    }

    public static List<CachedDocument> toCachedNotes(List<SavedNote> savedNotes) {
        List<CachedDocument> cachedDocuments = new ArrayList<>();

        for (SavedNote savedNote : savedNotes) {
            cachedDocuments.add(CachedDocument.createDocument(savedNote.getContent(), savedNote.getSlug(), savedNote.getTime()));
        }

        return cachedDocuments;
    }

    public static List<SavedNote> toCachedSavedNotes(List<CachedDocument> cachedDocuments) {
        List<SavedNote> savedNotes = new ArrayList<>();

        for (CachedDocument cachedDocument : cachedDocuments) {
            savedNotes.add(SavedNote.createNote(cachedDocument.getContent(), cachedDocument.getSlug(), cachedDocument.getTime()));
        }

        return savedNotes;
    }

    public static String[] getInstalledServices(List<ApplicationInfo> applicationInfos) {
        String[] applicationsArray = new String[applicationInfos.size() + 1];
        applicationsArray[0] = "dogbin";
        for (int i = 0; i < applicationInfos.size(); i++) {
            applicationsArray[i + 1] = String.valueOf(App.getInstance().getPackageManager().getApplicationLabel(applicationInfos.get(i)))
                    .replace(BinServiceUtils.START_TAG, "");
        }
        return applicationsArray;
    }

    public static int getSelectedService(List<ApplicationInfo> applicationInfos) {
        String selectedPackageName = App.getPreferencesUtil().getSelectedService();
        for (int i = 0; i < applicationInfos.size(); i++) {
            if (applicationInfos.get(i).packageName.equals(selectedPackageName))
                return i + 1;
        }
        return 0;
    }
}
