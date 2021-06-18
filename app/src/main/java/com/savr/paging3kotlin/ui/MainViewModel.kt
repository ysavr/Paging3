package com.savr.paging3kotlin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.savr.paging3kotlin.repository.MainRepository

class MainViewModel(private val repository: MainRepository = MainRepository()): ViewModel() {

    fun movieList() = repository.getMovie().cachedIn(viewModelScope)
}