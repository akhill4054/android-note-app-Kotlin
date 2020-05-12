package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notes.*


class FragmentNoteList : Fragment(), NoteAdapter.OnNoteClickListener {

    private lateinit var viewmodel: SharedNoteViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(SharedNoteViewmodel::class.java)
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
    }

    override fun onStart() {
        super.onStart()

        // Making notes option selected on nav_view menu
        (requireActivity() as MainActivity).nav_view.setCheckedItem(R.id.fragmentNoteList)
    }

    override fun onNoteClick(position: Int) {
        viewmodel.position = position

        findNavController().navigate(R.id.action_fragmentNoteList_to_fragmentNote)
    }

}
