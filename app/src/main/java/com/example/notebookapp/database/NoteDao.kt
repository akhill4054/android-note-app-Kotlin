package com.example.notebookapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * from notebook_table WHERE noteId = :key")
    suspend fun get(key: Long): Note

    @Query("DELETE FROM notebook_table")
    suspend fun clear()

    @Query("SELECT * FROM notebook_table ORDER BY noteId ASC")
    fun allNotes(): LiveData<List<Note>>

}