package com.dicoding.picodiploma.loginwithanimation.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.paging.StoryPagingSource
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import retrofit2.HttpException
import java.io.IOException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun getLocation(): List<ListStoryItem> {
        val allStories = mutableListOf<ListStoryItem>()
        var page = 1
        val size = 20

        while (true) {
            try {
                val response = apiService.getLocations(page, size)
                if (response.isSuccessful) {
                    val stories = response.body()?.stories ?: emptyList()
                    allStories.addAll(stories)
                    if (stories.size < size) break
                    page++
                } else {
                    Log.e("StoryRepository", "Failed to fetch stories: ${response.message()}")
                    break
                }
            } catch (e: IOException) {
                Log.e("StoryRepository", "Network error: ${e.message}")
                break
            } catch (e: HttpException) {
                Log.e("StoryRepository", "HTTP error: ${e.message()}")
                break
            }
        }

        return allStories
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference).also { instance = it }
            }
    }
}
