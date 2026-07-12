package com.lmstudio.chat.domain.model

data class ModelInfo(
    val id: String = "",
    val name: String = "",
    val contextLength: Int? = null,
    val ownedBy: String = "",
    val isLoaded: Boolean = false
)
