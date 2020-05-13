package com.example.notebookapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notebookapp.NoteRepository
import com.example.notebookapp.database.Note
import com.example.notebookapp.database.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedNoteViewmodel(application: Application): AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>

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

    init {
        val noteDao = NoteDatabase.getInstance(application, viewModelScope).noteDao
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun remove(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun clear(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.clear()
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

}