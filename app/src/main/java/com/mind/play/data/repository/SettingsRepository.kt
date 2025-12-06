package com.mind.play.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.mind.play.domain.models.AppSettings
import com.mind.play.ui.theme.TextSize
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val settings: StateFlow<AppSettings?> = dataStore.data
        .catch { throwable ->
            when (throwable) {
                is IOException -> emit(emptyPreferences())
                is ClassCastException -> {
                    dataStore.edit { it.clear() }
                    emit(emptyPreferences())
                }
                else -> throw throwable
            }
        }
        .map { preferences ->
            AppSettings(
                highContrast = preferences[Keys.HIGH_CONTRAST] ?: true,
                textSize = TextSize.values().getOrElse(
                    preferences[Keys.TEXT_SIZE] ?: TextSize.MEDIUM.ordinal
                ) { TextSize.MEDIUM },
                stressMode = preferences[Keys.STRESS_MODE] ?: false,
                uiSoundEnabled = preferences[Keys.UI_SOUND] ?: true,
                gameSoundEnabled = preferences[Keys.GAME_SOUND] ?: true,
                notificationsEnabled = preferences[Keys.NOTIFICATIONS] ?: false,
                onboardingCompleted = preferences[Keys.ONBOARDING_COMPLETED] ?: false
            )
        }
        .stateIn(scope, SharingStarted.Eagerly, null)
    
    suspend fun updateHighContrast(enabled: Boolean) {
        dataStore.edit { it[Keys.HIGH_CONTRAST] = enabled }
    }
    
    suspend fun updateTextSize(textSize: TextSize) {
        dataStore.edit { it[Keys.TEXT_SIZE] = textSize.ordinal }
    }
    
    suspend fun updateStressMode(enabled: Boolean) {
        dataStore.edit { it[Keys.STRESS_MODE] = enabled }
    }
    
    suspend fun updateUiSound(enabled: Boolean) {
        dataStore.edit { it[Keys.UI_SOUND] = enabled }
    }
    
    suspend fun updateGameSound(enabled: Boolean) {
        dataStore.edit { it[Keys.GAME_SOUND] = enabled }
    }
    
    suspend fun updateNotifications(enabled: Boolean) {
        dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }
    
    private object Keys {
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val TEXT_SIZE = intPreferencesKey("text_size_level")
        val STRESS_MODE = booleanPreferencesKey("stress_mode")
        val UI_SOUND = booleanPreferencesKey("ui_sound")
        val GAME_SOUND = booleanPreferencesKey("game_sound")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
