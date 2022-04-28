package com.example.storyapp.paging

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.StoryModel
import kotlinx.coroutines.flow.Flow

class StoriesRepository(private val service: ApiConfig){

    fun getSearchResultStream(token: String): Flow<PagingData<StoryModel>> {
        Log.d("TOKK", token)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoriesPagingSource(service, token) }
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 5
    }
}