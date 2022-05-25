package com.f0x1d.dogbin.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.f0x1d.dogbin.db.entity.DogbinSavedNote;
import com.f0x1d.dogbin.utils.Utils;

import java.util.List;

@Dao
public interface DogbinSavedNoteDao {

    @Query("SELECT * FROM DogbinSavedNote")
    LiveData<List<DogbinSavedNote>> getAll();

    @Query("SELECT * FROM DogbinSavedNote")
    List<DogbinSavedNote> getAllSync();

    @Query("SELECT * FROM DogbinSavedNote WHERE slug = :slug")
    LiveData<DogbinSavedNote> getBySlug(String slug);

    @Query("SELECT * FROM DogbinSavedNote WHERE slug = :slug")
    DogbinSavedNote getBySlugSync(String slug);

    @Query("UPDATE DogbinSavedNote SET content = :content, time = :time WHERE slug = :slug")
    void updateContentBySlug(String slug, String content, String time);

    @Transaction
    default void addToCache(DogbinSavedNote savedNote) {
        DogbinSavedNote maybeSavedNote = getBySlugSync(savedNote.getSlug());
        if (maybeSavedNote == null)
            insert(savedNote);
        else if (!maybeSavedNote.getContent().equals(savedNote.getContent()))
            updateContentBySlug(savedNote.getSlug(), savedNote.getContent(), Utils.currentTimeToString());
    }

    @Insert
    void insert(DogbinSavedNote savedNote);

    @Query("DELETE FROM DogbinSavedNote WHERE slug = :slug")
    void delete(String slug);

    @Query("DELETE FROM DogbinSavedNote")
    void nukeTable();
}
