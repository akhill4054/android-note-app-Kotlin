package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notebookapp.*
import com.example.notebookapp.viewmodels.SharedNoteViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notes.*

class FragmentNoteList : Fragment(), OnNoteClickListener {

    private lateinit var viewmodel: SharedNoteViewmodel
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    //    private lateinit var overlayToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var adapter: NoteListAdapter
    private lateinit var snackbar: Snackbar
    var actionMode: ActionMode? = null

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
        // Getting passed data from note fragment
        test("*${arguments?.get("data")}")

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

        // Initializing snackbar
        snackbar = Snackbar.make(
            (requireActivity() as MainActivity).parent_layout,
            "",
            Snackbar.LENGTH_SHORT
        )
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.darkSnackBarColor)
        )

        // Reverting toolbar configuration (In case if it was changed in NewNoteFragment)
        toolbar.setupWithNavController(
            findNavController(),
            (requireActivity() as MainActivity).appBarConfiguration
        )

//        overlayToolbar = (requireActivity() as MainActivity).overlayToolbar

        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setting up our list
        adapter = NoteListAdapter(this)
        notes_list.adapter = adapter

        if (viewmodel.isSelectionOn) {
            // Restoring selection state
            adapter.selectedItemPositions = viewmodel.selectedItemPositions
            selectionCount = viewmodel.selectionCount
            turnOnSelection()
            if (viewmodel.isAllSelected) {
                isAllSelected = viewmodel.isAllSelected
                actionMode!!.menu.getItem(0).setIcon(R.drawable.ic_check_box_24dp)
            }
        }

        // Observerving note list in viewmodel
        viewmodel.allNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)

                if (it.size > 0)
                    text_no_notes.visibility = View.GONE
                else {
                    text_no_notes.visibility = View.VISIBLE
                }
            }
        })

        // For swiping gestures in our list
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                // To prevent swiping gesture while selection is on
                return super.isItemViewSwipeEnabled() && !adapter.isSelectionOn
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
//                val note = adapter.data[position]
                val note = adapter.currentList[position]

                viewmodel.archive(note)
                viewmodel.remove(note)

                snackbar.setText("Moved to archive")
                    .setAction("Undo") {
                        // Undo archive
                        viewmodel.insert(note)
                        viewmodel.removeFromArchive(note.noteId)
                    }
                    .show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(notes_list)
    }

    override fun onStart() {
        super.onStart()

        // Super jump!
//        findNavController().navigate(R.id.fragmentArchive)

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

    // Handling list item click
    override fun onClick(position: Int) {
        if (isSelectionOn) {
            selectItem(position)
        } else {
            // Action on note view item click
            viewmodel.position = position

            // Bundle to pass to destination
            val action = FragmentNoteListDirections.actionFragmentNoteListToFragmentNote(position)
            findNavController().navigate(action)
        }
    }

    override fun onLongClick(position: Int) {
        if (!isSelectionOn) {
            turnOnSelection()
            adapter.selectedItemPositions = Array(adapter.itemCount) { -1 }
            adapter.isSelectionOn = true
        }
        selectItem(position)
    }

    private fun selectItem(position: Int) {
        // Selection stuff!
        adapter.selectedItemPositions[position] =
            if (adapter.selectedItemPositions[position] == -1) {
                selectionCount++
                if (selectionCount == adapter.itemCount)
                    enableAllSelected()
                1
            } else {
                selectionCount--
                if (isAllSelected) {
                    isAllSelected = false
                    viewmodel.isAllSelected = false
                    actionMode!!.menu.get(0)
                        .setIcon(R.drawable.ic_check_box_outline_blank_24dp)
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

        // Setting up contextual menu
        val callback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                (requireActivity() as MainActivity).menuInflater.inflate(
                    R.menu.notes_selection_menu,
                    menu
                )
                return true
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
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
                        for (flag in adapter.selectedItemPositions) {
                            if (flag == 1) {
//                                val note = adapter.data[i]
                                val note = adapter.currentList[i]
                                viewmodel.remove(note)
                                viewmodel.bin(note)
                                i++
                            }
                        }
                        adapter.notifyDataSetChanged()
                        cancelSelection()

                        // Showing a snackbar
                        showSnackbar("Moved to bin")
                    }
                    R.id.archive_selection -> {
                        // Moving selection to bin
                        var i = 0
                        for (flag in adapter.selectedItemPositions) {
                            if (flag == 1) {
//                                val note = adapter.data[i]
                                val note = adapter.currentList[i]
                                viewmodel.remove(note)
                                viewmodel.archive(note)
                                i++
                            }
                        }

                        adapter.notifyDataSetChanged()
                        cancelSelection()

                        // Showing a snackbar
                        showSnackbar("Moved to archive")
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
        updateSelectionCountText()
    }

    fun updateSelectionCountText() {
        actionMode?.title =
            if (selectionCount == 1)
                "1 item selected"
            else
                "$selectionCount items selected"
    }

    fun cancelSelection() {
        // Unlocking nav drawer
        (requireActivity() as MainActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        toolbar.visibility = View.VISIBLE

        isAllSelected = false
        isSelectionOn = false
        selectionCount = 0
        adapter.isSelectionOn = false
        adapter.notifyDataSetChanged()
        viewmodel.isSelectionOn = false

        actionMode?.finish()
        actionMode = null
    }

    private fun enableAllSelected() {
        isAllSelected = true
        actionMode!!.menu.get(0).setIcon(R.drawable.ic_check_box_24dp)
        selectionCount = adapter.itemCount
        adapter.selectedItemPositions = Array(adapter.itemCount) { 1 }
        adapter.notifyDataSetChanged()

        updateSelectionCountText()
    }

    private fun showSnackbar(msg: String) {
        val snackbar = Snackbar.make(
            (requireActivity() as MainActivity).parent_layout,
            msg,
            Snackbar.LENGTH_SHORT
        )
        val sbView = snackbar.view
        sbView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.darkSnackBarColor)
        )
        snackbar.show()
    }

}
