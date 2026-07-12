package com.lmstudio.chat.data.local.db

import androidx.room.*
import com.lmstudio.chat.data.local.entities.PersonaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaDao {

    @Query("SELECT * FROM personas ORDER BY is_favorite DESC, sort_order ASC, name ASC")
    fun getAllPersonas(): Flow<List<PersonaEntity>>

    @Query("SELECT * FROM personas WHERE is_favorite = 1 ORDER BY name ASC")
    fun getFavoritePersonas(): Flow<List<PersonaEntity>>

    @Query("SELECT * FROM personas WHERE is_default = 1 LIMIT 1")
    suspend fun getDefaultPersona(): PersonaEntity?

    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun getPersonaById(id: Long): PersonaEntity?

    @Query("SELECT * FROM personas WHERE id = :id")
    fun getPersonaByIdFlow(id: Long): Flow<PersonaEntity?>

    @Query("SELECT * FROM personas WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchPersonas(query: String): Flow<List<PersonaEntity>>

    @Query("SELECT * FROM personas WHERE categories LIKE '%' || :category || '%' ORDER BY name ASC")
    fun getPersonasByCategory(category: String): Flow<List<PersonaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersona(persona: PersonaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonas(personas: List<PersonaEntity>)

    @Update
    suspend fun updatePersona(persona: PersonaEntity)

    @Delete
    suspend fun deletePersona(persona: PersonaEntity)

    @Query("DELETE FROM personas WHERE id = :id")
    suspend fun deletePersonaById(id: Long)

    @Query("UPDATE personas SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("UPDATE personas SET is_default = 0")
    suspend fun clearDefaultPersona()

    @Query("UPDATE personas SET is_default = 1 WHERE id = :id")
    suspend fun setDefaultPersona(id: Long)

    @Query("SELECT COUNT(*) FROM personas")
    suspend fun getPersonaCount(): Int
}
