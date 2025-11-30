package com.alfikri.rizky.realtimepricetracker.domain.usecase.preferences

import com.alfikri.rizky.realtimepricetracker.domain.model.preferences.ThemeMode
import com.alfikri.rizky.realtimepricetracker.domain.repository.preferences.ThemePreferencesRepository
import kotlinx.coroutines.flow.Flow

// Handles all theme-related operations
class ThemePreferencesUseCase(
    private val themePreferencesRepository: ThemePreferencesRepository
) {

    fun observeThemeMode(): Flow<ThemeMode> =
        themePreferencesRepository.observeThemeMode()

    suspend fun getCurrentThemeMode(): ThemeMode =
        themePreferencesRepository.getThemeMode()

    suspend fun setLightMode() {
        themePreferencesRepository.setThemeMode(ThemeMode.LIGHT)
    }

    suspend fun setDarkMode() {
        themePreferencesRepository.setThemeMode(ThemeMode.DARK)
    }

    suspend fun setSystemMode() {
        themePreferencesRepository.setThemeMode(ThemeMode.SYSTEM)
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        themePreferencesRepository.setThemeMode(themeMode)
    }

    // Simple toggle between light and dark
    // If system mode, switch to dark first
    suspend fun toggleTheme() {
        val currentMode = getCurrentThemeMode()
        val newMode = when (currentMode) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> ThemeMode.DARK // default to dark when toggling from system
        }
        setThemeMode(newMode)
    }

    suspend fun resetToDefault() {
        themePreferencesRepository.resetTheme()
    }

    // Helper methods to check current mode
    // Note: these check the preference setting, not the actual rendered theme
    suspend fun isDarkMode(): Boolean = getCurrentThemeMode() == ThemeMode.DARK

    suspend fun isLightMode(): Boolean = getCurrentThemeMode() == ThemeMode.LIGHT

    suspend fun isSystemMode(): Boolean = getCurrentThemeMode() == ThemeMode.SYSTEM
}