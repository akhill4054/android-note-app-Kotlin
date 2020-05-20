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

import com.example.notebookapp.viewmodels.BinViewmodel
import kotlinx.android.synthetic.main.fragment_bin.*


class FragmentBin : Fragment(), OnNoteClickListener {

    lateinit var viewmodel: BinViewmodel
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var overlayToolbar: androidx.appcompat.widget.Toolbar
    lateinit var adapter: BinListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(BinViewmodel::class.java)
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

        return inflater.inflate(R.layout.fragment_bin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BinListAdapter(this)
        bin_notes_list.adapter = adapter

        viewmodel.allBinNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }

    override fun onClick(position: Int) {
        viewmodel.position = position

        findNavController().navigate(R.id.action_fragmentBin_to_fragmentBinNote)
    }

    override fun onLongClick(position: Int) {

    }

    fun canceLSelection() {

    }

}
