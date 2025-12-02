package com.alfikri.rizky.realtimepricetracker.presentation.stock

import com.alfikri.rizky.realtimepricetracker.presentation.base.MviEffect
import com.alfikri.rizky.realtimepricetracker.presentation.base.MviIntent
import com.alfikri.rizky.realtimepricetracker.presentation.base.MviState
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.StockUiModel

/**
 * Contract for Stock screen MVI architecture
 * Defines intents, state, and side effects
 */
object StockContract {

    /**
     * User intents/actions
     */
    sealed class Intent : MviIntent {
        // Start receiving real-time price updates
        data object StartPriceFeed : Intent()

        // Stop receiving price updates
        data object StopPriceFeed : Intent()

        // Toggle between start and stop
        data object TogglePriceFeed : Intent()

        // Toggle theme between light and dark
        data object ToggleTheme : Intent()

        // Refresh the stock list
        data object RefreshStocks : Intent()
    }

    /**
     * UI State
     */
    data class State(
        val stocks: List<StockUiModel> = emptyList(),
        val isLoading: Boolean = false,
        val isConnected: Boolean = false,
        val isActive: Boolean = false,
        val error: String? = null,
        val isDarkTheme: Boolean = false
    ) : MviState

    /**
     * One-time side effects
     */
    sealed class Effect : MviEffect {
        data class ShowError(val message: String) : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}