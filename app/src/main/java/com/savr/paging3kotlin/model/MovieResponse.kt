package com.savr.paging3kotlin.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(

    @SerializedName("results")
    val movieList: List<MovieData>
)
