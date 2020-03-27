package com.f0x1d.dogbin.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyNote {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "slug")
    private String mSlug;
    @ColumnInfo(name = "time")
    private String mTime;

    public static MyNote createNote(String slug, String time) {
        MyNote myNote = new MyNote();
        myNote.setSlug(slug);
        myNote.setTime(time);
        return myNote;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
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
