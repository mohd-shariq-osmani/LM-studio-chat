package com.lmstudio.chat.data.repository

import com.lmstudio.chat.data.local.db.ConversationDao
import com.lmstudio.chat.domain.model.Conversation
import com.lmstudio.chat.domain.repository.ConversationRepository
import com.lmstudio.chat.util.toDomain
import com.lmstudio.chat.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao
) : ConversationRepository {

    override fun getAllConversations(): Flow<List<Conversation>> =
        conversationDao.getAllConversations().map { list -> list.map { it.toDomain() } }

    override fun getPinnedConversations(): Flow<List<Conversation>> =
        conversationDao.getPinnedConversations().map { list -> list.map { it.toDomain() } }

    override fun getRecentConversations(): Flow<List<Conversation>> =
        conversationDao.getRecentConversations().map { list -> list.map { it.toDomain() } }

    override fun searchConversations(query: String): Flow<List<Conversation>> =
        conversationDao.searchConversations(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getConversationById(id: Long): Conversation? =
        conversationDao.getConversationById(id)?.toDomain()

    override suspend fun createConversation(conversation: Conversation): Long =
        conversationDao.insertConversation(conversation.toEntity())

    override suspend fun updateConversation(conversation: Conversation) =
        conversationDao.updateConversation(conversation.toEntity())

    override suspend fun deleteConversation(id: Long) =
        conversationDao.deleteConversationById(id)

    override suspend fun pinConversation(id: Long, pinned: Boolean) =
        conversationDao.updatePinStatus(id, pinned)

    override suspend fun archiveConversation(id: Long, archived: Boolean) =
        conversationDao.updateArchiveStatus(id, archived)

    override suspend fun renameConversation(id: Long, title: String) =
        conversationDao.updateTitle(id, title)

    override suspend fun updateConversationMeta(id: Long, preview: String) =
        conversationDao.updateConversationMeta(id, System.currentTimeMillis(), preview)
}
