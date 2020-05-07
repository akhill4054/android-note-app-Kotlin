package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(SharedNoteViewmodel::class.java)
        viewmodel.saveNoteFlag.value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            it?.let {
                adapter.data = it
                adapter.notifyDataSetChanged()
            }
        })

        viewmodel.tempNote.observe(viewLifecycleOwner, Observer {
            it?.let { note ->
                viewmodel.saveNoteFlag.observe(
                    viewLifecycleOwner,
                    Observer {
                        it?.let {flag ->
                            if (flag) {
                                // Saving note
                                viewmodel.insert(note)
                                viewmodel.tempNote.value = null
                                viewmodel.saveNoteFlag.value = false
                            }
                        }
                    }
                )
            }
        })
    }

    override fun onNoteClick(position: Int) {
        viewmodel.position = position

        findNavController().navigate(R.id.action_fragmentNoteList_to_fragmentNote)
    }

}
