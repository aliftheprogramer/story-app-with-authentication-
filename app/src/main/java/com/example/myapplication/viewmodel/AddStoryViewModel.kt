package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.response.AddStoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _addStoryResult = MutableLiveData<Result<AddStoryResponse>>()
    val addStoryResult: LiveData<Result<AddStoryResponse>> = _addStoryResult

    fun addStory(token: String, description: RequestBody, lat: RequestBody?, lon: RequestBody?, photo: MultipartBody.Part) {
        authRepository.addStory(token, description, lat, lon, photo).enqueue(object :
            Callback<AddStoryResponse> {
            override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                if (response.isSuccessful && response.body()?.error == false) {
                    _addStoryResult.value = Result.success(response.body()!!)
                } else {
                    _addStoryResult.value = Result.failure(Exception("Failed to add story"))
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _addStoryResult.value = Result.failure(t)
            }
        })
    }
}