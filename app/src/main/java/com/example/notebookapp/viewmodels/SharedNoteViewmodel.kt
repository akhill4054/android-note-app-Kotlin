package com.example.notebookapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notebookapp.database.ArchiveNote
import com.example.notebookapp.database.BinNote
import com.example.notebookapp.repos.NoteRepository
import com.example.notebookapp.database.Note
import com.example.notebookapp.database.NoteDatabase
import com.example.notebookapp.repos.ArchiveRepository
import com.example.notebookapp.repos.BinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedNoteViewmodel(application: Application): AndroidViewModel(application) {

    private val noteRepository: NoteRepository
    private val binRepository: BinRepository
    private val archiveRepository: ArchiveRepository
    val allNotes: LiveData<List<Note>>

    init {
        NoteDatabase.getInstance(application, viewModelScope).let { database ->
            noteRepository = NoteRepository(database.noteDao)
            archiveRepository = ArchiveRepository(database.archiveDao)
            binRepository = BinRepository(database.binDao)
        }

        allNotes = noteRepository.allNotes
    }

    // Opened note position
    var position = -1

    // Saving note
    var saveNoteFlag = MutableLiveData<Boolean>()
    var tempNote = MutableLiveData<Note>()

    // Selection stuff!
    var isSelectionOn = false
    var isAllSelected = false
    var selectedItemPositions = Array(0) { 0 }
    var selectionCount = 0

    // New note, should request focus?
    val shouldRequestFocus = MutableLiveData<Boolean>(false)

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.insert(note)
    }

    fun remove(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.delete(note)
    }

    fun clear(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.clear()
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.update(note)
    }

    fun archive(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        archiveRepository.archive(note)
    }

    fun bin(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        binRepository.bin(note)
    }

    fun removeFromBin(noteId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val note = BinNote(noteId = noteId)
        binRepository.delete(note)
    }

    fun removeFromArchive(noteId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val note = ArchiveNote(noteId = noteId)
        archiveRepository.delete(note)
    }

}