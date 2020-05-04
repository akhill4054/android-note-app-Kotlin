package com.example.notebookapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase: RoomDatabase() {

    abstract val noteDao: NoteDao

    class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val noteDao = database.noteDao

                    noteDao.clear()

                    for (i in 1..10)
                        noteDao.insert(
                            Note(
                                title = "Sample note $i",
                                note = "This is sample note $i"
                            )
                        )
                }
            }
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope): NoteDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                .addCallback(NoteDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}