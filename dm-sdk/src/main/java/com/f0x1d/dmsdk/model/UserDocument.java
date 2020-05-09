package com.f0x1d.dmsdk.model;

import androidx.annotation.Keep;

@Keep
public class UserDocument {

    private long mId;
    private String mSlug;
    private String mTime;

    public static UserDocument createDocument(String slug, String time) {
        UserDocument userDocument = new UserDocument();
        userDocument.setSlug(slug);
        userDocument.setTime(time);
        return userDocument;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        this.mSlug = slug;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    @Override
    public String toString() {
        return "SavedDocument{" +
                "mId=" + mId +
                ", mSlug='" + mSlug + '\'' +
                ", mTime='" + mTime + '\'' +
                '}';
    }
}
