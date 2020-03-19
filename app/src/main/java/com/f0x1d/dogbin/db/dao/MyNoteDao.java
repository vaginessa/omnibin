package com.f0x1d.dogbin.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.f0x1d.dogbin.db.entity.MyNote;

import java.util.List;

@Dao
public interface MyNoteDao {

    @Query("SELECT * FROM MyNote")
    LiveData<List<MyNote>> getAll();

    @Insert
    void insert(MyNote myNote);
}
