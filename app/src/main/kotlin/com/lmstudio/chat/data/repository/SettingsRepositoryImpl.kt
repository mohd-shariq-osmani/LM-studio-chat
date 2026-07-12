package com.lmstudio.chat.data.repository

import com.lmstudio.chat.data.local.datastore.SettingsDataStore
import com.lmstudio.chat.domain.model.AppSettings
import com.lmstudio.chat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> = combine(
        dataStore.baseUrl,
        dataStore.apiKey,
        dataStore.temperature,
        dataStore.topP,
        dataStore.maxTokens,
        dataStore.lastModel,
        dataStore.timeoutSeconds,
        dataStore.sendOnEnter,
        dataStore.showTokenCount,
        dataStore.streamResponses,
        dataStore.appLockEnabled
    ) { arr: Array<Any?> ->
        AppSettings(
            baseUrl = arr[0] as? String ?: "http://192.168.0.50:1234/v1",
            apiKey = arr[1] as? String ?: "",
            temperature = arr[2] as? Float ?: 0.7f,
            topP = arr[3] as? Float ?: 0.9f,
            maxTokens = arr[4] as? Int ?: 2048,
            lastModel = arr[5] as? String ?: "",
            timeoutSeconds = arr[6] as? Int ?: 120,
            sendOnEnter = arr[7] as? Boolean ?: true,
            showTokenCount = arr[8] as? Boolean ?: true,
            streamResponses = arr[9] as? Boolean ?: true,
            appLockEnabled = arr[10] as? Boolean ?: false
        )
    }

    override suspend fun updateBaseUrl(url: String) = dataStore.setBaseUrl(url)
    override suspend fun updateApiKey(key: String) = dataStore.setApiKey(key)
    override suspend fun updateTemperature(value: Float) = dataStore.setTemperature(value)
    override suspend fun updateTopP(value: Float) = dataStore.setTopP(value)
    override suspend fun updateMaxTokens(value: Int) = dataStore.setMaxTokens(value)
    override suspend fun updateLastModel(modelId: String) = dataStore.setLastModel(modelId)
    override suspend fun updateTimeoutSeconds(value: Int) = dataStore.setTimeoutSeconds(value)
    override suspend fun updateSendOnEnter(value: Boolean) = dataStore.setSendOnEnter(value)
    override suspend fun updateShowTokenCount(value: Boolean) = dataStore.setShowTokenCount(value)
    override suspend fun updateStreamResponses(value: Boolean) = dataStore.setStreamResponses(value)
    override suspend fun updateAppLockEnabled(value: Boolean) = dataStore.setAppLockEnabled(value)
}
