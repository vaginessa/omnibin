package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.TypedValue;

import androidx.annotation.AttrRes;

import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.db.entity.DogbinSavedNote;
import com.f0x1d.dogbin.db.entity.PastebinSavedNote;
import com.google.gson.Gson;

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

    private static Handler sUiHandler;
    private static Executor sExecutor = Executors.newCachedThreadPool();
    private static Gson sGson = new Gson();

    public static Executor getExecutor() {
        return sExecutor;
    }

    public static void runOnUiThread(Runnable runnable) {
        sUiHandler.post(runnable);
    }

    public static void initOnUiThread() {
        sUiHandler = new Handler();
    }

    public static Gson getGson() {
        return sGson;
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

    public static List<UserDocument> toUserNotes(List<DogbinSavedNote> savedNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (DogbinSavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime()));
        }

        return userDocuments;
    }

    public static List<UserDocument> toUserNotesPastebin(List<PastebinSavedNote> savedNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (PastebinSavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime()));
        }

        return userDocuments;
    }

    public static String[] getInstalledServices(List<ApplicationInfo> applicationInfos) {
        int implementedServicesCount = BinServiceUtils.IMPLEMENTED_SERVICES.length;

        String[] applicationsArray = new String[applicationInfos.size() + implementedServicesCount];
        System.arraycopy(BinServiceUtils.IMPLEMENTED_SERVICES, 0, applicationsArray, 0, implementedServicesCount);
        for (int i = 0; i < applicationInfos.size(); i++) {
            applicationsArray[i + implementedServicesCount] =
                    String.valueOf(App.getInstance().getPackageManager().getApplicationLabel(applicationInfos.get(i))).replace(BinServiceUtils.START_TAG, "");
        }

        return applicationsArray;
    }

    public static int getSelectedService(List<ApplicationInfo> applicationInfos) {
        String selectedPackageName = App.getPreferencesUtil().getSelectedService();

        switch (selectedPackageName) {
            case BinServiceUtils.DOGBIN_SERVICE:
                return 0;
            case BinServiceUtils.PASTEBIN_SERVICE:
                return 1;
        }

        for (int i = 0; i < applicationInfos.size(); i++) {
            if (applicationInfos.get(i).packageName.equals(selectedPackageName))
                return i + BinServiceUtils.IMPLEMENTED_SERVICES.length;
        }
        return 0;
    }
}
