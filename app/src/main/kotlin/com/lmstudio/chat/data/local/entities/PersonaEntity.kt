package com.lmstudio.chat.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "icon") val icon: String = "smart_toy",
    @ColumnInfo(name = "system_prompt") val systemPrompt: String = "",
    @ColumnInfo(name = "temperature") val temperature: Float = 0.7f,
    @ColumnInfo(name = "top_p") val topP: Float = 0.9f,
    @ColumnInfo(name = "max_tokens") val maxTokens: Int = 2048,
    @ColumnInfo(name = "context_length") val contextLength: Int = 4096,
    @ColumnInfo(name = "color") val color: String = "#19C37D",
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "categories") val categories: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_builtin") val isBuiltin: Boolean = false,
    @ColumnInfo(name = "sort_order") val sortOrder: Int = 0
)
