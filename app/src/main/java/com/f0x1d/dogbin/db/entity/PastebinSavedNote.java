package com.f0x1d.dogbin.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PastebinSavedNote {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "content")
    private String mContent;
    @ColumnInfo(name = "slug", index = true)
    private String mSlug;
    @ColumnInfo(name = "time")
    private String mTime;

    public static PastebinSavedNote createNote(String content, String slug, String time) {
        PastebinSavedNote savedNote = new PastebinSavedNote();
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
}
