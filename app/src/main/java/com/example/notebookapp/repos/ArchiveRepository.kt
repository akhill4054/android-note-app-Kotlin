package com.example.notebookapp.repos

import androidx.lifecycle.LiveData
import com.example.notebookapp.database.ArchiveDao
import com.example.notebookapp.database.ArchiveNote
import com.example.notebookapp.database.Note
import com.example.notebookapp.database.NoteDao

class ArchiveRepository(private val archiveDao: ArchiveDao) {

    val allNotes: LiveData<List<ArchiveNote>> = archiveDao.allNotes()

    suspend fun archive(note: Note) {
        val archiveNote = ArchiveNote(
            noteId = note.noteId,
            title = note.title,
            note = note.note,
            dateTime = note.dateTime
        )

        archiveDao.insert(archiveNote)
    }

    suspend fun insert(archiveNote: ArchiveNote) {
        archiveDao.insert(archiveNote)
    }

    suspend fun update(archiveNote: ArchiveNote) {
        archiveDao.update(archiveNote)
    }

    suspend fun delete(archiveNote: ArchiveNote) {
        archiveDao.delete(archiveNote)
    }

    suspend fun clear() {
        archiveDao.clear()
    }

}