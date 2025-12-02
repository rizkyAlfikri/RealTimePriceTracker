package com.alfikri.rizky.realtimepricetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alfikri.rizky.realtimepricetracker.domain.model.preferences.ThemeMode
import com.alfikri.rizky.realtimepricetracker.domain.usecase.preferences.ThemePreferencesUseCase
import com.alfikri.rizky.realtimepricetracker.presentation.stock.StockScreen
import com.alfikri.rizky.realtimepricetracker.presentation.stock.StockViewModel
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

/**
 * Main activity for the Real-Time Stock Price Tracker app
 * Uses MVI pattern and Jetpack Compose for UI
 */
class MainActivity : ComponentActivity() {

    private val themePreferencesUseCase: ThemePreferencesUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Get the StockViewModel from Koin
            val viewModel: StockViewModel = koinViewModel()

            // Observe theme mode from preferences
            val themeMode by themePreferencesUseCase.observeThemeMode()
                .collectAsState(initial = ThemeMode.SYSTEM)

            // Determine dark theme based on theme mode
            val darkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            // Apply theme and render UI
            RealTimePriceTrackerTheme(darkTheme = darkTheme) {
                StockScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}