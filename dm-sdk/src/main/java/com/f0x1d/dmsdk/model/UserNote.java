package com.f0x1d.dmsdk.model;

import androidx.annotation.Keep;

@Keep
public class UserNote {

    private long mId;
    private String mSlug;
    private String mTime;

    public static UserNote createNote(String slug, String time) {
        UserNote userNote = new UserNote();
        userNote.setSlug(slug);
        userNote.setTime(time);
        return userNote;
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
        return "SavedNote{" +
                "mId=" + mId +
                ", mSlug='" + mSlug + '\'' +
                ", mTime='" + mTime + '\'' +
                '}';
    }
}
