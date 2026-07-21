package com.lmstudio.chat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<ChatMessageDto>,
    @SerializedName("temperature") val temperature: Float = 0.7f,
    @SerializedName("top_p") val topP: Float = 0.9f,
    @SerializedName("max_tokens") val maxTokens: Int = 2048,
    @SerializedName("stream") val stream: Boolean = true,
    @SerializedName("stop") val stop: List<String>? = null
)

data class ChatMessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: Any // Can be String or List<ContentPartDto>
)

data class ContentPartDto(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrlDto? = null
)

data class ImageUrlDto(
    @SerializedName("url") val url: String
)
