package com.lmstudio.chat.util

import com.lmstudio.chat.data.local.entities.ConversationEntity
import com.lmstudio.chat.data.local.entities.MessageEntity
import com.lmstudio.chat.data.local.entities.PersonaEntity
import com.lmstudio.chat.data.local.entities.PromptEntity
import com.lmstudio.chat.domain.model.*

fun ConversationEntity.toDomain() = Conversation(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    modelId = modelId,
    personaId = personaId,
    isPinned = isPinned,
    isArchived = isArchived,
    messageCount = messageCount,
    lastMessagePreview = lastMessagePreview,
    tokenCount = tokenCount,
    folder = folder
)

fun Conversation.toEntity() = ConversationEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    modelId = modelId,
    personaId = personaId,
    isPinned = isPinned,
    isArchived = isArchived,
    messageCount = messageCount,
    lastMessagePreview = lastMessagePreview,
    tokenCount = tokenCount,
    folder = folder
)

fun MessageEntity.toDomain() = Message(
    id = id,
    conversationId = conversationId,
    role = when (role) {
        "user" -> MessageRole.USER
        "assistant" -> MessageRole.ASSISTANT
        "system" -> MessageRole.SYSTEM
        else -> MessageRole.USER
    },
    content = content,
    createdAt = createdAt,
    isStreaming = isStreaming,
    isError = isError,
    errorMessage = errorMessage,
    tokenCount = tokenCount,
    modelId = modelId,
    finishReason = finishReason,
    isEdited = isEdited,
    images = if (images.isBlank()) emptyList() else images.split(",")
)

fun Message.toEntity() = MessageEntity(
    id = id,
    conversationId = conversationId,
    role = when (role) {
        MessageRole.USER -> "user"
        MessageRole.ASSISTANT -> "assistant"
        MessageRole.SYSTEM -> "system"
    },
    content = content,
    createdAt = createdAt,
    isStreaming = isStreaming,
    isError = isError,
    errorMessage = errorMessage,
    tokenCount = tokenCount,
    modelId = modelId,
    finishReason = finishReason,
    isEdited = isEdited,
    images = images.joinToString(",")
)

fun PersonaEntity.toDomain() = Persona(
    id = id,
    name = name,
    description = description,
    icon = icon,
    systemPrompt = systemPrompt,
    temperature = temperature,
    topP = topP,
    maxTokens = maxTokens,
    contextLength = contextLength,
    color = color,
    isFavorite = isFavorite,
    isDefault = isDefault,
    categories = if (categories.isBlank()) emptyList() else categories.split(",").map { it.trim() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    isBuiltin = isBuiltin,
    sortOrder = sortOrder
)

fun Persona.toEntity() = PersonaEntity(
    id = id,
    name = name,
    description = description,
    icon = icon,
    systemPrompt = systemPrompt,
    temperature = temperature,
    topP = topP,
    maxTokens = maxTokens,
    contextLength = contextLength,
    color = color,
    isFavorite = isFavorite,
    isDefault = isDefault,
    categories = categories.joinToString(","),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isBuiltin = isBuiltin,
    sortOrder = sortOrder
)

fun PromptEntity.toDomain() = Prompt(
    id = id,
    title = title,
    content = content,
    description = description,
    folder = folder,
    isFavorite = isFavorite,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
    variables = if (variables.isBlank()) emptyList() else variables.split(",").map { it.trim() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    useCount = useCount
)

fun Prompt.toEntity() = PromptEntity(
    id = id,
    title = title,
    content = content,
    description = description,
    folder = folder,
    isFavorite = isFavorite,
    tags = tags.joinToString(","),
    variables = variables.joinToString(","),
    createdAt = createdAt,
    updatedAt = updatedAt,
    useCount = useCount
)

fun String.truncate(maxLength: Int, suffix: String = "..."): String =
    if (length <= maxLength) this else take(maxLength - suffix.length) + suffix
