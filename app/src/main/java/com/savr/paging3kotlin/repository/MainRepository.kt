package com.savr.paging3kotlin.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.savr.paging3kotlin.datasource.MoviePagingSource
import com.savr.paging3kotlin.model.MovieData
import kotlinx.coroutines.flow.Flow

class MainRepository {

    fun getMovie(): Flow<PagingData<MovieData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviePagingSource() }
        ).flow
    }
}