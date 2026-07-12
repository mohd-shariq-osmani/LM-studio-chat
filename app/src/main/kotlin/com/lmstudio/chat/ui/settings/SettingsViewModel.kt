package com.lmstudio.chat.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmstudio.chat.data.local.db.AppDatabase
import com.lmstudio.chat.domain.model.AppSettings
import com.lmstudio.chat.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.getSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    fun updateBaseUrl(url: String) = viewModelScope.launch {
        settingsRepository.updateBaseUrl(url)
    }

    fun updateApiKey(key: String) = viewModelScope.launch {
        settingsRepository.updateApiKey(key)
    }

    fun updateTemperature(value: Float) = viewModelScope.launch {
        settingsRepository.updateTemperature(value)
    }

    fun updateTopP(value: Float) = viewModelScope.launch {
        settingsRepository.updateTopP(value)
    }

    fun updateMaxTokens(value: Int) = viewModelScope.launch {
        settingsRepository.updateMaxTokens(value)
    }

    fun updateAppLockEnabled(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.updateAppLockEnabled(enabled)
    }

    fun clearDatabase() = viewModelScope.launch {
        appDatabase.clearAllTables()
    }
}
