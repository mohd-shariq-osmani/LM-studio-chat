package com.lmstudio.chat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("id") val id: String = "",
    @SerializedName("object") val `object`: String = "",
    @SerializedName("created") val created: Long = 0,
    @SerializedName("model") val model: String = "",
    @SerializedName("choices") val choices: List<ChatChoice> = emptyList(),
    @SerializedName("usage") val usage: TokenUsage? = null
)

data class ChatChoice(
    @SerializedName("index") val index: Int = 0,
    @SerializedName("message") val message: ChatMessageDto? = null,
    @SerializedName("delta") val delta: ChatDelta? = null,
    @SerializedName("finish_reason") val finishReason: String? = null
)

data class ChatDelta(
    @SerializedName("role") val role: String? = null,
    @SerializedName("content") val content: String? = null
)

data class TokenUsage(
    @SerializedName("prompt_tokens") val promptTokens: Int = 0,
    @SerializedName("completion_tokens") val completionTokens: Int = 0,
    @SerializedName("total_tokens") val totalTokens: Int = 0
)

data class StreamChunk(
    @SerializedName("id") val id: String = "",
    @SerializedName("object") val `object`: String = "",
    @SerializedName("created") val created: Long = 0,
    @SerializedName("model") val model: String = "",
    @SerializedName("choices") val choices: List<ChatChoice> = emptyList()
)
