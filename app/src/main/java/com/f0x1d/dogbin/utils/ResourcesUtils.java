package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import androidx.annotation.AttrRes;

public class ResourcesUtils {

    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static int getColorFromAttr(Context c, @AttrRes int attrId) {
        TypedValue typedValue = new TypedValue();
        c.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }
}
