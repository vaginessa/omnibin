package com.f0x1d.dogbin.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.f0x1d.dogbin.db.entity.SavedNote;
import com.f0x1d.dogbin.utils.Utils;

import java.util.List;

@Dao
public interface SavedNoteDao {

    @Query("SELECT * FROM SavedNote")
    LiveData<List<SavedNote>> getAll();

    @Query("SELECT * FROM SavedNote")
    List<SavedNote> getAllSync();

    @Query("SELECT * FROM SavedNote WHERE slug = :slug")
    LiveData<SavedNote> getBySlug(String slug);

    @Query("SELECT * FROM SavedNote WHERE slug = :slug")
    SavedNote getBySlugSync(String slug);

    @Query("UPDATE SavedNote SET content = :content, time = :time WHERE slug = :slug")
    void updateContentBySlug(String slug, String content, String time);

    @Transaction
    default void addToCache(SavedNote savedNote) {
        SavedNote maybeSavedNote = getBySlugSync(savedNote.getSlug());
        if (maybeSavedNote == null)
            insert(savedNote);
        else if (!maybeSavedNote.getContent().equals(savedNote.getContent()))
            updateContentBySlug(savedNote.getSlug(), savedNote.getContent(), Utils.currentTimeToString());
    }

    @Insert
    void insert(SavedNote savedNote);

    @Query("DELETE FROM SavedNote WHERE slug = :slug")
    void delete(String slug);

    @Query("DELETE FROM SavedNote")
    void nukeTable();
}
