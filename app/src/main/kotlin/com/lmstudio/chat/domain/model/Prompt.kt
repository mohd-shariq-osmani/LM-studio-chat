package com.lmstudio.chat.domain.model

data class Prompt(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val description: String = "",
    val folder: String = "General",
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(),
    val variables: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val useCount: Int = 0
)
