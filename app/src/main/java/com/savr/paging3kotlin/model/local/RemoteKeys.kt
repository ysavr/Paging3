package com.savr.paging3kotlin.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeys(
    @PrimaryKey
    val movieId: String,
    val prevKey: Int?,
    val nextKey: Int?
)