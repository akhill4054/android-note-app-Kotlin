package com.example.notebookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notebookapp.database.Note


class ItemViewHolder(itemView: View, val onNoteListener: NoteAdapter.OnNoteClickListener): RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val noteTitle = itemView.findViewById<TextView>(R.id.note_title)
    val noteContent = itemView.findViewById<TextView>(R.id.note_content)
    val noteAddedAt = itemView.findViewById(R.id.note_added_at) as TextView

    init {
        itemView.setOnClickListener(this)
    }

    companion object {
        fun from(parent: ViewGroup, onNoteListener: NoteAdapter.OnNoteClickListener): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)

            val view = layoutInflater.inflate(R.layout.list_item_view, parent, false)

            return ItemViewHolder(view, onNoteListener)
        }
    }

    override fun onClick(v: View?) {
        onNoteListener.onNoteClick(adapterPosition)
    }
}

class NoteAdapter(val onNoteListener: OnNoteClickListener): RecyclerView.Adapter<ItemViewHolder>() {

    var data = listOf<Note>()

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent, onNoteListener)
    }

    interface OnNoteClickListener {
        fun onNoteClick(position: Int)
    }

}