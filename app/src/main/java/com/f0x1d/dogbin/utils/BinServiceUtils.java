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
import com.f0x1d.dogbin.network.service.foxbin.FoxBinService;
import com.f0x1d.dogbin.network.service.pastebin.PasteBinService;
import dalvik.system.BaseDexClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BinServiceUtils {

    public static final String FOXBIN_SERVICE = "foxbin";
    public static final String PASTEBIN_SERVICE = "pastebin";

    public static String[] IMPLEMENTED_SERVICES = new String[]{FOXBIN_SERVICE, PASTEBIN_SERVICE};

    private static MutableLiveData<BinService> sInstanceData = new MutableLiveData<>();
    private static BinService sInstance;
    private static List<ApplicationInfo> sInstalledServices = Collections.emptyList();

    public static boolean loadingNeeded() {
        return sInstance == null;
    }

    public synchronized static void loadActiveServiceIfNeeded() {
        if (sInstance == null) {
            List<ApplicationInfo> installedServices = loadAndGetInstalledServices();

            String selectedService = App.getPreferencesUtil().getSelectedService();

            switch (selectedService) {
                case PASTEBIN_SERVICE:
                    sInstance = PasteBinService.getInstance();
                    break;

                default:
                case FOXBIN_SERVICE:
                    sInstance = FoxBinService.getInstance();
                    break;
            }

            try {
                for (ApplicationInfo installedService : installedServices) {
                    if (installedService.packageName.equals(selectedService)) {
                        sInstance = loadServiceFromApp(installedService.packageName);
                        break;
                    }
                }
            } catch (Exception e) {
                Utils.runOnUiThread(() -> Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.error, e.getLocalizedMessage()), Toast.LENGTH_SHORT).show());

                App.getPreferencesUtil().setSelectedService(FOXBIN_SERVICE);
                sInstance = FoxBinService.getInstance();
            }

            sInstanceData.postValue(sInstance);
        }
    }

    public synchronized static void loadService(String packageName) throws Exception {
        sInstance = loadServiceFromApp(packageName);
        sInstanceData.postValue(sInstance);
        App.getPreferencesUtil().setSelectedService(packageName);
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
        if (binService.getSDKVersion() != Constants.LATEST_VERSION) {
            Toast.makeText(App.getInstance(), R.string.module_v_old, Toast.LENGTH_SHORT).show();
            App.getPreferencesUtil().setSelectedService(FOXBIN_SERVICE);
            return FoxBinService.getInstance();
        }
        binService.init(App.getInstance().createPackageContext(packageName, 0), App.getInstance().getApplicationContext(),
                App.getInstance().getSharedPreferences(packageName + "_module", Context.MODE_PRIVATE));
        return binService;
    }

    private static List<ApplicationInfo> loadAndGetInstalledServices() {
        List<ApplicationInfo> installedPlugins = new ArrayList<>();
        for (ApplicationInfo installedApplication : App.getInstance().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)) {
            if ((installedApplication.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                continue;

            if (installedApplication.metaData != null && installedApplication.metaData.getString("binservice") != null)
                installedPlugins.add(installedApplication);
        }

        sInstalledServices = installedPlugins;
        return installedPlugins;
    }

    public static String getInbuiltServiceForUrl(String url) {
        if (url.contains("pastebin.com")) return PASTEBIN_SERVICE;
        else if (url.contains("f0x1d.com/foxbin")) return FOXBIN_SERVICE;

        else return FOXBIN_SERVICE;
    }

    public static void refreshInstalledServices() {
        Utils.getExecutor().execute(BinServiceUtils::loadAndGetInstalledServices);
    }

    public synchronized static void refreshCurrentService() {
        sInstance = null;
        loadActiveServiceIfNeeded();
    }

    public static LiveData<BinService> getInstanceData() {
        return sInstanceData;
    }

    public static List<ApplicationInfo> getInstalledServices() {
        return sInstalledServices;
    }
}
