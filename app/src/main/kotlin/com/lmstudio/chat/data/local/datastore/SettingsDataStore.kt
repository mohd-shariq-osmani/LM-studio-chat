package com.lmstudio.chat.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lmstudio_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val BASE_URL = stringPreferencesKey("base_url")
        val API_KEY = stringPreferencesKey("api_key")
        val TEMPERATURE = floatPreferencesKey("temperature")
        val TOP_P = floatPreferencesKey("top_p")
        val MAX_TOKENS = intPreferencesKey("max_tokens")
        val LAST_MODEL = stringPreferencesKey("last_model")
        val TIMEOUT_SECONDS = intPreferencesKey("timeout_seconds")
        val SEND_ON_ENTER = booleanPreferencesKey("send_on_enter")
        val SHOW_TOKEN_COUNT = booleanPreferencesKey("show_token_count")
        val STREAM_RESPONSES = booleanPreferencesKey("stream_responses")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
    }

    val appLockEnabled: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[APP_LOCK_ENABLED] ?: false }

    val baseUrl: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[BASE_URL] ?: "http://192.168.0.50:1234/v1" }

    val apiKey: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[API_KEY] ?: "" }

    val temperature: Flow<Float> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[TEMPERATURE] ?: 0.7f }

    val topP: Flow<Float> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[TOP_P] ?: 0.9f }

    val maxTokens: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[MAX_TOKENS] ?: 2048 }

    val lastModel: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[LAST_MODEL] ?: "" }

    val timeoutSeconds: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[TIMEOUT_SECONDS] ?: 120 }

    val sendOnEnter: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[SEND_ON_ENTER] ?: true }

    val showTokenCount: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[SHOW_TOKEN_COUNT] ?: true }

    val streamResponses: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[STREAM_RESPONSES] ?: true }

    suspend fun setBaseUrl(url: String) { context.dataStore.edit { it[BASE_URL] = url } }
    suspend fun setApiKey(key: String) { context.dataStore.edit { it[API_KEY] = key } }
    suspend fun setTemperature(value: Float) { context.dataStore.edit { it[TEMPERATURE] = value } }
    suspend fun setTopP(value: Float) { context.dataStore.edit { it[TOP_P] = value } }
    suspend fun setMaxTokens(value: Int) { context.dataStore.edit { it[MAX_TOKENS] = value } }
    suspend fun setLastModel(modelId: String) { context.dataStore.edit { it[LAST_MODEL] = modelId } }
    suspend fun setTimeoutSeconds(value: Int) { context.dataStore.edit { it[TIMEOUT_SECONDS] = value } }
    suspend fun setSendOnEnter(value: Boolean) { context.dataStore.edit { it[SEND_ON_ENTER] = value } }
    suspend fun setShowTokenCount(value: Boolean) { context.dataStore.edit { it[SHOW_TOKEN_COUNT] = value } }
    suspend fun setStreamResponses(value: Boolean) { context.dataStore.edit { it[STREAM_RESPONSES] = value } }
    suspend fun setAppLockEnabled(value: Boolean) { context.dataStore.edit { it[APP_LOCK_ENABLED] = value } }
}
