package com.f0x1d.dogbin.network.model;

import com.google.gson.annotations.SerializedName;

public class DocumentInfoResponse {

    @SerializedName("key")
    private String mKey;

    @SerializedName("data")
    private String mContent;

    @SerializedName("isEditable")
    private boolean mIsEditable;

    @SerializedName("type")
    private String mType;

    @SerializedName("version")
    private int mVersion;

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getKey() {
        return mKey;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setEditable(boolean editable) {
        mIsEditable = editable;
    }

    public boolean isEditable() {
        return mIsEditable;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getType() {
        return mType;
    }

    public void setVersion(int version) {
        this.mVersion = version;
    }

    public int getVersion() {
        return mVersion;
    }
}
