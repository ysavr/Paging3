package com.savr.paging3kotlin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.savr.paging3kotlin.model.local.MovieEntity
import com.savr.paging3kotlin.repository.MainRepository
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val repository: MainRepository = MainRepository()): ViewModel() {

    fun movieList() = repository.getMovie().cachedIn(viewModelScope)

    @ExperimentalPagingApi
    fun fetchMovie(): Flow<PagingData<MovieEntity>>{
        return repository.getLocalMovie().cachedIn(viewModelScope)
    }
}