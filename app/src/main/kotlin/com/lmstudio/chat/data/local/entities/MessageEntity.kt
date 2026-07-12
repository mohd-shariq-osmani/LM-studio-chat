package com.lmstudio.chat.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = ConversationEntity::class,
        parentColumns = ["id"],
        childColumns = ["conversation_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["conversation_id"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "conversation_id") val conversationId: Long,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_streaming") val isStreaming: Boolean = false,
    @ColumnInfo(name = "is_error") val isError: Boolean = false,
    @ColumnInfo(name = "error_message") val errorMessage: String = "",
    @ColumnInfo(name = "token_count") val tokenCount: Int = 0,
    @ColumnInfo(name = "model_id") val modelId: String = "",
    @ColumnInfo(name = "finish_reason") val finishReason: String = "",
    @ColumnInfo(name = "is_edited") val isEdited: Boolean = false,
    @ColumnInfo(name = "images") val images: String = "" // Comma-separated list of local URIs
)
