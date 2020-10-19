package com.savr.paging3kotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.savr.paging3kotlin.data.datasource.PostDataSource
import com.savr.paging3kotlin.data.APIService

class MainViewModel(private val apiService: APIService) : ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 6)) {
        PostDataSource(apiService)
    }.flow.cachedIn(viewModelScope)
}