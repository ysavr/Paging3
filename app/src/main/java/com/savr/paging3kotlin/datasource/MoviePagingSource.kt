package com.savr.paging3kotlin.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.savr.paging3kotlin.model.remote.MovieData
import com.savr.paging3kotlin.repository.remote.APIService
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(private val service: APIService = APIService.getApiService()) :
    PagingSource<Int, MovieData>() {

    override fun getRefreshKey(state: PagingState<Int, MovieData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieData> {
        val pagePosition = params.key ?: STARTING_PAGE_INDEX

        return try {

            val data = service.getMovie(API_KEY, LANGUAGE, pagePosition).movieList
            val prevKey = if (pagePosition == STARTING_PAGE_INDEX) null else pagePosition - 1
            val nextKey = if (data.isEmpty()) null else pagePosition + 1

            LoadResult.Page(data, prevKey, nextKey)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val API_KEY = "e9906e4a57e2cf19f54dcba5a135d47f"
        private const val LANGUAGE = "en-EN"
    }
}