package com.example.myapplication.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSourceFactory
import androidx.paging.liveData
import com.example.myapplication.api.ApiService
import com.example.myapplication.api.LoginRequest
import com.example.myapplication.api.RegisterRequest
import com.example.myapplication.response.AddStoryResponse
import com.example.myapplication.response.ListStoryItem
import com.example.myapplication.response.LoginResponse
import com.example.myapplication.response.RegisterResponse
import com.example.myapplication.response.StoryResponse
import com.example.myapplication.viewmodel.IAuthRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) : IAuthRepository {

    fun login(email: String, password: String): Call<LoginResponse> {
        return apiService.login(LoginRequest(email, password))
    }

    fun register(email: String, name: String, password: String): Call<RegisterResponse> {
        return apiService.register(RegisterRequest(email, name, password))
    }


    fun addStory(
        token: String,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
        photo: MultipartBody.Part
    ): Call<AddStoryResponse> {
        return apiService.addStory("Bearer $token", description, lat, lon, photo)
    }

    fun getStoriesWithLocation(token: String): Call<StoryResponse> {
        return apiService.getStories(1, null, 1, "Bearer $token")
    }

    override fun getPagedStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }





}