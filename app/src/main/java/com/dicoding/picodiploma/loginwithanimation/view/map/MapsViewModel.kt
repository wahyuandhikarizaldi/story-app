package com.dicoding.picodiploma.loginwithanimation.view.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.repository.StoryRepository
import kotlinx.coroutines.launch

class MapsViewModel(
    private val storyRepository: StoryRepository?
) : ViewModel() {

    private val _storiesLiveData = MutableLiveData<List<ListStoryItem>>()
    val storiesLiveData: LiveData<List<ListStoryItem>> get() = _storiesLiveData

    init {
        loadStories()
    }

    private fun loadStories() {
        viewModelScope.launch {
            try {
                storyRepository?.let {
                    val stories = it.getLocation()
                    _storiesLiveData.postValue(stories)
                } ?: run {
                    Log.e("MapsViewModel", "StoryRepository is null")
                }
            } catch (e: Exception) {
                Log.e("MapsViewModel", "Error loading stories", e)
            }
        }
    }
}
