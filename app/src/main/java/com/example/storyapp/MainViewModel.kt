package com.example.storyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.StoryModel
import com.example.storyapp.model.User
import com.example.storyapp.model.UserPreference
import com.example.storyapp.paging.StoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference) : ViewModel() {
    private val service = ApiConfig()
    private val repository = StoriesRepository(service)

    fun getStories(token: String): Flow<PagingData<StoryModel>> {
        return repository.getSearchResultStream(token).cachedIn(viewModelScope)
    }

    fun getToken(): LiveData<User> {
        return pref.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }


}