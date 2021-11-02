package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.Constants;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.network.FoxBinService;
import com.f0x1d.dogbin.network.PasteBinService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;

public class BinServiceUtils {

    public static final String PASTEBIN_SERVICE = "pastebin";
    public static final String FOXBIN_SERVICE = "foxbin";

    public static String[] IMPLEMENTED_SERVICES = new String[]{PASTEBIN_SERVICE, FOXBIN_SERVICE};

    private static BinService sInstance;
    private static final MutableLiveData<List<ApplicationInfo>> sInstalledServicesData = new MutableLiveData<>();

    public static BinService getCurrentActiveService() {
        synchronized (BinServiceUtils.class) {
            if (sInstance == null) {
                List<ApplicationInfo> installedServices = getInstalledServices();

                String selectedService = App.getPreferencesUtil().getSelectedService();
                if (selectedService == null) {
                    selectedService = FOXBIN_SERVICE;
                    App.getPreferencesUtil().setSelectedService(selectedService);
                }

                switch (selectedService) {
                    case PASTEBIN_SERVICE:
                        return sInstance = PasteBinService.getInstance();
                    case FOXBIN_SERVICE:
                        return sInstance = FoxBinService.getInstance();
                }

                try {
                    for (ApplicationInfo installedService : installedServices) {
                        if (installedService.packageName.equals(selectedService)) {
                            return sInstance = loadServiceFromApp(installedService.packageName);
                        }
                    }
                } catch (Exception e) {
                    Utils.runOnUiThread(() ->
                            Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.error, e.getLocalizedMessage()), Toast.LENGTH_SHORT).show());
                }

                App.getPreferencesUtil().setSelectedService(FOXBIN_SERVICE);
                return sInstance = FoxBinService.getInstance();
            }
            return sInstance;
        }
    }

    public static BinService getBinServiceForPackageName(String packageName) {
        synchronized (BinServiceUtils.class) {
            try {
                sInstance = loadServiceFromApp(packageName);
                App.getPreferencesUtil().setSelectedService(packageName);
                return sInstance;
            } catch (Exception e) {
                return FoxBinService.getInstance();
            }
        }
    }

    private static BinService loadServiceFromApp(String packageName) throws Exception {
        ApplicationInfo applicationInfo = App.getInstance().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        if (applicationInfo.metaData == null || applicationInfo.metaData.getString("binservice") == null) {
            App.getPreferencesUtil().setSelectedService(FOXBIN_SERVICE);
            return FoxBinService.getInstance();
        }

        File outDir = new File("/data/data/" + App.getInstance().getPackageName() + "/files/" + applicationInfo.packageName);
        if (!outDir.exists())
            outDir.mkdirs();

        BaseDexClassLoader baseDexClassLoader = new BaseDexClassLoader(applicationInfo.sourceDir, outDir, null, App.getInstance().getClassLoader());
        BinService binService = (BinService) baseDexClassLoader.loadClass(applicationInfo.metaData.getString("binservice")).getConstructor().newInstance();
        if (binService.getSDKVersion() < Constants.LATEST_VERSION) {
            Toast.makeText(App.getInstance(), R.string.module_v_old, Toast.LENGTH_SHORT).show();
            App.getPreferencesUtil().setSelectedService(FOXBIN_SERVICE);
            return FoxBinService.getInstance();
        }
        binService.init(App.getInstance().createPackageContext(packageName, 0), App.getInstance().getApplicationContext(),
                App.getInstance().getSharedPreferences(packageName + "_module", Context.MODE_PRIVATE));
        return binService;
    }

    private static List<ApplicationInfo> getInstalledServices() {
        List<ApplicationInfo> installedPlugins = new ArrayList<>();
        for (ApplicationInfo installedApplication : App.getInstance().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)) {
            if ((installedApplication.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                continue;

            if (installedApplication.metaData != null && installedApplication.metaData.getString("binservice") != null)
                installedPlugins.add(installedApplication);
        }

        sInstalledServicesData.postValue(installedPlugins);

        return installedPlugins;
    }

    public static String getInbuiltServiceForUrl(String url) {
        if (url.contains("pastebin.com")) return PASTEBIN_SERVICE;
        else if (url.contains("f0x1d.com/foxbin")) return FOXBIN_SERVICE;

        else return FOXBIN_SERVICE;
    }

    public static void refreshInstalledServices() {
        Utils.getExecutor().execute(BinServiceUtils::getInstalledServices);
    }

    public static void refreshCurrentService() {
        sInstance = null;
    }

    public static LiveData<List<ApplicationInfo>> getInstalledServicesData() {
        return sInstalledServicesData;
    }
}
