package com.example.mykeep;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;

@Dao
public interface NoteDao {
    @Insert
    void insert (Note note);

    @Query("DELETE FROM note_table")
    void deleteAll();

    @Query("SELECT *from note_table ORDER BY title ASC")
    LiveData<ArrayList<Note>> getAllNotes();
}
