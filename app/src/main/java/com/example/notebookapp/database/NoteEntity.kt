package com.example.notebookapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*


@Entity(tableName = "notebook_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var noteId: Long = 0L,

    @ColumnInfo(name = "created_date_time")
    val dateTime: String = formattedDateTimeString(),

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "note")
    var note: String = ""
)

fun formattedDateTimeString(): String {
    val dateTime = LocalDateTime.now()

    val dd = dateTime.dayOfMonth.toString()
    var month = dateTime.month.toString()
    val yy = dateTime.year.toString()

    var hh = dateTime.hour
    val mm = dateTime.minute
    var post = "am"

    month = month[0] + month.slice(1 until month.length).toLowerCase(Locale.ROOT)

    if (hh == 12) post = "pm"
    else if (hh > 12) post = "pm"; hh -= 12

    val hhstr = if (hh < 10)
                    "0${hh}"
                else
                    hh.toString()
    val mmstr = if (mm < 10)
                    "0${mm}"
                else
                    mm.toString()

    return "$dd $month $yy at $hhstr:$mmstr $post"
}