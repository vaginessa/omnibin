package com.f0x1d.dogbin.utils.services;

import android.content.pm.ApplicationInfo;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.utils.ThreadingUtils;

import java.util.List;

public class ServicesUtils {

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

    public static void switchService(int which, List<ApplicationInfo> services) {
        switch (which) {
            case 0:
                App.getPreferencesUtil().setSelectedService(BinServiceUtils.FOXBIN_SERVICE);
                break;
            case 1:
                App.getPreferencesUtil().setSelectedService(BinServiceUtils.PASTEBIN_SERVICE);
                break;
            default:
                App.getPreferencesUtil().setSelectedService(services.get(which - BinServiceUtils.IMPLEMENTED_SERVICES.length).packageName);
        }

        ThreadingUtils.getExecutor().execute(BinServiceUtils::refreshCurrentService);
    }
}
