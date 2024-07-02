package com.dicoding.picodiploma.loginwithanimation.data.paging

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {

    companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoryPagingSource"
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStories(position, params.loadSize)

            Log.d(TAG, "Request: Page $position, Size ${params.loadSize}")

            if (response.isSuccessful) {
                val responseData = response.body()?.stories ?: emptyList()
                Log.d(TAG, "Response: ${response.body()}")
                LoadResult.Page(
                    data = responseData,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (responseData.isEmpty()) null else position + 1
                )
            } else {
                Log.e(TAG, "Error Response: ${response.message()}")
                LoadResult.Error(Exception("Error: ${response.message()}"))
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Exception: ${exception.message}")
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
