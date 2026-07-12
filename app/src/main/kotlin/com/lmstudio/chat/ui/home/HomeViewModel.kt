package com.lmstudio.chat.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmstudio.chat.domain.model.Conversation
import com.lmstudio.chat.domain.model.ModelInfo
import com.lmstudio.chat.domain.model.Persona
import com.lmstudio.chat.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val personaRepository: PersonaRepository,
    private val modelRepository: ModelRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed defaults if empty
            personaRepository.seedDefaultPersonas()

            // Observe recent chats
            conversationRepository.getRecentConversations().collectLatest { chats ->
                _uiState.update { it.copy(recentChats = chats) }
            }
        }

        viewModelScope.launch {
            // Observe favorite personas
            personaRepository.getFavoritePersonas().collectLatest { personas ->
                _uiState.update { it.copy(favoritePersonas = personas) }
            }
        }

        viewModelScope.launch {
            // Fetch and set models
            settingsRepository.getSettings().collectLatest { settings ->
                _uiState.update { it.copy(isLoadingModels = true, baseUrl = settings.baseUrl) }
                val result = modelRepository.getAvailableModels(settings.baseUrl, settings.apiKey)
                result.onSuccess { models ->
                    _uiState.update {
                        it.copy(
                            models = models,
                            selectedModel = settings.lastModel.ifBlank { models.firstOrNull()?.id ?: "" },
                            isLoadingModels = false,
                            error = ""
                        )
                    }
                }.onFailure { err ->
                    _uiState.update { it.copy(isLoadingModels = false, error = err.message ?: "Failed to connect") }
                }
            }
        }
    }

    fun selectModel(modelId: String) {
        viewModelScope.launch {
            settingsRepository.updateLastModel(modelId)
            _uiState.update { it.copy(selectedModel = modelId) }
        }
    }

    fun startNewChat(onChatCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val defaultPersona = personaRepository.getDefaultPersona()
            val newChat = Conversation(
                title = "New Chat",
                modelId = _uiState.value.selectedModel,
                personaId = defaultPersona?.id ?: -1L
            )
            val newId = conversationRepository.createConversation(newChat)
            onChatCreated(newId)
        }
    }
}

data class HomeUiState(
    val recentChats: List<Conversation> = emptyList(),
    val favoritePersonas: List<Persona> = emptyList(),
    val models: List<ModelInfo> = emptyList(),
    val selectedModel: String = "",
    val isLoadingModels: Boolean = false,
    val error: String = "",
    val baseUrl: String = ""
)
