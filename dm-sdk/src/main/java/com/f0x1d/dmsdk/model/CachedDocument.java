package com.f0x1d.dmsdk.model;

import androidx.annotation.Keep;

@Keep
public class CachedDocument {

    private long mId;
    private String mContent;
    private String mSlug;
    private String mTime;

    public static CachedDocument createDocument(String content, String slug, String time) {
        CachedDocument cachedDocument = new CachedDocument();
        cachedDocument.setContent(content);
        cachedDocument.setSlug(slug);
        cachedDocument.setTime(time);
        return cachedDocument;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
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
                ", mContent='" + mContent + '\'' +
                ", mSlug='" + mSlug + '\'' +
                ", mTime='" + mTime + '\'' +
                '}';
    }
}
