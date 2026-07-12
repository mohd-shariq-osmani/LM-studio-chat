package com.lmstudio.chat.data.local.db

import androidx.room.*
import com.lmstudio.chat.data.local.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    suspend fun getMessagesForConversationOnce(conversationId: Long): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: Long): MessageEntity?

    @Query("SELECT * FROM messages WHERE content LIKE '%' || :query || '%' ORDER BY created_at DESC LIMIT 50")
    suspend fun searchMessages(query: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("DELETE FROM messages WHERE conversation_id = :conversationId")
    suspend fun deleteAllMessagesForConversation(conversationId: Long)

    @Query("UPDATE messages SET content = :content, is_edited = 1 WHERE id = :id")
    suspend fun updateMessageContent(id: Long, content: String)

    @Query("UPDATE messages SET is_streaming = :isStreaming WHERE id = :id")
    suspend fun updateStreamingStatus(id: Long, isStreaming: Boolean)

    @Query("SELECT COUNT(*) FROM messages WHERE conversation_id = :conversationId")
    suspend fun getMessageCount(conversationId: Long): Int

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentMessages(conversationId: Long, limit: Int): List<MessageEntity>
}
