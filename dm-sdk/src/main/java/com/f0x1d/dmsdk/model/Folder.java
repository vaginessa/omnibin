package com.f0x1d.dmsdk.model;

import android.graphics.drawable.Drawable;
import androidx.annotation.Keep;

@Keep
public class Folder {

    private String mTitle;
    private Drawable mIcon;

    private String mKey;
    private boolean mAvailableUnauthorized;

    public static Folder create(String title, Drawable icon, String key, boolean availableUnauthorized) {
        Folder folder = new Folder();
        folder.setTitle(title);
        folder.setIcon(icon);
        folder.setKey(key);
        folder.setAvailableUnauthorized(availableUnauthorized);
        return folder;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public boolean isAvailableUnauthorized() {
        return mAvailableUnauthorized;
    }

    public void setAvailableUnauthorized(boolean availableUnauthorized) {
        this.mAvailableUnauthorized = availableUnauthorized;
    }
}
