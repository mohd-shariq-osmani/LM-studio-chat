package com.lmstudio.chat.domain.repository

import com.lmstudio.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(conversationId: Long): Flow<List<Message>>
    suspend fun getMessagesOnce(conversationId: Long): List<Message>
    suspend fun sendMessage(conversationId: Long, content: String, modelId: String, images: List<String> = emptyList()): Long
    suspend fun saveAssistantMessage(conversationId: Long, content: String, modelId: String): Long
    suspend fun updateMessage(message: Message)
    suspend fun deleteMessage(id: Long)
    suspend fun editMessage(id: Long, newContent: String)
    fun streamResponse(
        baseUrl: String,
        apiKey: String,
        modelId: String,
        messages: List<Message>,
        systemPrompt: String,
        temperature: Float,
        topP: Float,
        maxTokens: Int
    ): Flow<String>
}
