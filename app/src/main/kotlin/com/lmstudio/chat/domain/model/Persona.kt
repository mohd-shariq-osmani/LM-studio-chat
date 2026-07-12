package com.lmstudio.chat.domain.model

data class Persona(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val icon: String = "smart_toy",
    val systemPrompt: String = "",
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val maxTokens: Int = 2048,
    val contextLength: Int = 4096,
    val color: String = "#19C37D",
    val isFavorite: Boolean = false,
    val isDefault: Boolean = false,
    val categories: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isBuiltin: Boolean = false,
    val sortOrder: Int = 0
)
