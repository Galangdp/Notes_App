package com.galang.notesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.galang.notesapp.data.entity.Notes

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotes(notes: Notes)

    @Query("SELECT * FROM notes_table")
    fun getAllData() : LiveData<List<Notes>>

    @Query("SELECT * FROM notes_table ORDER BY CASE WHEN priority " +
            "LIKE 'H%' THEN 1 WHEN priority " +
            "LIKE 'M%' THEN 2 WHEN priority " +
            "LIKE 'L%' THEN 3 END")
    // % = kata kunci nya jika mau ambil kata kunci yang pertama "high" maka % taro di belakang
    //Jika mau kata kunci di belakang maka sebaliknya taruh % di depan
    //And jika mau nyari tengah tengah ataupun pernah berfikir kalau huruf ini tuh ada
    // caranya adalah di apit dua % for example: %G%
    fun sortByHighPriority(): LiveData<List<Notes>>  //Memantau setiap ada perubahan data

    @Query("SELECT * FROM notes_table ORDER BY CASE WHEN priority " +
            "LIKE 'L%' THEN 1 WHEN priority " +
            "LIKE 'M%' THEN 2 WHEN priority " +
            "LIKE 'H%' THEN 3 END")
    fun sortByLowPriority(): LiveData<List<Notes>>

    @Query("DELETE FROM notes_table")
    suspend fun deleteAllData()

    @Query("SELECT * FROM notes_table WHERE title LIKE :query")
    fun searchByQuery(query: String) : LiveData<List<Notes>>

    @Delete
    suspend fun deleteNote(notes: Notes)

    @Update
    suspend fun updateNote(notes: Notes)
}