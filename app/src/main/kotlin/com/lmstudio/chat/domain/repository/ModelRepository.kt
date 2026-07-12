package com.lmstudio.chat.domain.repository

import com.lmstudio.chat.domain.model.ModelInfo

interface ModelRepository {
    suspend fun getAvailableModels(baseUrl: String, apiKey: String): Result<List<ModelInfo>>
}
