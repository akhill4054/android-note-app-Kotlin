package com.example.notebookapp.fragments_main_activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.notebookapp.MainActivity
import com.example.notebookapp.R
import com.example.notebookapp.database.Note
import com.example.notebookapp.viewmodels.SharedNoteViewmodel
import kotlinx.android.synthetic.main.fragment_note.*


class FragmentNewNote : Fragment() {

    private lateinit var viewmodel: SharedNoteViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).hideActionButton()

        viewmodel = ViewModelProvider(requireActivity()).get(SharedNoteViewmodel::class.java)

        // Setting up the toolbar
        (requireActivity() as MainActivity).toolbar.menu.clear()

        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.tempNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                text_title.setText(it.title)
                text_note.setText(it.note)
            }
        })

        text_note.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onStop() {
        super.onStop()

        // To hide soft keyboard if left open
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(text_note.windowToken, 0)

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

        // To make action button appear
        (activity as MainActivity).showActionButton()
    }

}
