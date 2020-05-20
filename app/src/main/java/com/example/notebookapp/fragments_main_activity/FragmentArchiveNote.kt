package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.notebookapp.MainActivity
import com.example.notebookapp.R
import com.example.notebookapp.database.ArchiveNote
import com.example.notebookapp.hideKeyboard
import com.example.notebookapp.viewmodels.ArchiveViewmodel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_note.*


class FragmentArchiveNote : Fragment() {

    private lateinit var viewmodel: ArchiveViewmodel
    private lateinit var note: ArchiveNote
    private var hasClickedOnMenuItem = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).hideActionButton()

        // Setting up the toolbar
        val toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()

        // Toolbar menu
        toolbar.inflateMenu(R.menu.archive_note_menu)

        toolbar.setOnMenuItemClickListener { item ->
            // Setting up our snackbar
            val snackbar = Snackbar.make(
                (requireActivity() as MainActivity).main_activity_parent,
                "",
                Snackbar.LENGTH_SHORT
            )
            val sbView = snackbar.view
            sbView.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.darkSnackbarColor)
            )

            val noteTitle = text_title.text.toString()
            val noteText = text_note.text.toString()
            if (noteText.isNotEmpty() || noteTitle.isNotEmpty())
                updateNote(noteTitle, noteText)
            else {
                snackbar.setText("This action can't be done with an empty note!")
                    .show()
                return@setOnMenuItemClickListener true
            }

            when (item.itemId) {
                R.id.remove -> {
                    // Move note to bin and navigate up
                    hasClickedOnMenuItem = false

                    viewmodel.remove(note)
                    viewmodel.bin(note)

                    findNavController().navigateUp()

                    // Undo remove action!
                    snackbar.setAction("undo") {
                        viewmodel.insert(note)
                        viewmodel.removeFromBin(note.noteId)
                    }
                        .setText("Moved to bin")
                        .show()
                }
                else -> {
                    hasClickedOnMenuItem = true

                    viewmodel.remove(note)
                    viewmodel.unarchive(note)

                    findNavController().navigateUp()

                    // Undo archive action!
                    snackbar.setAction("undo") {
                        viewmodel.removeFromNote(note.noteId)
                        viewmodel.insert(note)
                    }
                        .setText("Note unarchived")
                        .show()
                }
            }
            true
        }

        return LayoutInflater.from(activity).inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(ArchiveViewmodel::class.java)

        // Get note from viewmodel and set it's content in view
        note = viewmodel.allArchiveNotes.value!![viewmodel.position]
        text_title.setText(note.title)
        text_note.setText(note.note)
    }

    override fun onStop() {
        super.onStop()

        hideKeyboard()

        // Clearing focus, just in case
        text_title.clearFocus()
        text_note.clearFocus()

        // Update note using viewmodel
        val titleText = text_title.text.toString()
        val noteText = text_note.text.toString()

        if (hasClickedOnMenuItem)
            return
        else if (titleText.isNotEmpty() || noteText.isNotEmpty()) {
            updateNote(titleText, noteText)
        } else {
            val sb = Snackbar.make(
                (requireActivity() as MainActivity).main_activity_parent,
                "Empty note can't be saved!",
                Snackbar.LENGTH_SHORT
            )
            val sbView = sb.view
            sbView.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.darkSnackbarColor)
            )
            sb.show()
        }
    }

    private fun updateNote(titleText: String, noteText: String) {
        note = ArchiveNote(
            noteId = note.noteId,
            title = titleText,
            note = noteText
        )

        viewmodel.update(note)
    }

}
