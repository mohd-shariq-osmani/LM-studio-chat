package com.lmstudio.chat.domain.repository

import com.lmstudio.chat.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateBaseUrl(url: String)
    suspend fun updateApiKey(key: String)
    suspend fun updateTemperature(value: Float)
    suspend fun updateTopP(value: Float)
    suspend fun updateMaxTokens(value: Int)
    suspend fun updateLastModel(modelId: String)
    suspend fun updateTimeoutSeconds(value: Int)
    suspend fun updateSendOnEnter(value: Boolean)
    suspend fun updateShowTokenCount(value: Boolean)
    suspend fun updateStreamResponses(value: Boolean)
    suspend fun updateAppLockEnabled(value: Boolean)
}
