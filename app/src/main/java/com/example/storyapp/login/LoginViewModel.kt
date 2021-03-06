package com.example.storyapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.User
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun saveToken(user: User) {
        viewModelScope.launch {
            pref.saveToken(user)
        }
    }

    fun getToken(): LiveData<User> {
        return pref.getToken().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }
}