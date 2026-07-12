package com.lmstudio.chat.domain.model

data class AppSettings(
    val baseUrl: String = "http://192.168.0.50:1234/v1",
    val apiKey: String = "",
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val maxTokens: Int = 2048,
    val lastModel: String = "",
    val timeoutSeconds: Int = 120,
    val sendOnEnter: Boolean = true,
    val showTokenCount: Boolean = true,
    val streamResponses: Boolean = true,
    val appLockEnabled: Boolean = false
)
