package com.galang.notesapp.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.galang.notesapp.data.entity.Priority
import kotlinx.parcelize.Parcelize

// anotasi entity untuk menandakan bahwa sebuah data class dijadikan sebuah table database
@Entity(tableName = "notes_table")
@Parcelize  //Buat ngelempar data
data class Notes(
    // primary key adalah untuk id didalam table supaya tidak duplikat
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var title : String,
    var priority: Priority,
    var description : String,
    var date : String
):Parcelable
