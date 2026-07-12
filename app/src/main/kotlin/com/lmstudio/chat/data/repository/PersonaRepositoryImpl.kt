package com.lmstudio.chat.data.repository

import com.google.gson.Gson
import com.lmstudio.chat.data.local.db.PersonaDao
import com.lmstudio.chat.domain.model.Persona
import com.lmstudio.chat.domain.repository.PersonaRepository
import com.lmstudio.chat.util.DefaultPersonas
import com.lmstudio.chat.util.toDomain
import com.lmstudio.chat.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonaRepositoryImpl @Inject constructor(
    private val personaDao: PersonaDao,
    private val gson: Gson
) : PersonaRepository {

    override fun getAllPersonas(): Flow<List<Persona>> =
        personaDao.getAllPersonas().map { list -> list.map { it.toDomain() } }

    override fun getFavoritePersonas(): Flow<List<Persona>> =
        personaDao.getFavoritePersonas().map { list -> list.map { it.toDomain() } }

    override fun searchPersonas(query: String): Flow<List<Persona>> =
        personaDao.searchPersonas(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getPersonaById(id: Long): Persona? =
        personaDao.getPersonaById(id)?.toDomain()

    override suspend fun getDefaultPersona(): Persona? =
        personaDao.getDefaultPersona()?.toDomain()

    override suspend fun createPersona(persona: Persona): Long =
        personaDao.insertPersona(persona.toEntity())

    override suspend fun updatePersona(persona: Persona) =
        personaDao.updatePersona(persona.toEntity())

    override suspend fun deletePersona(id: Long) =
        personaDao.deletePersonaById(id)

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        personaDao.updateFavoriteStatus(id, isFavorite)

    override suspend fun setDefault(id: Long) {
        personaDao.clearDefaultPersona()
        personaDao.setDefaultPersona(id)
    }

    override suspend fun duplicatePersona(id: Long): Long {
        val persona = personaDao.getPersonaById(id) ?: return -1L
        val duplicate = persona.copy(
            id = 0,
            name = "${persona.name} (Copy)",
            isDefault = false,
            isBuiltin = false
        )
        return personaDao.insertPersona(duplicate)
    }

    override suspend fun seedDefaultPersonas() {
        val count = personaDao.getPersonaCount()
        if (count == 0) {
            personaDao.insertPersonas(DefaultPersonas.getDefaultPersonas())
        }
    }

    override suspend fun getPersonaCount(): Int = personaDao.getPersonaCount()

    override fun exportPersona(persona: Persona): String = gson.toJson(persona)

    override fun importPersona(json: String): Persona? = try {
        gson.fromJson(json, Persona::class.java)
    } catch (e: Exception) {
        null
    }
}
