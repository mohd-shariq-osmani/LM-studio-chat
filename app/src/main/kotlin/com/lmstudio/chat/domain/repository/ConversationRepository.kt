package com.lmstudio.chat.domain.repository

import com.lmstudio.chat.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getAllConversations(): Flow<List<Conversation>>
    fun getPinnedConversations(): Flow<List<Conversation>>
    fun getRecentConversations(): Flow<List<Conversation>>
    fun searchConversations(query: String): Flow<List<Conversation>>
    suspend fun getConversationById(id: Long): Conversation?
    suspend fun createConversation(conversation: Conversation): Long
    suspend fun updateConversation(conversation: Conversation)
    suspend fun deleteConversation(id: Long)
    suspend fun pinConversation(id: Long, pinned: Boolean)
    suspend fun archiveConversation(id: Long, archived: Boolean)
    suspend fun renameConversation(id: Long, title: String)
    suspend fun updateConversationMeta(id: Long, preview: String)
}
