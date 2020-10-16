package com.f0x1d.dogbin.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.f0x1d.dogbin.db.dao.DogbinSavedNoteDao;
import com.f0x1d.dogbin.db.dao.PastebinSavedNoteDao;
import com.f0x1d.dogbin.db.entity.DogbinSavedNote;
import com.f0x1d.dogbin.db.entity.PastebinSavedNote;

@Database(entities = {DogbinSavedNote.class, PastebinSavedNote.class}, version = 4)
public abstract class MyDatabase extends RoomDatabase {

    public static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE SavedNote(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, content TEXT, slug TEXT, time TEXT)");
            database.execSQL("CREATE INDEX index_SavedNote_slug ON SavedNote(slug)");
        }
    };

    public static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS MyNote");
        }
    };

    public static Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE PastebinSavedNote(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, content TEXT, slug TEXT, time TEXT)");
            database.execSQL("CREATE INDEX index_PastebinSavedNote_slug ON PastebinSavedNote(slug)");

            database.execSQL("ALTER TABLE SavedNote RENAME TO DogbinSavedNote");
            database.execSQL("DROP INDEX index_SavedNote_slug");
            database.execSQL("CREATE INDEX index_DogbinSavedNote_slug ON DogbinSavedNote(slug)");
        }
    };

    public abstract DogbinSavedNoteDao getDogbinSavedNoteDao();
    public abstract PastebinSavedNoteDao getPastebinSavedNoteDao();
}
