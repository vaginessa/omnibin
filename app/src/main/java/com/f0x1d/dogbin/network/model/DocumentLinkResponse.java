package com.f0x1d.dogbin.network.model;

import com.squareup.moshi.Json;

public class DocumentLinkResponse {

    @Json(name = "isUrl")
    private boolean mIsUrl;

    // Slug is a note's url
    @Json(name = "key")
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
