package com.lmstudio.chat.ui.prompts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmstudio.chat.domain.model.Prompt
import com.lmstudio.chat.domain.repository.PromptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromptsViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptsUiState())
    val uiState: StateFlow<PromptsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            promptRepository.getAllPrompts().collectLatest { list ->
                _uiState.update { it.copy(prompts = list, isLoading = false) }
            }
        }
    }

    fun searchPrompts(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                promptRepository.getAllPrompts().collectLatest { list ->
                    _uiState.update { it.copy(prompts = list) }
                }
            } else {
                promptRepository.searchPrompts(query).collectLatest { list ->
                    _uiState.update { it.copy(prompts = list) }
                }
            }
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            promptRepository.toggleFavorite(id, isFavorite)
        }
    }

    fun savePrompt(title: String, content: String) {
        viewModelScope.launch {
            val newPrompt = Prompt(title = title, content = content)
            promptRepository.createPrompt(newPrompt)
        }
    }

    fun deletePrompt(id: Long) {
        viewModelScope.launch {
            promptRepository.deletePrompt(id)
        }
    }
}

data class PromptsUiState(
    val prompts: List<Prompt> = emptyList(),
    val isLoading: Boolean = true
)
