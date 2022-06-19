package com.trifle.android.hug.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class token(
    val tk : String,
    val email : String
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
