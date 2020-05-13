package com.example.notebookapp.fragments_main_activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
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
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var overlayToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var adapter: NoteAdapter

    // List item selection stuff
    var isSelectionOn = false
    private var isAllSelected = false
    private var selectionCount = 0

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
            if (isSelectionOn)
                cancelSelection()

            findNavController().navigate(R.id.action_fragmentNoteList_to_fragmenNewNote)
        }

        // Setting up the toolbar
        toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()

        overlayToolbar = (requireActivity() as MainActivity).overlayToolbar

        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NoteAdapter(this)
        notes_list.adapter = adapter

        if (viewmodel.isSelectionOn) {
            // Restoring selection state
            adapter.selectedItemPositions = viewmodel.selectedItemPositions
            selectionCount = viewmodel.selectionCount
            turnOnSelection()
            if (viewmodel.isAllSelected) {
                isAllSelected = viewmodel.isAllSelected
                overlayToolbar.menu.getItem(0).setIcon(R.drawable.ic_check_box_white_24dp)
            }
        }

        viewmodel.allNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }

    override fun onStart() {
        super.onStart()

        // Making notes option selected on nav_view menu
        (requireActivity() as MainActivity).nav_view.setCheckedItem(R.id.fragmentNoteList)
    }

    override fun onDetach() {
        super.onDetach()

        if (isSelectionOn) {
            // Preserving selection state in viewmodel
            viewmodel.isSelectionOn = true
            viewmodel.isAllSelected = isAllSelected
            viewmodel.selectedItemPositions = adapter.selectedItemPositions
            viewmodel.selectionCount = selectionCount
        }
    }

    override fun onClick(position: Int) {
        if (isSelectionOn) {
            selectItem(position)
        } else {
            // Action on note view item click
            viewmodel.position = position
            findNavController().navigate(R.id.action_fragmentNoteList_to_fragmentNote)
        }
    }

    override fun onLongClick(position: Int) {
        if (!isSelectionOn) {
            turnOnSelection()
            adapter.selectedItemPositions = Array(adapter.data.size) { -1 }
            adapter.isSelectionOn = true
        }
        selectItem(position)
    }

    private fun selectItem(position: Int) {
        // Selection stuff!
        adapter.selectedItemPositions[position] =
            if (adapter.selectedItemPositions[position] == -1) {
                selectionCount++
                if (selectionCount == adapter.data.size)
                    enableAllSelected()
                1
            } else {
                selectionCount--
                if (isAllSelected) {
                    isAllSelected = false
                    viewmodel.isAllSelected = false
                    overlayToolbar.menu.getItem(0)
                        .setIcon(R.drawable.ic_check_box_outline_blank_white_24dp)
                }
                -1
            }

        updateSelectionCountText()

        adapter.notifyItemChanged(position)
        if (selectionCount == 0) {
            cancelSelection()
        }
    }

    private fun turnOnSelection() {
        isSelectionOn = true
        adapter.isSelectionOn = true

        // Locking nav drawer
        (requireActivity() as MainActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // Setting up slection toolbar
        overlayToolbar.visibility = View.VISIBLE
        updateSelectionCountText()

        overlayToolbar.inflateMenu(R.menu.selection_menu)
        overlayToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.select_all -> {
                    if (isAllSelected) {
                        cancelSelection()
                        viewmodel.isAllSelected = false
                    } else
                        enableAllSelected()
                }
                R.id.delete_selection -> {
                    TODO("Move to bin selected items here.")
                }
                R.id.archive_selection -> {
                    TODO("Archive selected items here.")
                }
                else ->
                    cancelSelection()
            }
            true
        }
    }

    fun updateSelectionCountText() = overlayToolbar.setTitle(
        if (selectionCount == 1)
            "1 item selected"
        else
            "$selectionCount items selected"
    )

    fun cancelSelection() {
        // Unlocking nav drawer
        (requireActivity() as MainActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        overlayToolbar.visibility = View.INVISIBLE
        overlayToolbar.menu.clear()

        isAllSelected = false
        isSelectionOn = false
        selectionCount = 0
        adapter.isSelectionOn = false
        adapter.notifyDataSetChanged()
        viewmodel.isSelectionOn = false
    }

    private fun enableAllSelected() {
        isAllSelected = true
        overlayToolbar.menu.getItem(0).setIcon(R.drawable.ic_check_box_white_24dp)
        selectionCount = adapter.data.size
        adapter.selectedItemPositions = Array(adapter.data.size) { 1 }
        adapter.notifyDataSetChanged()

        updateSelectionCountText()
    }

}
