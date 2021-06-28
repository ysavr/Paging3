package com.savr.paging3kotlin.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.savr.paging3kotlin.datasource.MoviePagingSource
import com.savr.paging3kotlin.mediator.MovieMediator
import com.savr.paging3kotlin.model.local.MovieEntity
import com.savr.paging3kotlin.model.remote.MovieData
import com.savr.paging3kotlin.repository.local.AppDatabase
import com.savr.paging3kotlin.repository.local.LocalInjector
import com.savr.paging3kotlin.repository.remote.APIService
import kotlinx.coroutines.flow.Flow

class MainRepository(private val appDatabase: AppDatabase? = LocalInjector.injectDb()) {

    fun getMovie(): Flow<PagingData<MovieData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviePagingSource() }
        ).flow
    }

    @ExperimentalPagingApi
    fun getLocalMovie(): Flow<PagingData<MovieEntity>> {
        if (appDatabase == null) throw  IllegalStateException("Database is not initialized")

        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { appDatabase.getMovieDao().getAllMovie() },
            remoteMediator = MovieMediator(apiService = APIService.getApiService(), appDatabase = appDatabase)
        ).flow
    }
}