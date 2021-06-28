package com.savr.paging3kotlin.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.savr.paging3kotlin.model.local.MovieEntity
import com.savr.paging3kotlin.model.local.RemoteKeys

@Database(version = 1, entities = [RemoteKeys::class, MovieEntity::class], exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getRemoteDao(): RemoteKeyDao
    abstract fun getMovieDao(): MovieDao

    companion object {
        private const val db_name = "movie.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, db_name)
                .build()
    }
}