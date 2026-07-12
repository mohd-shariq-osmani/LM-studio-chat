package com.lmstudio.chat.data.remote.api

import com.google.gson.Gson
import com.lmstudio.chat.data.remote.dto.ChatRequest
import com.lmstudio.chat.data.remote.dto.StreamChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    fun streamChat(
        baseUrl: String,
        apiKey: String,
        request: ChatRequest
    ): Flow<String> = flow {
        var normalizedBase = baseUrl.trimEnd('/')
        if (!normalizedBase.endsWith("/v1") && !normalizedBase.endsWith("/v1/")) {
            normalizedBase = "$normalizedBase/v1"
        }
        val url = "$normalizedBase/chat/completions"

        val jsonBody = gson.toJson(request.copy(stream = true))
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url(url)
            .post(requestBody)
            .apply {
                if (apiKey.isNotEmpty()) addHeader("Authorization", "Bearer $apiKey")
                addHeader("Accept", "text/event-stream")
                addHeader("Content-Type", "application/json")
            }
            .build()

        val response = okHttpClient.newCall(httpRequest).execute()

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}: ${response.message}")
        }

        val body = response.body ?: throw Exception("Empty response body")

        body.source().use { source ->
            while (!source.exhausted()) {
                val line = source.readUtf8Line() ?: break
                when {
                    line.startsWith("data: ") -> {
                        val data = line.removePrefix("data: ").trim()
                        if (data == "[DONE]") return@flow
                        try {
                            val chunk = gson.fromJson(data, StreamChunk::class.java)
                            val content = chunk.choices.firstOrNull()?.delta?.content
                            if (!content.isNullOrEmpty()) emit(content)
                        } catch (_: Exception) { /* skip malformed */ }
                    }
                    else -> { /* skip empty/other lines */ }
                }
            }
        }
    }
}
