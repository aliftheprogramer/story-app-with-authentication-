package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.myapplication.response.ListStoryItem

interface IAuthRepository {
    fun getPagedStories(token: String): LiveData<PagingData<ListStoryItem>>
}
