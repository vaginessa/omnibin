package com.f0x1d.dogbin.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SavedNote {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "content")
    private String mContent;
    @ColumnInfo(name = "slug", index = true)
    private String mSlug;
    @ColumnInfo(name = "time")
    private String mTime;

    public static SavedNote createNote(String content, String slug, String time) {
        SavedNote savedNote = new SavedNote();
        savedNote.setContent(content);
        savedNote.setSlug(slug);
        savedNote.setTime(time);
        return savedNote;
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
