package com.savr.paging3kotlin.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.savr.paging3kotlin.model.local.MovieEntity
import com.savr.paging3kotlin.model.local.RemoteKeys
import com.savr.paging3kotlin.repository.local.AppDatabase
import com.savr.paging3kotlin.repository.remote.APIService
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

@ExperimentalPagingApi
class MovieMediator(private val apiService: APIService= APIService.getApiService(), private val appDatabase: AppDatabase): RemoteMediator<Int, MovieEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {

//        val pageKeyData = getKeyPageData(loadType, state)
//        val page = when (pageKeyData) {
//            is MediatorResult.Success -> {
//                return pageKeyData
//            }
//            else -> {
//                pageKeyData as Int
//            }
//        }
        val loadKey = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            // In this example, you never need to prepend, since REFRESH
            // will always load the first page in the list. Immediately
            // return, reporting end of pagination.
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
//                val remoteKey = getFirstRemoteKey(state)
//                val prevKey = remoteKey?.prevKey
//                if (prevKey == null) {
//                    return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
//                }
//                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                if (remoteKeys?.nextKey == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
                remoteKeys.nextKey
            }
        }

        try {
            Log.d("loggkeyyy", "$loadKey")
            val response = apiService.getMovie(API_KEY, LANGUAGE, loadKey)
            val endOfPaginationReached = response.movieList.isEmpty()
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.getRemoteDao().clearRemoteKeys()
                    appDatabase.getMovieDao().clearAllMovies()
                }
                val prevKey = if ( loadKey == 1) null else loadKey.minus(1)
                val nextKey = if (endOfPaginationReached) null else loadKey.plus(1)
                val keys = response.movieList.map {
                    RemoteKeys(it.id.toString(), prevKey, nextKey)
                }
                appDatabase.getRemoteDao().insertAll(keys)
                val newData : MutableList<MovieEntity> = ArrayList()
                for (data in response.movieList) {
                    newData.add(MovieEntity(data.id.toString(), data.title, data.releaseDate, data.posterPath, data.overview))
                }
                appDatabase.getMovieDao().insertAll(newData)
            }
            Log.d("loggkeyyy", "$endOfPaginationReached")
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, MovieEntity>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
//                val remoteKeys = getClosestRemoteKey(state)
//                remoteKeys?.nextKey?.minus(1) ?: 1
                null
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
//                val remoteKeys = getFirstRemoteKey(state)
//                    ?: throw InvalidObjectException("Invalid state, key should not be null")
//                //end of list condition reached
//                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
//                remoteKeys.prevKey
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, MovieEntity>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { movie -> appDatabase.getRemoteDao().remoteKeyMovieId(movie.id) }
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