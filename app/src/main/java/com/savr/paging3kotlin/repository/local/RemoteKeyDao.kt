package com.savr.paging3kotlin.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savr.paging3kotlin.model.local.RemoteKeys

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<RemoteKeys>)

    @Query("Select * from RemoteKeys where movieId = :id")
    suspend fun remoteKeyMovieId(id: String): RemoteKeys?

    @Query("Delete from RemoteKeys")
    suspend fun clearRemoteKeys()
}