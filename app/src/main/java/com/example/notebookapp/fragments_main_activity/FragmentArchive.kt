package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notebookapp.*

import com.example.notebookapp.viewmodels.ArchiveViewmodel
import kotlinx.android.synthetic.main.fragment_archive.*


class FragmentArchive : Fragment(), OnNoteClickListener {

    lateinit var viewmodel: ArchiveViewmodel
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var overlayToolbar: androidx.appcompat.widget.Toolbar
    lateinit var adapter: ArchiveListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(ArchiveViewmodel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Setting up the toolbar(s)
        toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()

        overlayToolbar = (requireActivity() as MainActivity).overlayToolbar

        // Hiding action button
        (requireActivity() as MainActivity).hideActionButton()

        return inflater.inflate(R.layout.fragment_archive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArchiveListAdapter(this)
        archive_notes_list.adapter = adapter

        viewmodel.allArchiveNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }

    override fun onClick(position: Int) {
        viewmodel.position = position

        findNavController().navigate(R.id.action_fragmentArchive_to_fragmentArchiveNote)
    }

    override fun onLongClick(position: Int) {

    }

    fun canceLSelection() {

    }

}
