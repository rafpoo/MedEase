package com.example.medease.database.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medease.api.ApiClient
import com.example.medease.data.model.ml.WqiRequest
import com.example.medease.data.model.ml.WqiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WqiViewModel : ViewModel() {
    private val _result = MutableLiveData<WqiResponse?>()
    val result: LiveData<WqiResponse?> = _result

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun predictWqi(request: WqiRequest) {
        _loading.value = true

        ApiClient.apiService.predictWqi(request)
            .enqueue(object : Callback<WqiResponse> {

                override fun onResponse(
                    call: Call<WqiResponse>,
                    response: Response<WqiResponse>
                ) {
                    _loading.value = false
                    _result.value = response.body()
                }

                override fun onFailure(call: Call<WqiResponse>, t: Throwable) {
                    _loading.value = false
                    _result.value = null
                }
            })
    }

}