package com.f0x1d.dogbin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.f0x1d.dogbin.db.dao.MyNoteDao;
import com.f0x1d.dogbin.db.entity.MyNote;

@Database(entities = {MyNote.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {

    public abstract MyNoteDao myNoteDao();
}
