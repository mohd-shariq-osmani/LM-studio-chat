package com.lmstudio.chat.data.repository

import com.lmstudio.chat.data.remote.api.LmStudioApiService
import com.lmstudio.chat.domain.model.ModelInfo
import com.lmstudio.chat.domain.repository.ModelRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepositoryImpl @Inject constructor(
    private val apiService: LmStudioApiService
) : ModelRepository {

    override suspend fun getAvailableModels(baseUrl: String, apiKey: String): Result<List<ModelInfo>> {
        return try {
            val auth = if (apiKey.isNotEmpty()) "Bearer $apiKey" else ""
            var normalizedBase = baseUrl.trimEnd('/')
            if (!normalizedBase.endsWith("/v1") && !normalizedBase.endsWith("/v1/")) {
                normalizedBase = "$normalizedBase/v1"
            }
            val url = "$normalizedBase/models"
            val response = apiService.getModels(url, auth)
            if (response.isSuccessful) {
                val models = response.body()?.data?.map { dto ->
                    ModelInfo(
                        id = dto.id,
                        name = dto.id.substringAfterLast("/").substringAfterLast("\\"),
                        contextLength = dto.contextLength ?: dto.maxContextLength,
                        ownedBy = dto.ownedBy,
                        isLoaded = true
                    )
                } ?: emptyList()
                Result.success(models)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
