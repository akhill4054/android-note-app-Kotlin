package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.notebookapp.*
import com.example.notebookapp.viewmodels.SharedNoteViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notes.*

const val SCROLL_POSITION = "SCROLL_POSITION"


class FragmentNoteList : Fragment(), OnNoteClickListener {

    private lateinit var viewmodel: SharedNoteViewmodel
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var overlayToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var listAdapter: NoteListAdapter

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

            viewmodel.shouldRequestFocus.value = true
        }

        // To make action button appear
        (activity as MainActivity).showActionButton()

        // Setting up the toolbar
        toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()

        // Reverting toolbar configuration (In case if it was changed in NewNoteFragment)
        toolbar.setupWithNavController(
            findNavController(),
            (requireActivity() as MainActivity).appBarConfiguration
        )

        overlayToolbar = (requireActivity() as MainActivity).overlayToolbar

        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = NoteListAdapter(this)
        notes_list.adapter = listAdapter

        if (viewmodel.isSelectionOn) {
            // Restoring selection state
            listAdapter.selectedItemPositions = viewmodel.selectedItemPositions
            selectionCount = viewmodel.selectionCount
            turnOnSelection()
            if (viewmodel.isAllSelected) {
                isAllSelected = viewmodel.isAllSelected
                overlayToolbar.menu.getItem(0).setIcon(R.drawable.ic_check_box_white_24dp)
            }
        }

        viewmodel.allNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                listAdapter.data = it
            }
        })

        // Getting back scroll position
        savedInstanceState?.run {
            test(getInt(SCROLL_POSITION).toString())
        }
    }

    override fun onStart() {
        super.onStart()

        // Making notes option selected on nav_view menu
        (requireActivity() as MainActivity).nav_view.setCheckedItem(R.id.fragmentNoteList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Saving scroll position, just in case
        outState.run {
            putInt(SCROLL_POSITION, 12)
        }
    }

    override fun onDetach() {
        super.onDetach()

        if (isSelectionOn) {
            // Preserving selection state in viewmodel
            viewmodel.isSelectionOn = true
            viewmodel.isAllSelected = isAllSelected
            viewmodel.selectedItemPositions = listAdapter.selectedItemPositions
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
            listAdapter.selectedItemPositions = Array(listAdapter.data.size) { -1 }
            listAdapter.isSelectionOn = true
        }
        selectItem(position)
    }

    private fun selectItem(position: Int) {
        // Selection stuff!
        listAdapter.selectedItemPositions[position] =
            if (listAdapter.selectedItemPositions[position] == -1) {
                selectionCount++
                if (selectionCount == listAdapter.data.size)
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

        listAdapter.notifyItemChanged(position)
        if (selectionCount == 0) {
            cancelSelection()
        }
    }

    private fun turnOnSelection() {
        isSelectionOn = true
        listAdapter.isSelectionOn = true

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
                    // Moving selection to bin
                    var i = 0
                    for (flag in listAdapter.selectedItemPositions) {
                        if (flag == 1) {
                            val note = listAdapter.data[i]
                            viewmodel.remove(note)
                            viewmodel.bin(note)
                            i++
                        }
                    }

                    listAdapter.notifyDataSetChanged()
                    cancelSelection()

                    // Showing a snackbar
                    showSnackbar("Moved to bin")
                }
                R.id.archive_selection -> {
                    // Moving selection to bin
                    var i = 0
                    for (flag in listAdapter.selectedItemPositions) {
                        if (flag == 1) {
                            val note = listAdapter.data[i]
                            viewmodel.remove(note)
                            viewmodel.archive(note)
                            i++
                        }
                    }

                    listAdapter.notifyDataSetChanged()
                    cancelSelection()

                    // Showing a snackbar
                    showSnackbar("Moved to archive")
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
        listAdapter.isSelectionOn = false
        listAdapter.notifyDataSetChanged()
        viewmodel.isSelectionOn = false
    }

    private fun enableAllSelected() {
        isAllSelected = true
        overlayToolbar.menu.getItem(0).setIcon(R.drawable.ic_check_box_white_24dp)
        selectionCount = listAdapter.data.size
        listAdapter.selectedItemPositions = Array(listAdapter.data.size) { 1 }
        listAdapter.notifyDataSetChanged()

        updateSelectionCountText()
    }

    private fun showSnackbar(msg: String) {
        val snackbar = Snackbar.make(
            (requireActivity() as MainActivity).main_activity_parent,
            msg,
            Snackbar.LENGTH_SHORT
        )
        val sbView = snackbar.view
        sbView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.darkSnackbarColor)
        )
        snackbar.show()
    }

}
