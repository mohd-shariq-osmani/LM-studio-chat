package com.lmstudio.chat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmstudio.chat.domain.model.Conversation
import com.lmstudio.chat.domain.model.Message
import com.lmstudio.chat.domain.model.MessageRole
import com.lmstudio.chat.domain.model.ModelInfo
import com.lmstudio.chat.domain.model.Persona
import com.lmstudio.chat.domain.repository.ChatRepository
import com.lmstudio.chat.domain.repository.ConversationRepository
import com.lmstudio.chat.domain.repository.ModelRepository
import com.lmstudio.chat.domain.repository.PersonaRepository
import com.lmstudio.chat.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val conversationRepository: ConversationRepository,
    private val personaRepository: PersonaRepository,
    private val settingsRepository: SettingsRepository,
    private val modelRepository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var streamingJob: Job? = null

    init {
        // Fetch Settings & Models
        viewModelScope.launch {
            settingsRepository.getSettings().collectLatest { settings ->
                _uiState.update {
                    it.copy(
                        baseUrl = settings.baseUrl,
                        apiKey = settings.apiKey,
                        selectedModel = settings.lastModel,
                        temperature = settings.temperature,
                        topP = settings.topP,
                        maxTokens = settings.maxTokens
                    )
                }
                loadModels()
            }
        }

        // Fetch Personas
        viewModelScope.launch {
            personaRepository.getAllPersonas().collectLatest { personas ->
                _uiState.update { it.copy(personas = personas) }
            }
        }
    }

    fun setConversation(id: Long?) {
        if (id == null) {
            viewModelScope.launch {
                val defaultPersona = personaRepository.getDefaultPersona()
                _uiState.update {
                    it.copy(
                        conversationId = null,
                        conversationTitle = "New Chat",
                        messages = emptyList(),
                        selectedPersona = defaultPersona
                    )
                }
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(conversationId = id) }
            // Get conversation details
            val conversation = conversationRepository.getConversationById(id)
            if (conversation != null) {
                val persona = personaRepository.getPersonaById(conversation.personaId)
                _uiState.update {
                    it.copy(
                        conversationTitle = conversation.title,
                        selectedModel = conversation.modelId,
                        selectedPersona = persona
                    )
                }
            }

            // Observe messages
            chatRepository.getMessages(id).collectLatest { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun renameConversation(newTitle: String) {
        val id = _uiState.value.conversationId ?: return
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            conversationRepository.renameConversation(id, newTitle)
            _uiState.update { it.copy(conversationTitle = newTitle) }
        }
    }

    fun exportChatText(): String {
        val state = _uiState.value
        val sb = java.lang.StringBuilder()
        sb.appendLine("LM Studio Chat Export")
        sb.appendLine("========================================")
        sb.appendLine("Title: ${state.conversationTitle}")
        sb.appendLine("Model: ${state.selectedModel}")
        state.selectedPersona?.let { sb.appendLine("Persona: ${it.name}") }
        sb.appendLine("Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
        sb.appendLine("========================================\n")

        for (msg in state.messages) {
            val roleLabel = when (msg.role) {
                MessageRole.USER -> "[USER]"
                MessageRole.ASSISTANT -> "[ASSISTANT]"
                MessageRole.SYSTEM -> "[SYSTEM]"
            }
            sb.appendLine("$roleLabel")
            sb.appendLine(msg.content)
            sb.appendLine()
        }
        return sb.toString()
    }

    private fun loadModels() {
        viewModelScope.launch {
            val state = _uiState.value
            val result = modelRepository.getAvailableModels(state.baseUrl, state.apiKey)
            result.onSuccess { models ->
                _uiState.update {
                    it.copy(
                        models = models,
                        selectedModel = state.selectedModel.ifBlank { models.firstOrNull()?.id ?: "" }
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to load models") }
            }
        }
    }

    fun selectModel(modelId: String) {
        viewModelScope.launch {
            settingsRepository.updateLastModel(modelId)
            _uiState.update { it.copy(selectedModel = modelId) }
            _uiState.value.conversationId?.let { id ->
                val conversation = conversationRepository.getConversationById(id)
                if (conversation != null) {
                    conversationRepository.updateConversation(conversation.copy(modelId = modelId))
                }
            }
        }
    }

    fun selectPersona(persona: Persona) {
        _uiState.update { it.copy(selectedPersona = persona) }
        viewModelScope.launch {
            _uiState.value.conversationId?.let { id ->
                val conversation = conversationRepository.getConversationById(id)
                if (conversation != null) {
                    conversationRepository.updateConversation(conversation.copy(personaId = persona.id))
                }
            }
        }
    }

    fun sendMessage(content: String, images: List<String> = emptyList()) {
        if (content.isBlank() && images.isEmpty()) return

        viewModelScope.launch {
            var activeId = _uiState.value.conversationId
            if (activeId == null) {
                // Create conversation first
                val defaultPersona = _uiState.value.selectedPersona ?: personaRepository.getDefaultPersona()
                val newChat = Conversation(
                    title = content.take(30).ifBlank { "Image Chat" },
                    modelId = _uiState.value.selectedModel,
                    personaId = defaultPersona?.id ?: -1L
                )
                activeId = conversationRepository.createConversation(newChat)
                setConversation(activeId)
            }

            val conversationId = activeId ?: return@launch

            // Save User message
            chatRepository.sendMessage(conversationId, content, _uiState.value.selectedModel, images)
            conversationRepository.updateConversationMeta(conversationId, content)

            // Stream response
            generateResponse(conversationId)
        }
    }

    fun regenerateResponse() {
        val conversationId = _uiState.value.conversationId ?: return
        val messages = _uiState.value.messages
        if (messages.isEmpty()) return

        viewModelScope.launch {
            // Delete the last assistant message if exists
            val lastMsg = messages.last()
            if (lastMsg.role == MessageRole.ASSISTANT) {
                chatRepository.deleteMessage(lastMsg.id)
            }
            generateResponse(conversationId)
        }
    }

    fun stopGeneration() {
        streamingJob?.cancel()
        _uiState.update { it.copy(isGenerating = false) }
        viewModelScope.launch {
            val messages = _uiState.value.messages
            if (messages.isNotEmpty()) {
                val lastMsg = messages.last()
                if (lastMsg.isStreaming) {
                    chatRepository.updateMessage(lastMsg.copy(isStreaming = false))
                }
            }
        }
    }

    fun editMessage(messageId: Long, newContent: String) {
        viewModelScope.launch {
            chatRepository.editMessage(messageId, newContent)
            // If user message is edited, delete all subsequent messages and regenerate
            val conversationId = _uiState.value.conversationId ?: return@launch
            val messages = chatRepository.getMessagesOnce(conversationId)
            val index = messages.indexOfFirst { it.id == messageId }
            if (index != -1) {
                for (i in (index + 1) until messages.size) {
                    chatRepository.deleteMessage(messages[i].id)
                }
                generateResponse(conversationId)
            }
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            chatRepository.deleteMessage(messageId)
        }
    }

    private fun generateResponse(conversationId: Long) {
        streamingJob?.cancel()
        _uiState.update { it.copy(isGenerating = true, error = "") }

        streamingJob = viewModelScope.launch {
            val state = _uiState.value
            val history = chatRepository.getMessagesOnce(conversationId)
            val systemPrompt = state.selectedPersona?.systemPrompt ?: ""

            // Create empty assistant response message
            val responseId = chatRepository.saveAssistantMessage(conversationId, "", state.selectedModel)
            var responseText = ""

            chatRepository.streamResponse(
                baseUrl = state.baseUrl,
                apiKey = state.apiKey,
                modelId = state.selectedModel,
                messages = history,
                systemPrompt = systemPrompt,
                temperature = state.selectedPersona?.temperature ?: state.temperature,
                topP = state.selectedPersona?.topP ?: state.topP,
                maxTokens = state.selectedPersona?.maxTokens ?: state.maxTokens
            )
            .flowOn(kotlinx.coroutines.Dispatchers.IO)
            .catch { err ->
                _uiState.update { it.copy(isGenerating = false, error = err.message ?: "Stream error") }
                chatRepository.updateMessage(
                    Message(
                        id = responseId,
                        conversationId = conversationId,
                        role = MessageRole.ASSISTANT,
                        content = responseText,
                        isError = true,
                        errorMessage = err.message ?: "Stream error"
                    )
                )
            }.collect { chunk ->
                responseText += chunk
                chatRepository.updateMessage(
                    Message(
                        id = responseId,
                        conversationId = conversationId,
                        role = MessageRole.ASSISTANT,
                        content = responseText,
                        isStreaming = true
                    )
                )
            }

            // Stream complete
            _uiState.update { it.copy(isGenerating = false) }
            chatRepository.updateMessage(
                Message(
                    id = responseId,
                    conversationId = conversationId,
                    role = MessageRole.ASSISTANT,
                    content = responseText,
                    isStreaming = false
                )
            )
            conversationRepository.updateConversationMeta(conversationId, responseText)
        }
    }
}

data class ChatUiState(
    val conversationId: Long? = null,
    val conversationTitle: String = "New Chat",
    val messages: List<Message> = emptyList(),
    val models: List<ModelInfo> = emptyList(),
    val selectedModel: String = "",
    val selectedPersona: Persona? = null,
    val personas: List<Persona> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String = "",
    val baseUrl: String = "",
    val apiKey: String = "",
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val maxTokens: Int = 2048
)
