package com.lmstudio.chat.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmstudio.chat.data.local.db.MessageDao
import com.lmstudio.chat.domain.model.Message
import com.lmstudio.chat.domain.repository.ConversationRepository
import com.lmstudio.chat.util.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val messageDao: MessageDao,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList()) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val messages = messageDao.searchMessages(query).map { it.toDomain() }
            _uiState.update { it.copy(results = messages, isLoading = false) }
        }
    }
}

data class SearchUiState(
    val results: List<Message> = emptyList(),
    val isLoading: Boolean = false
)
// SearchScreen file logic
