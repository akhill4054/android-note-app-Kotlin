package com.example.notebookapp

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notebookapp.database.Note


class ItemViewHolder(itemView: View, val onNoteListener: NoteAdapter.OnNoteClickListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
    val rootView = itemView.findViewById<View>(R.id.root_view)
    val noteTitle = itemView.findViewById<TextView>(R.id.note_title)
    val noteContent = itemView.findViewById<TextView>(R.id.note_content)
    val noteAddedAt = itemView.findViewById(R.id.note_added_at) as TextView

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onNoteListener: NoteAdapter.OnNoteClickListener
        ): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)

            val view = layoutInflater.inflate(R.layout.list_item_view, parent, false)

            return ItemViewHolder(view, onNoteListener)
        }
    }

    override fun onClick(v: View?) {
        onNoteListener.onClick(adapterPosition)
    }

    override fun onLongClick(v: View?): Boolean {
        onNoteListener.onLongClick(adapterPosition)
        return true
    }

}

class NoteAdapter(val onNoteListener: OnNoteClickListener) :
    RecyclerView.Adapter<ItemViewHolder>() {

    var isSelectionOn = false

    var data = listOf<Note>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedItemPositions = Array(0) { -1 }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]

        holder.noteTitle.text = item.title
        holder.noteContent.text = item.note
        holder.noteAddedAt.text = item.dateTime

        if (item.title.length == 0) {
            holder.noteTitle.visibility = View.GONE
            holder.noteContent.visibility = View.VISIBLE
        } else if (item.note.length == 0) {
            holder.noteTitle.visibility = View.VISIBLE
            holder.noteContent.visibility = View.GONE
        } else {
            holder.noteTitle.visibility = View.VISIBLE
            holder.noteContent.visibility = View.VISIBLE
        }

        if (isSelectionOn && selectedItemPositions[position] == 1) {
            holder.rootView.setBackgroundResource(R.drawable.selected_ripple)
        } else {
            holder.rootView.setBackgroundResource(R.drawable.ripple)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent, onNoteListener)
    }

    interface OnNoteClickListener {
        fun onClick(position: Int)

        fun onLongClick(position: Int)
    }

}