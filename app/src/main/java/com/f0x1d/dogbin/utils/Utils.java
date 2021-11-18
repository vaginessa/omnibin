package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AttrRes;

import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.DogbinSavedNote;
import com.f0x1d.dogbin.db.entity.FoxBinSavedNote;
import com.f0x1d.dogbin.db.entity.PastebinSavedNote;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Utils {

    private static Handler sUiHandler;
    private static final Executor sExecutor = Executors.newCachedThreadPool();
    private static final Gson sGson = new Gson();

    public static Executor getExecutor() {
        return sExecutor;
    }

    public static void runOnUiThread(Runnable runnable) {
        sUiHandler.post(runnable);
    }

    public static void initOnUiThread() {
        sUiHandler = new Handler();
    }

    public static int statusBarHeight() {
        int result = 0;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void applyToolbarTitleAndMargins(View view, String title) {
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);

        MaterialToolbar materialToolbar = view.findViewById(R.id.toolbar);
        ((ViewGroup.MarginLayoutParams) materialToolbar.getLayoutParams()).topMargin = Utils.statusBarHeight();
    }

    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static Gson getGson() {
        return sGson;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
        /*Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));*/

        return new Date(System.currentTimeMillis()).toLocaleString();
    }

    public static List<UserDocument> toUserNotes(List<DogbinSavedNote> savedNotes, boolean myNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (DogbinSavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime(), myNotes));
        }

        return userDocuments;
    }

    public static List<UserDocument> toUserNotesPastebin(List<PastebinSavedNote> savedNotes, boolean myNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (PastebinSavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime(), myNotes));
        }

        return userDocuments;
    }

    public static List<UserDocument> toUserNotesFoxBin(List<FoxBinSavedNote> savedNotes, boolean myNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (FoxBinSavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime(), myNotes));
        }

        return userDocuments;
    }

    public static String[] getInstalledServices(List<ApplicationInfo> applicationInfos) {
        int implementedServicesCount = BinServiceUtils.IMPLEMENTED_SERVICES.length;

        String[] applicationsArray = new String[applicationInfos.size() + implementedServicesCount];
        System.arraycopy(BinServiceUtils.IMPLEMENTED_SERVICES, 0, applicationsArray, 0, implementedServicesCount);
        for (int i = 0; i < applicationInfos.size(); i++) {
            applicationsArray[i + implementedServicesCount] =
                    String.valueOf(App.getInstance().getPackageManager().getApplicationLabel(applicationInfos.get(i)));
        }

        return applicationsArray;
    }

    public static int getSelectedService(List<ApplicationInfo> applicationInfos) {
        String selectedPackageName = App.getPreferencesUtil().getSelectedService();

        switch (selectedPackageName) {
            case BinServiceUtils.FOXBIN_SERVICE:
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
