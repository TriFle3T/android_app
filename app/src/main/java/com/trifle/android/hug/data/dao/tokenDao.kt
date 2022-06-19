package com.trifle.android.hug.data.dao

import androidx.room.*
import com.trifle.android.hug.data.entity.token

@Dao
interface tokenDao {
    @Insert
    fun insert(tk:token)

    @Update
    fun update(tk:token)

    @Delete
    fun delete(tk:token)

    @Query("SELECT * FROM token")
    fun getAll() : List<token>

    @Query("DELETE FROM token")
    fun deleteAll()
}