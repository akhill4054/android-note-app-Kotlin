package com.example.notebookapp.fragments_main_activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notebookapp.MainActivity
import com.example.notebookapp.R
import com.example.notebookapp.database.Note
import com.example.notebookapp.viewmodels.SharedNoteViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.custom_alertdialog.view.*
import kotlinx.android.synthetic.main.fragment_note.*


class FragmentNote : Fragment() {

    private lateinit var viewmodel: SharedNoteViewmodel
    private lateinit var note: Note

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Setting click listener to action button
        requireActivity().findViewById<FloatingActionButton>(R.id.btn_action).setOnClickListener {
            findNavController().navigate(R.id.action_fragmentNote_to_fragmentNewNote)
        }

        // Setting up the toolbar
        val toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.delete_menu)
        toolbar.setOnMenuItemClickListener {
            // Delete current note and navigate back
            val dialogView = layoutInflater.inflate(R.layout.custom_alertdialog, null, false)
            val builder = AlertDialog.Builder(activity)
                .setView(dialogView)
                .show()

            dialogView.positive_buttton.setOnClickListener {
                // Deleting the note from table
                viewmodel.remove(note)
                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()

                findNavController().navigateUp()
                builder.dismiss()
            }

            dialogView.negative_buttton.setOnClickListener {
                builder.dismiss()
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
    }

    override fun onStop() {
        super.onStop()

        // Update note using viewmodel
        val titleText = text_title.text.toString()
        val noteText = text_note.text.toString()

        if (titleText.length > 0 || noteText.length > 0) {
            note = Note(
                noteId = note.noteId,
                title = titleText,
                note = noteText
            )

            viewmodel.update(note)
        } else
            viewmodel.remove(note)
    }

}
