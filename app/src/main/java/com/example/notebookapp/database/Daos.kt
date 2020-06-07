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

    @Query("SELECT * from notes_table WHERE noteId = :key")
    suspend fun get(key: Long): Note

    @Query("SELECT * FROM notes_table ORDER BY noteID DESC LIMIT 1")
    suspend fun recent(): Note

    @Query("DELETE FROM notes_table")
    suspend fun clear()

    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    fun allNotes(): LiveData<List<Note>>

}

@Dao
interface BinDao {

    @Insert
    suspend fun insert(binNote: BinNote)

    @Update
    suspend fun update(binNote: BinNote)

    @Delete
    suspend fun delete(binNote: BinNote)

    @Query("DELETE FROM bin_table")
    suspend fun clear()

    @Query("SELECT * FROM bin_table ORDER BY noteId DESC")
    fun allNotes(): LiveData<List<BinNote>>

}

@Dao
interface ArchiveDao {

    @Insert
    suspend fun insert(archiveNote: ArchiveNote)

    @Update
    suspend fun update(archiveNote: ArchiveNote)

    @Delete
    suspend fun delete(archiveNote: ArchiveNote)

    @Query("DELETE FROM archive_table")
    suspend fun clear()

    @Query("SELECT * FROM archive_table ORDER BY noteId DESC")
    fun allNotes(): LiveData<List<ArchiveNote>>

}