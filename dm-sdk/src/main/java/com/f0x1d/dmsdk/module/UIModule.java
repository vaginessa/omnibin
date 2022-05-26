package com.f0x1d.dmsdk.module;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Keep;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.base.BaseModule;

@Keep
public abstract class UIModule extends BaseModule {

    public UIModule(BinService binService) {
        super(binService);
    }

    /**
     * builds a view for a dialog after publish button is pressed
     * @param editingMode
     * @param theme
     * @return view for a dialog, null to skip dialog
     */
    public View buildSettingsDialog(boolean editingMode, Resources.Theme theme) {
        return null;
    }

    /**
     * collect data that you need from your dialog view
     * @param view
     * @param editingMode
     * @return bundle with data you need, it will be passed in create/editDocument method
     */
    public Bundle collectDataFromDialog(View view, boolean editingMode) {
        return null;
    }
}
