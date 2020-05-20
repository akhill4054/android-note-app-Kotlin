package com.example.notebookapp.repos

import androidx.lifecycle.LiveData
import com.example.notebookapp.database.Note
import com.example.notebookapp.database.NoteDao

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.allNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun clear() {
        noteDao.clear()
    }

}