package com.alfikri.rizky.realtimepricetracker.data.repository.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfikri.rizky.realtimepricetracker.domain.model.preferences.ThemeMode
import com.alfikri.rizky.realtimepricetracker.domain.repository.preferences.ThemePreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implementation of ThemePreferencesRepository using DataStore
 * Stores theme preference as a string value
 */
class ThemePreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : ThemePreferencesRepository {

    private val TAG = "ThemePreferencesRepo"

    override fun observeThemeMode(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            val themeString = preferences[THEME_MODE_KEY]
            val themeMode = ThemeMode.fromString(themeString)
            Log.d(TAG, "Theme mode observed: $themeMode")
            themeMode
        }
    }

    override suspend fun getThemeMode(): ThemeMode {
        val preferences = dataStore.data.first()
        val themeString = preferences[THEME_MODE_KEY]
        val themeMode = ThemeMode.fromString(themeString)
        Log.d(TAG, "Theme mode retrieved: $themeMode")
        return themeMode
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        Log.d(TAG, "Setting theme mode to: $themeMode")
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    override suspend fun resetTheme() {
        Log.d(TAG, "Resetting theme to default (SYSTEM)")
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = ThemeMode.DEFAULT.name
        }
    }

    companion object {
        /**
         * DataStore key for theme mode
         */
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
}