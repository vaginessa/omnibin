package com.f0x1d.dogbin.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.f0x1d.dogbin.db.entity.PastebinSavedNote;
import com.f0x1d.dogbin.utils.TimeUtils;

import java.util.List;

@Dao
public interface PastebinSavedNoteDao {

    @Query("SELECT * FROM PastebinSavedNote")
    LiveData<List<PastebinSavedNote>> getAll();

    @Query("SELECT * FROM PastebinSavedNote")
    List<PastebinSavedNote> getAllSync();

    @Query("SELECT * FROM PastebinSavedNote WHERE slug = :slug")
    LiveData<PastebinSavedNote> getBySlug(String slug);

    @Query("SELECT * FROM PastebinSavedNote WHERE slug = :slug")
    PastebinSavedNote getBySlugSync(String slug);

    @Query("UPDATE PastebinSavedNote SET content = :content, time = :time WHERE slug = :slug")
    void updateContentBySlug(String slug, String content, String time);

    @Transaction
    default void addToCache(PastebinSavedNote savedNote) {
        PastebinSavedNote maybeSavedNote = getBySlugSync(savedNote.getSlug());
        if (maybeSavedNote == null)
            insert(savedNote);
        else if (!maybeSavedNote.getContent().equals(savedNote.getContent()))
            updateContentBySlug(savedNote.getSlug(), savedNote.getContent(), TimeUtils.currentTimeToString());
    }

    @Insert
    void insert(PastebinSavedNote savedNote);

    @Query("DELETE FROM PastebinSavedNote WHERE slug = :slug")
    void delete(String slug);

    @Query("DELETE FROM PastebinSavedNote")
    void nukeTable();
}
