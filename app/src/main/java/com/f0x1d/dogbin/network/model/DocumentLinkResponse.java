package com.f0x1d.dogbin.network.model;

import com.google.gson.annotations.SerializedName;

public class DocumentLinkResponse {

    @SerializedName("isUrl")
    private boolean mIsUrl;

    // Slug is a note's url
    @SerializedName("key")
    private String mSlug;


    public boolean isUrl() {
        return mIsUrl;
    }

    public void setUrl(boolean url) {
        mIsUrl = url;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        this.mSlug = slug;
    }
}
