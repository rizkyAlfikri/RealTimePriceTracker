package com.alfikri.rizky.realtimepricetracker.domain.repository.preferences

import com.alfikri.rizky.realtimepricetracker.domain.model.preferences.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for theme preferences
 * Handles storing and retrieving theme settings
 */
interface ThemePreferencesRepository {

    /**
     * Observes the current theme mode
     * @return Flow emitting the current theme mode whenever it changes
     */
    fun observeThemeMode(): Flow<ThemeMode>

    /**
     * Gets the current theme mode (one-time read)
     * @return Current theme mode
     */
    suspend fun getThemeMode(): ThemeMode

    /**
     * Sets the theme mode
     * @param themeMode The theme mode to set
     */
    suspend fun setThemeMode(themeMode: ThemeMode)

    /**
     * Resets theme to default (SYSTEM)
     */
    suspend fun resetTheme()
}