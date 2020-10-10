package com.f0x1d.dogbin.network.model;

import com.google.gson.annotations.SerializedName;

public class DocumentResponse {

    @SerializedName("slug")
    private String mSlug;

    @SerializedName("created")
    private String mCreatedTime;

    @SerializedName("link")
    private String mLink;

    @SerializedName("type")
    private String mType;

    public void setSlug(String slug) {
        this.mSlug = slug;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setCreatedTime(String createdTime) {
        this.mCreatedTime = createdTime;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public void setLink(String link) {
        this.mLink = link;
    }

    public String getLink() {
        return mLink;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getType() {
        return mType;
    }
}
