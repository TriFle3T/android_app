package com.trifle.android.hug.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trifle.android.hug.data.dao.tokenDao
import com.trifle.android.hug.data.entity.token

@Database(entities = arrayOf(token::class), version = 2)
abstract class userDb : RoomDatabase(){
    abstract fun tokenDao() : tokenDao

    companion object {
        private var instance : userDb ? = null
        @Synchronized
        fun getInstance(context: Context): userDb? {
            if (instance == null){
                synchronized(userDb::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        userDb::class.java,
                        "user-database"
                    )
                    .addMigrations(Migration_1_2)
                    .build()
                }
            }
            return instance
        }
        private val Migration_1_2 = object:Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'token' ADD 'email' STRING ")
            }
        }
    }

}