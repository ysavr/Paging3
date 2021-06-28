package com.savr.paging3kotlin.model.remote

import com.google.gson.annotations.SerializedName

data class MovieResponse(

    @SerializedName("results")
    val movieList: List<MovieData>
)
