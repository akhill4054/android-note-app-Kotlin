package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notebookapp.MainActivity
import com.example.notebookapp.NoteAdapter
import com.example.notebookapp.R
import com.example.notebookapp.viewmodels.SharedNoteViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_notes.*


class FragmentNoteList : Fragment(), NoteAdapter.OnNoteClickListener {

    private lateinit var viewmodel: SharedNoteViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewmodel = ViewModelProvider(requireActivity()).get(SharedNoteViewmodel::class.java)

        // Setting click listener to action button
        requireActivity().findViewById<FloatingActionButton>(R.id.btn_action).setOnClickListener {
            findNavController().navigate(R.id.action_fragmentNoteList_to_fragmenNewNote)
        }

        // Setting up the toolbar
        val toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.settings) {
                findNavController().navigate(R.id.action_fragmentNoteList_to_fragmentSettings)
                true
            } else false
        }

        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NoteAdapter(this)
        notes_list.adapter = adapter

        viewmodel.allNotes.observe(viewLifecycleOwner, Observer {
            notes_list.adapter = adapter
            it?.let {
                adapter.data = it
            }
        })

        viewmodel.tempNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewmodel.insert(it)
                viewmodel.tempNote.value = null
            }
        })
    }

    override fun onNoteClick(position: Int) {
        viewmodel.position = position

        findNavController().navigate(R.id.action_fragmentNoteList_to_fragmentNote)
    }

}
