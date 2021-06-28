package com.savr.paging3kotlin.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MovieEntity(
    @PrimaryKey
    val id: String,
    val title: String?,
    val releaseDate: String?,
    val posterPath: String?,
    val overview: String?
)