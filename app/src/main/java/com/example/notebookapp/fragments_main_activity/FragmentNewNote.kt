package com.example.notebookapp.fragments_main_activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notebookapp.MainActivity
import com.example.notebookapp.R
import com.example.notebookapp.database.Note
import com.example.notebookapp.hideKeyboard
import com.example.notebookapp.test
import com.example.notebookapp.viewmodels.SharedNoteViewmodel
import com.example.notebookapp.views.TextInput
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.list_item_view.*


class FragmentNewNote : Fragment() {

    private lateinit var viewmodel: SharedNoteViewmodel
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).hideActionButton()

        viewmodel = ViewModelProvider(requireActivity()).get(SharedNoteViewmodel::class.java)

        // Setting up the toolbar
        toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()

        toolbar.setNavigationOnClickListener {
            saveNote()
            findNavController().navigateUp()
        }

        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.tempNote.observe(viewLifecycleOwner, Observer {
            it?.let { note ->
                text_title.setText(note.title)
                text_note.setText(note.note)
            }
        })



        // Setting focus
        viewmodel.shouldRequestFocus.observe(viewLifecycleOwner, Observer {
            if (it) {
                text_note.requestFocus()

                // Showing keyboard
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(text_note, InputMethodManager.SHOW_IMPLICIT)
            }
        })
    }

    override fun onStop() {
        super.onStop()

        // To hide soft keyboard if left open
        hideKeyboard()
        text_note.hideKeyboard()

        val titleText = text_title.text.toString()
        val noteText = text_note.text.toString()

        if (titleText.isNotEmpty() || noteText.isNotEmpty()) {
            viewmodel.tempNote.value = Note(
                title = titleText,
                note = noteText
            )
        }
    }

    override fun onDetach() {
        super.onDetach()

        viewmodel.shouldRequestFocus.value = false
    }

    fun saveNote() {
        // Saving note
        val titleText = text_title.text.toString()
        val noteText = text_note.text.toString()

        if (titleText.isNotEmpty() || noteText.isNotEmpty()) {
            viewmodel.insert(
                Note(
                title = titleText,
                note = noteText
                )
            )
        }

        viewmodel.tempNote.value = Note()
    }

}
