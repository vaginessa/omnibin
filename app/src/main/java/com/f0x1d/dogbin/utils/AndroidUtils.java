package com.f0x1d.dogbin.utils;

import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.f0x1d.dogbin.utils.backport.OldSupplier;

public class AndroidUtils {

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static <T extends ViewModel> ViewModelProvider.Factory buildViewModelFactory(OldSupplier<T> supplier) {
        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <R extends ViewModel> R create(@NonNull Class<R> modelClass) {
                return (R) supplier.get();
            }
        };
    }
}
