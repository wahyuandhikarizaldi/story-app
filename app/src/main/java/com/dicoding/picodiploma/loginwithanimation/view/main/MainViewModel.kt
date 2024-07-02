package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.repository.UserRepository
import com.dicoding.picodiploma.loginwithanimation.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    storyRepository: StoryRepository?
) : ViewModel() {

    val storiesLiveData: LiveData<PagingData<ListStoryItem>> = storyRepository?.getStories()?.cachedIn(viewModelScope) ?: MutableLiveData()

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout(token: String) {
        viewModelScope.launch {
            userRepository.logout(token)
        }
    }
}
