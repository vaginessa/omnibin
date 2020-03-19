package com.f0x1d.dogbin.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyNote {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String slug;
    public String time;
}
