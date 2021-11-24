package com.f0x1d.dogbin.network.service.foxbin;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.view.ContextThemeWrapper;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.UIModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;

public class FoxBinUI extends UIModule {

    public FoxBinUI(BinService binService) {
        super(binService);
    }

    @Override
    public View buildSettingsDialog(boolean editingMode, Resources.Theme theme) {
        return editingMode ? null : LayoutInflater.from(new ContextThemeWrapper(App.getInstance(), theme))
                .inflate(R.layout.dialog_foxbin_settings, null, false);
    }

    @Override
    public Bundle collectDataFromDialog(View view, boolean editingMode) {
        if (editingMode) return null;

        Spinner spinner = view.findViewById(R.id.values_spinner);
        int selectedValue = spinner.getSelectedItemPosition();

        long expiration = 0;
        long oneMinute = 1000 * 60;
        long oneHour = oneMinute * 60;
        long oneDay = oneHour * 24;
        switch (selectedValue) { // kotlin supremacy
            case 1:
                expiration = oneMinute * 10;
                break;
            case 2:
                expiration = oneMinute * 30;
                break;
            case 3:
                expiration = oneHour;
                break;
            case 4:
                expiration = oneHour * 3;
                break;
            case 5:
                expiration = oneHour * 12;
                break;
            case 6:
                expiration = oneDay;
                break;
            case 7:
                expiration = oneDay * 7;
                break;
            case 8:
                expiration = oneDay * 30;
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putLong("delete_after", expiration);
        return bundle;
    }
}
