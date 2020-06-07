package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notebookapp.*

import com.example.notebookapp.viewmodels.ArchiveViewmodel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_archive.*


class FragmentArchive : Fragment(), OnNoteClickListener {

    lateinit var viewmodel: ArchiveViewmodel
    lateinit var toolbar: MaterialToolbar
    var actionMode: ActionMode? = null
    lateinit var adapter: ArchiveListAdapter
    lateinit var snackBar: Snackbar

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

        // Initializing our snackbar
        snackBar = Snackbar.make(
            (requireActivity() as MainActivity).parent_layout,
            "",
            Snackbar.LENGTH_SHORT
        )
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkSnackBarColor
            )
        )

        // Hiding action button
        (requireActivity() as MainActivity).hideActionButton(true)

        return inflater.inflate(R.layout.fragment_archive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArchiveListAdapter(this)
        archive_notes_list.adapter = adapter

        viewmodel.allArchiveNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it

                if (it.size > 0)
                    text_no_archives.visibility = View.GONE
                else
                    text_no_archives.visibility = View.VISIBLE
            }
        })

        if (viewmodel.isSelectionOn) {
            adapter.run {
                // Call startSelection before restoring selectedItemsPositions, because it reinitializes it
                startSelection()
                selectedItemPositions = viewmodel.selectedItemPositions
                selectionCount = viewmodel.selectionCount

                actionMode!!.title =
                    if (selectionCount == 1)
                        "1 item selected"
                    else
                        "$selectionCount items selected"

                if (viewmodel.isAllSelected) {
                    isAllSelected = true
                    actionMode!!.menu.getItem(0)
                        .setIcon(R.drawable.ic_check_box_24dp)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()

        // Saving selection state using viewmodel
        if (adapter.isSelectionOn) {
            viewmodel.run {
                isSelectionOn = adapter.isSelectionOn
                isAllSelected = adapter.isAllSelected
                selectedItemPositions = adapter.selectedItemPositions
                selectionCount = adapter.selectionCount
            }
        }
    }

    override fun onClick(position: Int) {
        if (adapter.isSelectionOn)
            selectItem(position)
        else {
            viewmodel.position = position

            findNavController().navigate(R.id.action_fragmentArchive_to_fragmentArchiveNote)
        }
    }

    override fun onLongClick(position: Int) {
        if (!adapter.isSelectionOn)
            startSelection()

        selectItem(position)
    }

    private fun startSelection() {
        (requireActivity() as MainActivity).run {
            // Locking nav drawer
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            // Setting up contextual app bar
            val callback = object : ActionMode.Callback {
                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    (requireActivity() as MainActivity).menuInflater.inflate(R.menu.archive_selection_menu, menu)
                    return true
                }

                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return true
                }

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.select_all -> {
                            adapter.run {
                                if (!isAllSelected) {
                                    isAllSelected = true
                                    selectedItemPositions = Array(itemCount) { 1 }
                                    selectionCount = itemCount
                                    notifyDataSetChanged()

                                    actionMode!!.title =
                                        if (adapter.selectionCount == 1)
                                            "1 item selected"
                                        else
                                            "${adapter.selectionCount} items selected"

                                    actionMode!!.menu.getItem(0)
                                        .setIcon(R.drawable.ic_check_box_24dp)
                                } else
                                    cancelSelection()
                            }
                        }
                        R.id.unarchive_selection -> {
                            // Unarchive selected items
                            if (adapter.isAllSelected) {
                                for (archiveNote in adapter.data)
                                    viewmodel.unarchive(archiveNote)
                                viewmodel.clear()
                            } else {
                                for ((i, position) in adapter.selectedItemPositions.withIndex()) {
                                    if (position == 1) {
                                        val archiveNote = adapter.data[i]
                                        viewmodel.remove(archiveNote)
                                        viewmodel.unarchive(archiveNote)
                                    }
                                }
                            }
                            snackBar.setText("Unarchived selected ${if (adapter.selectionCount == 1) "note" else "notes"}")
                                .show()
                            cancelSelection()
                        }
                        R.id.bin_selection -> {
                            // Move selected items to bin
                            if (adapter.isAllSelected) {
                                for (archiveNote in adapter.data)
                                    viewmodel.bin(archiveNote)
                                viewmodel.clear()
                            } else {
                                for ((i, position) in adapter.selectedItemPositions.withIndex()) {
                                    if (position == 1) {
                                        val archiveNote = adapter.data[i]
                                        viewmodel.remove(archiveNote)
                                        viewmodel.bin(archiveNote)
                                    }
                                }
                            }
                            snackBar.setText("Moved to bin selected ${if (adapter.selectionCount == 1) "note" else "notes"}")
                                .show()
                            cancelSelection()
                        }
                        else ->
                            cancelSelection()
                    }
                    return true
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    cancelSelection()
                }
            }

            actionMode = (requireActivity() as MainActivity).startSupportActionMode(callback)

        }

        adapter.run {
            isSelectionOn = true
            selectedItemPositions = Array(adapter.itemCount) { 0 }
            notifyDataSetChanged()
        }

    }

    fun cancelSelection() {
        // Unlocking nav drawer
        (requireActivity() as MainActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        actionMode?.finish()

        adapter.run {
            isSelectionOn = false
            isAllSelected = false
            selectionCount = 0
            notifyDataSetChanged()
        }

        viewmodel.isSelectionOn = false
    }

    private fun selectItem(position: Int) {
        if (adapter.selectedItemPositions[position] == 1) {
            adapter.run {
                selectedItemPositions[position] = 0
                notifyItemChanged(position)
                selectionCount--

                if (isAllSelected) {
                    isAllSelected = false
                    actionMode!!.menu.getItem(0)
                        .setIcon(R.drawable.ic_check_box_outline_blank_24dp)
                }
            }
        } else {
            adapter.run {
                selectedItemPositions[position] = 1
                notifyItemChanged(position)
                selectionCount++

                if (!isAllSelected && selectionCount == itemCount) {
                    isAllSelected = true
                    actionMode!!.menu.getItem(0)
                        .setIcon(R.drawable.ic_check_box_24dp)
                }
            }
        }

        if (adapter.selectionCount == 0)
            cancelSelection()

        // Updating toolbar
        actionMode!!.title =
            if (adapter.selectionCount == 1)
                "1 item selected"
            else
                "${adapter.selectionCount} items selected"

    }

}
