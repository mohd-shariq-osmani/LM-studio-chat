package com.lmstudio.chat.ui.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmstudio.chat.domain.model.Conversation
import com.lmstudio.chat.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationsUiState())
    val uiState: StateFlow<ConversationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            conversationRepository.getAllConversations().collectLatest { list ->
                _uiState.update { it.copy(conversations = list, isLoading = false) }
            }
        }
    }

    fun searchConversations(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                conversationRepository.getAllConversations().collectLatest { list ->
                    _uiState.update { it.copy(conversations = list) }
                }
            } else {
                conversationRepository.searchConversations(query).collectLatest { list ->
                    _uiState.update { it.copy(conversations = list) }
                }
            }
        }
    }

    fun pinConversation(id: Long, isPinned: Boolean) {
        viewModelScope.launch {
            conversationRepository.pinConversation(id, isPinned)
        }
    }

    fun archiveConversation(id: Long, isArchived: Boolean) {
        viewModelScope.launch {
            conversationRepository.archiveConversation(id, isArchived)
        }
    }

    fun renameConversation(id: Long, newTitle: String) {
        viewModelScope.launch {
            conversationRepository.renameConversation(id, newTitle)
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            conversationRepository.deleteConversation(id)
        }
    }
}

data class ConversationsUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = true
)
