package com.f0x1d.dmsdk.model;

import androidx.annotation.Keep;

@Keep
public class CachedNote {

    private long mId;
    private String mContent;
    private String mSlug;
    private String mTime;

    public static CachedNote createNote(String content, String slug, String time) {
        CachedNote cachedNote = new CachedNote();
        cachedNote.setContent(content);
        cachedNote.setSlug(slug);
        cachedNote.setTime(time);
        return cachedNote;
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
        return "SavedNote{" +
                "mId=" + mId +
                ", mContent='" + mContent + '\'' +
                ", mSlug='" + mSlug + '\'' +
                ", mTime='" + mTime + '\'' +
                '}';
    }
}
