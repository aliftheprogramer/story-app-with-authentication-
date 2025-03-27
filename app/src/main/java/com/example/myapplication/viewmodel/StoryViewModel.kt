package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myapplication.response.ListStoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val authRepository: IAuthRepository) : ViewModel() {

    fun getPagedStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return authRepository.getPagedStories(token).cachedIn(viewModelScope)
    }
}