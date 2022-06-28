package com.f0x1d.dogbin.utils;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.f0x1d.dogbin.R;

public class ViewUtils {

    public static void setupSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout, boolean nightMode) {
        swipeRefreshLayout.setColorSchemeColors(ResourcesUtils.getColorFromAttr(swipeRefreshLayout.getContext(), R.attr.colorPrimary));
        if (nightMode)
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ResourcesUtils.getColorFromAttr(swipeRefreshLayout.getContext(), android.R.attr.windowBackground));
    }
}
