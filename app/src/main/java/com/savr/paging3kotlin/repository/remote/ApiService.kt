package com.savr.paging3kotlin.repository.remote

import com.savr.paging3kotlin.model.remote.MovieResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("movie?")
    suspend fun getMovie(
        @Query("api_key") apiKey: String,
        @Query("language") language : String,
        @Query("page") page : Int) : MovieResponse

    companion object {

        fun getApiService(): APIService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/discover/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}