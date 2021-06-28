package com.savr.paging3kotlin.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.savr.paging3kotlin.model.local.MovieEntity
import com.savr.paging3kotlin.model.local.RemoteKeys
import com.savr.paging3kotlin.repository.local.AppDatabase
import com.savr.paging3kotlin.repository.remote.APIService
import java.lang.Exception

@ExperimentalPagingApi
class MovieMediator(
    private val apiService: APIService = APIService.getApiService(),
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, MovieEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {

        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getClosestRemoteKey(state)
                    remoteKey?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
                }

                LoadType.APPEND -> {
                    val remoteKeys = getLastRemoteKey(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = apiService.getMovie(API_KEY, LANGUAGE, page)
            val endOfPagination = response.body()?.movieList?.size!! < state.config.pageSize

            if (response.isSuccessful) {
                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        appDatabase.getRemoteDao().clearRemoteKeys()
                        appDatabase.getMovieDao().clearAllMovies()
                    }
                    val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                    val nextKey = if (endOfPagination) null else page + 1

                    val keys = response.body()!!.movieList.map {
                        RemoteKeys(it.id.toString(), prevKey, nextKey)
                    }

                    appDatabase.getRemoteDao().insertAll(keys)
                    val newData: MutableList<MovieEntity> = ArrayList()
                    for (data in response.body()!!.movieList) {
                        newData.add(
                            MovieEntity(
                                data.id.toString(),
                                data.title,
                                data.releaseDate,
                                data.posterPath,
                                data.overview
                            )
                        )
                    }
                    appDatabase.getMovieDao().insertAll(newData)
                }
                MediatorResult.Success(endOfPagination)
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, MovieEntity>): RemoteKeys? {
//        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
//            ?.let { movie -> appDatabase.getRemoteDao().remoteKeyMovieId(movie.id) }
        return state.lastItemOrNull()?.let {
            appDatabase.getRemoteDao().remoteKeyMovieId(it.id)
        }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, MovieEntity>): RemoteKeys? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { movie -> appDatabase.getRemoteDao().remoteKeyMovieId(movie.id) }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, MovieEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                appDatabase.getRemoteDao().remoteKeyMovieId(repoId)
            }
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val API_KEY = "e9906e4a57e2cf19f54dcba5a135d47f"
        private const val LANGUAGE = "en-EN"
    }
}