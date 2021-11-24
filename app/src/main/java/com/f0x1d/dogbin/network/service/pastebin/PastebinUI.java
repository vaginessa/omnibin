package com.f0x1d.dogbin.network.service.pastebin;

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

public class PastebinUI extends UIModule {

    public PastebinUI(BinService binService) {
        super(binService);
    }

    @Override
    public View buildSettingsDialog(boolean editingMode, Resources.Theme theme) {
        return editingMode ? null : LayoutInflater.from(new ContextThemeWrapper(App.getInstance(), theme))
                .inflate(R.layout.dialog_pastebin_settings, null, false);
    }

    @Override
    public Bundle collectDataFromDialog(View view, boolean editingMode) {
        if (editingMode) return null;

        Spinner spinner = view.findViewById(R.id.values_spinner);
        int selectedValue = spinner.getSelectedItemPosition();

        String expiration = "N";
        switch (selectedValue) {  // kotlin supremacy x2
            case 1:
                expiration = "10M";
                break;
            case 2:
                expiration = "1H";
                break;
            case 3:
                expiration = "1D";
                break;
            case 4:
                expiration = "1W";
                break;
            case 5:
                expiration = "2W";
                break;
            case 6:
                expiration = "1M";
                break;
            case 7:
                expiration = "6M";
                break;
            case 8:
                expiration = "1Y";
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("expiration", expiration);
        return bundle;
    }
}
