package com.example.medease.api

import com.example.medease.data.model.ml.WqiRequest
import com.example.medease.data.model.ml.WqiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("predict")
    fun predictWqi(
        @Body request: WqiRequest
    ): Call<WqiResponse>
}