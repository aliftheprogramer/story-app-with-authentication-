package com.example.myapplication.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.api.ApiService
import com.example.myapplication.response.ListStoryItem
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.io.IOException

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStories(position, params.loadSize, 0, "Bearer $token").awaitResponse()

            if (response.isSuccessful) {
                val responseData = response.body()
                Log.d("StoryPagingSource", "Data loaded successfully: ${responseData?.listStory?.size} stories")
                LoadResult.Page(
                    data = responseData?.listStory ?: emptyList(),
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (responseData?.listStory.isNullOrEmpty()) null else position + 1
                )
            } else {
                Log.e("StoryPagingSource", "Error loading data: ${response.errorBody()?.string()}")
                LoadResult.Error(IOException("Error loading data"))
            }
        } catch (exception: Exception) {
            Log.e("StoryPagingSource", "Exception loading data", exception)
            LoadResult.Error(exception)
        }
    }
}