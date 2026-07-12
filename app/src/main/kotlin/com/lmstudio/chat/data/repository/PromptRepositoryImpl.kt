package com.lmstudio.chat.data.repository

import com.lmstudio.chat.data.local.db.PromptDao
import com.lmstudio.chat.domain.model.Prompt
import com.lmstudio.chat.domain.repository.PromptRepository
import com.lmstudio.chat.util.toDomain
import com.lmstudio.chat.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptRepositoryImpl @Inject constructor(
    private val promptDao: PromptDao
) : PromptRepository {

    override fun getAllPrompts(): Flow<List<Prompt>> =
        promptDao.getAllPrompts().map { list -> list.map { it.toDomain() } }

    override fun getFavoritePrompts(): Flow<List<Prompt>> =
        promptDao.getFavoritePrompts().map { list -> list.map { it.toDomain() } }

    override fun getAllFolders(): Flow<List<String>> = promptDao.getAllFolders()

    override fun getPromptsByFolder(folder: String): Flow<List<Prompt>> =
        promptDao.getPromptsByFolder(folder).map { list -> list.map { it.toDomain() } }

    override fun searchPrompts(query: String): Flow<List<Prompt>> =
        promptDao.searchPrompts(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getPromptById(id: Long): Prompt? = promptDao.getPromptById(id)?.toDomain()

    override suspend fun createPrompt(prompt: Prompt): Long = promptDao.insertPrompt(prompt.toEntity())

    override suspend fun updatePrompt(prompt: Prompt) = promptDao.updatePrompt(prompt.toEntity())

    override suspend fun deletePrompt(id: Long) = promptDao.deletePromptById(id)

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        promptDao.updateFavoriteStatus(id, isFavorite)

    override suspend fun incrementUseCount(id: Long) = promptDao.incrementUseCount(id)
}
