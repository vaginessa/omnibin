package com.f0x1d.dogbin.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.f0x1d.dogbin.db.entity.FoxBinSavedNote;
import com.f0x1d.dogbin.utils.Utils;

import java.util.List;

@Dao
public interface FoxBinSavedNoteDao {

    @Query("SELECT * FROM FoxBinSavedNote")
    LiveData<List<FoxBinSavedNote>> getAll();

    @Query("SELECT * FROM FoxBinSavedNote")
    List<FoxBinSavedNote> getAllSync();

    @Query("SELECT * FROM FoxBinSavedNote WHERE slug = :slug")
    LiveData<FoxBinSavedNote> getBySlug(String slug);

    @Query("SELECT * FROM FoxBinSavedNote WHERE slug = :slug")
    FoxBinSavedNote getBySlugSync(String slug);

    @Query("UPDATE FoxBinSavedNote SET content = :content, time = :time WHERE slug = :slug")
    void updateContentBySlug(String slug, String content, String time);

    @Transaction
    default void addToCache(FoxBinSavedNote savedNote) {
        FoxBinSavedNote maybeSavedNote = getBySlugSync(savedNote.getSlug());
        if (maybeSavedNote == null)
            insert(savedNote);
        else if (!maybeSavedNote.getContent().equals(savedNote.getContent()))
            updateContentBySlug(savedNote.getSlug(), savedNote.getContent(), Utils.currentTimeToString());
    }

    @Insert
    void insert(FoxBinSavedNote savedNote);

    @Query("DELETE FROM FoxBinSavedNote WHERE slug = :slug")
    void delete(String slug);

    @Query("DELETE FROM FoxBinSavedNote")
    void nukeTable();
}
