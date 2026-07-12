package com.lmstudio.chat.domain.model

enum class MessageRole { USER, ASSISTANT, SYSTEM }

data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val role: MessageRole = MessageRole.USER,
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val tokenCount: Int = 0,
    val modelId: String = "",
    val finishReason: String = "",
    val isEdited: Boolean = false,
    val images: List<String> = emptyList()
)
