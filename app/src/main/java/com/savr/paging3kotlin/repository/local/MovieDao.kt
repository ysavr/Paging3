package com.savr.paging3kotlin.repository.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savr.paging3kotlin.model.local.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<MovieEntity>)

    @Query("Select * from MovieEntity")
    fun getAllMovie(): PagingSource<Int, MovieEntity>

    @Query("Delete from MovieEntity")
    suspend fun clearAllMovies()
}