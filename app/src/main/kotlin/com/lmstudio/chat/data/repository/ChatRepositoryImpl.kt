package com.lmstudio.chat.data.repository

import com.lmstudio.chat.data.local.db.MessageDao
import com.lmstudio.chat.data.local.entities.MessageEntity
import com.lmstudio.chat.data.remote.api.StreamingService
import com.lmstudio.chat.data.remote.dto.ChatMessageDto
import com.lmstudio.chat.data.remote.dto.ChatRequest
import com.lmstudio.chat.domain.model.Message
import com.lmstudio.chat.domain.model.MessageRole
import com.lmstudio.chat.domain.repository.ChatRepository
import com.lmstudio.chat.util.toDomain
import com.lmstudio.chat.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val streamingService: StreamingService
) : ChatRepository {

    override fun getMessages(conversationId: Long): Flow<List<Message>> =
        messageDao.getMessagesForConversation(conversationId).map { list -> list.map { it.toDomain() } }

    override suspend fun getMessagesOnce(conversationId: Long): List<Message> =
        messageDao.getMessagesForConversationOnce(conversationId).map { it.toDomain() }

    override suspend fun sendMessage(conversationId: Long, content: String, modelId: String, images: List<String>): Long =
        messageDao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = "user",
                content = content,
                modelId = modelId,
                images = images.joinToString(",")
            )
        )

    override suspend fun saveAssistantMessage(conversationId: Long, content: String, modelId: String): Long =
        messageDao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = "assistant",
                content = content,
                modelId = modelId
            )
        )

    override suspend fun updateMessage(message: Message) =
        messageDao.updateMessage(message.toEntity())

    override suspend fun deleteMessage(id: Long) =
        messageDao.deleteMessageById(id)

    override suspend fun editMessage(id: Long, newContent: String) =
        messageDao.updateMessageContent(id, newContent)

    override fun streamResponse(
        baseUrl: String,
        apiKey: String,
        modelId: String,
        messages: List<Message>,
        systemPrompt: String,
        temperature: Float,
        topP: Float,
        maxTokens: Int
    ): Flow<String> {
        val chatMessages = mutableListOf<ChatMessageDto>()
        if (systemPrompt.isNotBlank()) {
            chatMessages.add(ChatMessageDto(role = "system", content = systemPrompt))
        }
        messages.forEach { msg ->
            if (!msg.isError) {
                chatMessages.add(
                    ChatMessageDto(
                        role = when (msg.role) {
                            MessageRole.USER -> "user"
                            MessageRole.ASSISTANT -> "assistant"
                            MessageRole.SYSTEM -> "system"
                        },
                        content = msg.content
                    )
                )
            }
        }
        val request = ChatRequest(
            model = modelId,
            messages = chatMessages,
            temperature = temperature,
            topP = topP,
            maxTokens = maxTokens,
            stream = true
        )
        return streamingService.streamChat(baseUrl, apiKey, request)
    }
}
