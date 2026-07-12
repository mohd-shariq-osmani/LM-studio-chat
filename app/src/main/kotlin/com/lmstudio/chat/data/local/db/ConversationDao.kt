package com.lmstudio.chat.data.local.db

import androidx.room.*
import com.lmstudio.chat.data.local.entities.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversations WHERE is_archived = 0 ORDER BY is_pinned DESC, updated_at DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE is_pinned = 1 AND is_archived = 0 ORDER BY updated_at DESC")
    fun getPinnedConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE is_archived = 1 ORDER BY updated_at DESC")
    fun getArchivedConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE is_archived = 0 ORDER BY updated_at DESC LIMIT 5")
    fun getRecentConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversationByIdFlow(id: Long): Flow<ConversationEntity?>

    @Query("SELECT * FROM conversations WHERE title LIKE '%' || :query || '%' AND is_archived = 0 ORDER BY updated_at DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Query("UPDATE conversations SET is_pinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean)

    @Query("UPDATE conversations SET is_archived = :isArchived WHERE id = :id")
    suspend fun updateArchiveStatus(id: Long, isArchived: Boolean)

    @Query("UPDATE conversations SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("UPDATE conversations SET updated_at = :updatedAt, last_message_preview = :preview WHERE id = :id")
    suspend fun updateConversationMeta(id: Long, updatedAt: Long, preview: String)

    @Query("SELECT COUNT(*) FROM conversations WHERE is_archived = 0")
    suspend fun getConversationCount(): Int
}
