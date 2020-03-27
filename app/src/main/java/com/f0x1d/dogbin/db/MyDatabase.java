package com.f0x1d.dogbin.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.f0x1d.dogbin.db.dao.MyNoteDao;
import com.f0x1d.dogbin.db.dao.SavedNoteDao;
import com.f0x1d.dogbin.db.entity.MyNote;
import com.f0x1d.dogbin.db.entity.SavedNote;

@Database(entities = {MyNote.class, SavedNote.class}, version = 2)
public abstract class MyDatabase extends RoomDatabase {

    public static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE SavedNote(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, content TEXT, slug TEXT, time TEXT)");
            database.execSQL("CREATE INDEX index_SavedNote_slug ON SavedNote(slug)");
        }
    };

    public abstract MyNoteDao getMyNoteDao();

    public abstract SavedNoteDao getSavedNoteDao();
}
