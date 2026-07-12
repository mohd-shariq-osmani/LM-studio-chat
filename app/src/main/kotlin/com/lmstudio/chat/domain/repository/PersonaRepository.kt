package com.lmstudio.chat.domain.repository

import com.lmstudio.chat.domain.model.Persona
import kotlinx.coroutines.flow.Flow

interface PersonaRepository {
    fun getAllPersonas(): Flow<List<Persona>>
    fun getFavoritePersonas(): Flow<List<Persona>>
    fun searchPersonas(query: String): Flow<List<Persona>>
    suspend fun getPersonaById(id: Long): Persona?
    suspend fun getDefaultPersona(): Persona?
    suspend fun createPersona(persona: Persona): Long
    suspend fun updatePersona(persona: Persona)
    suspend fun deletePersona(id: Long)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun setDefault(id: Long)
    suspend fun duplicatePersona(id: Long): Long
    suspend fun seedDefaultPersonas()
    suspend fun getPersonaCount(): Int
    fun exportPersona(persona: Persona): String
    fun importPersona(json: String): Persona?
}
