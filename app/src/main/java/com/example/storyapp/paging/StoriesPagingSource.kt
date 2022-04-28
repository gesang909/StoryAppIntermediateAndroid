package com.example.storyapp.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.StoryModel
import com.example.storyapp.paging.StoriesRepository.Companion.NETWORK_PAGE_SIZE
import retrofit2.HttpException
import retrofit2.await
import java.io.IOException

class StoriesPagingSource(
    private val service: ApiConfig,
    private val token: String
): PagingSource<Int, StoryModel>() {
    override fun getRefreshKey(state: PagingState<Int, StoryModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryModel> {
        val position = params.key ?: 1
        return try {
            Log.d("PagingSource", token)
            val response = service.getApiService().fetchStories("Bearer $token", position, params.loadSize)
            val repos = response.await().listStory
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize/NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}