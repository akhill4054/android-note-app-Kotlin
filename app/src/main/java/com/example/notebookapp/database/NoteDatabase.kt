package com.example.notebookapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Database(
    entities = [Note::class, BinNote::class, ArchiveNote::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao
    abstract val binDao: BinDao
    abstract val archiveDao: ArchiveDao

    class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val noteDao = database.noteDao
                    val archiveDao = database.archiveDao
                    val binDao = database.binDao

                    noteDao.clear()
                    archiveDao.clear()
                    binDao.clear()

                    for (i in 10 downTo 1) {
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

    }

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): NoteDatabase {
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}