package com.lmstudio.chat.data.remote.api

import com.lmstudio.chat.data.remote.dto.ChatRequest
import com.lmstudio.chat.data.remote.dto.ChatResponse
import com.lmstudio.chat.data.remote.dto.ModelsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface LmStudioApiService {

    @GET
    suspend fun getModels(
        @Url url: String,
        @Header("Authorization") authorization: String = ""
    ): Response<ModelsResponse>

    @POST
    suspend fun chatCompletion(
        @Url url: String,
        @Header("Authorization") authorization: String = "",
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
