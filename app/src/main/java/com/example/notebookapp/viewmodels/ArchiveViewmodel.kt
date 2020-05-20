package com.example.notebookapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.notebookapp.database.ArchiveNote
import com.example.notebookapp.database.BinNote
import com.example.notebookapp.database.Note
import com.example.notebookapp.database.NoteDatabase
import com.example.notebookapp.repos.ArchiveRepository
import com.example.notebookapp.repos.BinRepository
import com.example.notebookapp.repos.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArchiveViewmodel(application: Application): AndroidViewModel(application) {

    private val noteRepository: NoteRepository
    private val binRepository: BinRepository
    private val archiveRepository: ArchiveRepository
    val allArchiveNotes: LiveData<List<ArchiveNote>>

    var position = -1

    init {
        NoteDatabase.getInstance(application, viewModelScope).let { database ->
            noteRepository = NoteRepository(database.noteDao)
            archiveRepository = ArchiveRepository(database.archiveDao)
            binRepository = BinRepository(database.binDao)
        }

        allArchiveNotes = archiveRepository.allNotes
    }

    // Selection stuff!
    var isSelectionOn = false
    var isAllSelected = false
    var selectedItemPositions = Array(0) { 0 }
    var selectionCount = 0

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun unarchive(archiveNote: ArchiveNote) = viewModelScope.launch(Dispatchers.IO) {
        val note = Note(
            noteId = archiveNote.noteId,
            title = archiveNote.title,
            note = archiveNote.note,
            dateTime = archiveNote.dateTime
        )

        noteRepository.insert(note)
    }

    fun removeFromNote(noteId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val note = Note(noteId = noteId)
        noteRepository.delete(note)
    }

    fun remove(archiveNote: ArchiveNote) = viewModelScope.launch(Dispatchers.IO) {
        archiveRepository.delete(archiveNote)
    }

    fun insert(archiveNote: ArchiveNote) = viewModelScope.launch(Dispatchers.IO) {
        archiveRepository.insert(archiveNote)
    }

    fun update(archiveNote: ArchiveNote) = viewModelScope.launch(Dispatchers.IO) {
        archiveRepository.update(archiveNote)
    }

    fun clear() = viewModelScope.launch(Dispatchers.IO) {
        archiveRepository.clear()
    }

    fun bin(archiveNote: ArchiveNote) = viewModelScope.launch(Dispatchers.IO) {
        binRepository.insert(
            BinNote(
                noteId = archiveNote.noteId,
                title = archiveNote.title,
                note = archiveNote.note,
                dateTime = archiveNote.dateTime
            )
        )
    }

    fun removeFromBin(noteId: Long) = viewModelScope.launch(Dispatchers.IO) {
        binRepository.delete(
            BinNote(noteId = noteId)
        )
    }

}