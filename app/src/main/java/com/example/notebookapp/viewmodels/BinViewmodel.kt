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

class BinViewmodel(application: Application): AndroidViewModel(application) {

    private val noteRepository: NoteRepository
    private val binRepository: BinRepository
    private val archiveRepository: ArchiveRepository
    val allBinNotes: LiveData<List<BinNote>>

    init {
        NoteDatabase.getInstance(application, viewModelScope).let { database ->
            noteRepository = NoteRepository(database.noteDao)
            archiveRepository = ArchiveRepository(database.archiveDao)
            binRepository = BinRepository(database.binDao)
        }

        allBinNotes = binRepository.allNotes
    }

    // Selection stuff!
    var isSelectionOn = false
    var isAllSelected = false
    var selectedItemPositions = Array(0) { 0 }
    var selectionCount = 0

    var position = -1

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun restore(binNote: BinNote) = viewModelScope.launch(Dispatchers.IO) {
        val note = Note(
            noteId = binNote.noteId,
            title = binNote.title,
            note = binNote.note,
            lastModified = binNote.lastModified
        )

        noteRepository.insert(note)
    }

    fun removeFromNote(noteId: Long) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.delete(
            Note(noteId = noteId)
        )
    }

    fun insert(binNote: BinNote) = viewModelScope.launch(Dispatchers.IO) {
        binRepository.insert(binNote)
    }

    fun remove(binNote: BinNote) = viewModelScope.launch(Dispatchers.IO) {
        binRepository.delete(binNote)
    }

    fun clear() = viewModelScope.launch(Dispatchers.IO) {
        binRepository.clear()
    }

}