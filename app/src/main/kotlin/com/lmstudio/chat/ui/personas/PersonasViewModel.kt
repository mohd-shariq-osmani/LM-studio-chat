package com.lmstudio.chat.ui.personas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmstudio.chat.domain.model.Persona
import com.lmstudio.chat.domain.repository.PersonaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonasViewModel @Inject constructor(
    val personaRepository: PersonaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonasUiState())
    val uiState: StateFlow<PersonasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            personaRepository.getAllPersonas().collectLatest { list ->
                _uiState.update { it.copy(personas = list, isLoading = false) }
            }
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            personaRepository.toggleFavorite(id, isFavorite)
        }
    }

    fun setDefaultPersona(id: Long) {
        viewModelScope.launch {
            personaRepository.setDefault(id)
        }
    }

    fun deletePersona(id: Long) {
        viewModelScope.launch {
            personaRepository.deletePersona(id)
        }
    }

    fun duplicatePersona(id: Long) {
        viewModelScope.launch {
            personaRepository.duplicatePersona(id)
        }
    }

    fun searchPersonas(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                personaRepository.getAllPersonas().collectLatest { list ->
                    _uiState.update { it.copy(personas = list) }
                }
            } else {
                personaRepository.searchPersonas(query).collectLatest { list ->
                    _uiState.update { it.copy(personas = list) }
                }
            }
        }
    }
}

data class PersonasUiState(
    val personas: List<Persona> = emptyList(),
    val isLoading: Boolean = true
)
