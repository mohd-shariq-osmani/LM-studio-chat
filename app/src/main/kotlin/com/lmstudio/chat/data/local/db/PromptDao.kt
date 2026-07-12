package com.lmstudio.chat.data.local.db

import androidx.room.*
import com.lmstudio.chat.data.local.entities.PromptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {

    @Query("SELECT * FROM prompts ORDER BY is_favorite DESC, use_count DESC, title ASC")
    fun getAllPrompts(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE is_favorite = 1 ORDER BY title ASC")
    fun getFavoritePrompts(): Flow<List<PromptEntity>>

    @Query("SELECT DISTINCT folder FROM prompts ORDER BY folder ASC")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT * FROM prompts WHERE folder = :folder ORDER BY title ASC")
    fun getPromptsByFolder(folder: String): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getPromptById(id: Long): PromptEntity?

    @Query("SELECT * FROM prompts WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchPrompts(query: String): Flow<List<PromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptEntity): Long

    @Update
    suspend fun updatePrompt(prompt: PromptEntity)

    @Delete
    suspend fun deletePrompt(prompt: PromptEntity)

    @Query("DELETE FROM prompts WHERE id = :id")
    suspend fun deletePromptById(id: Long)

    @Query("UPDATE prompts SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("UPDATE prompts SET use_count = use_count + 1 WHERE id = :id")
    suspend fun incrementUseCount(id: Long)
}
