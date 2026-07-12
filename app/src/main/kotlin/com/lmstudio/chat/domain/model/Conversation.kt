package com.lmstudio.chat.domain.model

data class Conversation(
    val id: Long = 0,
    val title: String = "New Chat",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val modelId: String = "",
    val personaId: Long = -1L,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val messageCount: Int = 0,
    val lastMessagePreview: String = "",
    val tokenCount: Int = 0,
    val folder: String = ""
)
