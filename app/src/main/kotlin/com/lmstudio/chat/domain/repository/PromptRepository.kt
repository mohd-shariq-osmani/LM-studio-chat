package com.lmstudio.chat.domain.repository

import com.lmstudio.chat.domain.model.Prompt
import kotlinx.coroutines.flow.Flow

interface PromptRepository {
    fun getAllPrompts(): Flow<List<Prompt>>
    fun getFavoritePrompts(): Flow<List<Prompt>>
    fun getAllFolders(): Flow<List<String>>
    fun getPromptsByFolder(folder: String): Flow<List<Prompt>>
    fun searchPrompts(query: String): Flow<List<Prompt>>
    suspend fun getPromptById(id: Long): Prompt?
    suspend fun createPrompt(prompt: Prompt): Long
    suspend fun updatePrompt(prompt: Prompt)
    suspend fun deletePrompt(id: Long)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun incrementUseCount(id: Long)
}
