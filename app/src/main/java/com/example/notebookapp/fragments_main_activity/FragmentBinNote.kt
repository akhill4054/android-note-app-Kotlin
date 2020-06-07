package com.example.notebookapp.fragments_main_activity

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
import com.example.notebookapp.database.BinNote
import com.example.notebookapp.hideKeyboard
import com.example.notebookapp.viewmodels.BinViewmodel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_note.*

class FragmentBinNote : Fragment() {

    lateinit var snackbar: Snackbar
    lateinit var note: BinNote
    lateinit var viewmodel: BinViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        snackbar = Snackbar.make(
            (requireActivity() as MainActivity).parent_layout,
            "Note cannot be edited inside bin",
            Snackbar.LENGTH_SHORT
        )
        val sbView = snackbar.view
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.darkSnackBarColor)
        )

        viewmodel = ViewModelProvider(requireActivity()).get(BinViewmodel::class.java)
        note = viewmodel.allBinNotes.value!![viewmodel.position]

        // Setting up toolbar
        (requireActivity() as MainActivity).toolbar.run {
            menu.clear()
            inflateMenu(R.menu.bin_note_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        // Delete the note forver
                        viewmodel.remove(note)

                        snackbar.setText("Deleted note forever")
                            .setAction("Undo") {
                                viewmodel.insert(note)
                            }
                            .show()
                    }
                    else -> {
                        // Restore note
                        viewmodel.remove(note)
                        viewmodel.restore(note)

                        snackbar.setText("Restored note")
                            .setAction("Undo") {
                                viewmodel.removeFromNote(note.noteId)
                                viewmodel.insert(note)
                            }
                            .show()
                    }
                }

                navigateUp()
                true
            }
        }

        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_title.run {
            setText(note.title)
            focusable = TextInputEditText.NOT_FOCUSABLE
            setOnClickListener {
                hideKeyboard()
                snackbar.show()
            }
        }

        text_note.run {
            setText(note.note)
            focusable = TextInputEditText.NOT_FOCUSABLE
            setOnClickListener {
                snackbar.show()
            }
        }
    }

    private fun navigateUp() = findNavController().navigateUp()

}
