package com.galang.notesapp.data.room

import android.content.Context
import androidx.room.*
import com.galang.notesapp.data.entity.Notes


@Database(entities = [Notes::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {

        // supaya instance database tidak dibuat berulang kali saat aplikasi di running

        @Volatile
        private var instance: NotesDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): NotesDatabase{
            if (instance == null){
                synchronized(NotesDatabase::class.java){
                    instance = Room.databaseBuilder(
                        context,
                        NotesDatabase::class.java,
                        "notes.db"
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return instance as NotesDatabase
        }
    }
}