package com.savr.paging3kotlin.model

import com.google.gson.annotations.SerializedName

data class MovieData (
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("overview")
    val overview: String?
)