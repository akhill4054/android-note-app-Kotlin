package com.example.notebookapp.repos

import androidx.lifecycle.LiveData
import com.example.notebookapp.database.*

class BinRepository(private val binDao: BinDao) {

    val allNotes: LiveData<List<BinNote>> = binDao.allNotes()

    suspend fun bin(note: Note) {
        val binNote = BinNote(
            noteId = note.noteId,
            title = note.title,
            note = note.note,
            lastModified = note.lastModified
        )
        binDao.insert(binNote)
    }

    suspend fun insert(binNote: BinNote) {
        binDao.insert(binNote)
    }

    suspend fun update(binNote: BinNote) {
        binDao.update(binNote)
    }

    suspend fun delete(binNote: BinNote) {
        binDao.delete(binNote)
    }

    suspend fun clear() {
        binDao.clear()
    }

}