package com.example.notebookapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


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
    val month = dateTime.month.toString()
    val yy = dateTime.year.toString()
    val hh = dateTime.hour
    val mm = dateTime.minute
    var time = ""

    time = if (hh > 12)
            "${hh - 12}:${mm} pm"
           else if (hh == 12)
            "$hh:${mm} pm"
           else
            "$hh:${mm} am"

    return "$dd $month $yy at $time"
}