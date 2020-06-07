package com.example.notebookapp.fragments_main_activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notebookapp.MainActivity
import com.example.notebookapp.R
import com.example.notebookapp.database.Note
import com.example.notebookapp.hideKeyboard
import com.example.notebookapp.test
import com.example.notebookapp.viewmodels.SharedNoteViewmodel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_note.*
import java.text.SimpleDateFormat


class FragmentNote : Fragment() {

    private lateinit var viewmodel: SharedNoteViewmodel
    private lateinit var note: Note
    private var hasClickedOnMenuItem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Passed data
        test(FragmentNoteArgs.fromBundle(requireArguments()).position.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).hideActionButton(true)

        // Setting up the toolbar
        val toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()

        // Toolbar menu
        toolbar.inflateMenu(R.menu.edit_note_menu)

        toolbar.setOnMenuItemClickListener { item ->
            // Setting up our snackbar
            val snackbar = Snackbar.make(
                (requireActivity() as MainActivity).parent_layout,
                "",
                Snackbar.LENGTH_SHORT
            )
            val sbView = snackbar.view
            sbView.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.darkSnackBarColor)
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
                        viewmodel.removeFromBin(note.noteId)
                        viewmodel.insert(note)
                    }
                        .setText("Moved to bin")
                        .show()
                } R.id.archive -> {
                    hasClickedOnMenuItem = true

                    viewmodel.archive(note)
                    viewmodel.remove(note)

                    findNavController().navigateUp()

                    // Undo archive action!
                    snackbar.setAction("undo") {
                        viewmodel.removeFromArchive(note.noteId)
                        viewmodel.insert(note)
                    }
                        .setText("Note archived")
                        .show()
                } else -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            if (note.title.isNotEmpty()) {
                                "${note.title}\n${note.note}"
                            }
                            else
                                note.title + note.note
                        )
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }

            true
        }

        return LayoutInflater.from(activity).inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(SharedNoteViewmodel::class.java)

        // Get note from viewmodel and set it's content in view
        note = viewmodel.allNotes.value!![viewmodel.position]
        text_title.setText(note.title)
        text_note.setText(note.note)
        val date = note.lastModified
        val dateString = SimpleDateFormat("dd MMM yyyy").format(date)
        val timeString = SimpleDateFormat("hh:mm a").format(date)
        text_edited_on.text = "edited on $dateString at $timeString"
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
                (requireActivity() as MainActivity).parent_layout,
                "Empty note can't be saved!",
                Snackbar.LENGTH_SHORT
            )
            val sbView = sb.view
            sbView.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.darkSnackBarColor)
            )
            sb.show()
        }
    }

    override fun onDetach() {
        super.onDetach()

        // Passing data to start destination
        val args = Bundle()
        args.putString("data", "Hello World!")
        findNavController().setGraph(R.navigation.nav_graph, args)
    }

    private fun updateNote(titleText: String, noteText: String) {
        note = Note(
            noteId = note.noteId,
            title = titleText,
            note = noteText
        )

        val oldNote = viewmodel.allNotes.value!![viewmodel.position]
        if (oldNote.title != note.title || oldNote.note != note.note)
            viewmodel.update(note)
    }

}
