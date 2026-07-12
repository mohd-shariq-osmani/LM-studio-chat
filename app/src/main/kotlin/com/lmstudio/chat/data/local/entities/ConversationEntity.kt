package com.lmstudio.chat.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") val title: String = "New Chat",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "model_id") val modelId: String = "",
    @ColumnInfo(name = "persona_id") val personaId: Long = -1L,
    @ColumnInfo(name = "is_pinned") val isPinned: Boolean = false,
    @ColumnInfo(name = "is_archived") val isArchived: Boolean = false,
    @ColumnInfo(name = "message_count") val messageCount: Int = 0,
    @ColumnInfo(name = "last_message_preview") val lastMessagePreview: String = "",
    @ColumnInfo(name = "token_count") val tokenCount: Int = 0,
    @ColumnInfo(name = "folder") val folder: String = ""
)
