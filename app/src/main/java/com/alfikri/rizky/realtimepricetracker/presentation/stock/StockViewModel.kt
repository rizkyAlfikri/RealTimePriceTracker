package com.alfikri.rizky.realtimepricetracker.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfikri.rizky.realtimepricetracker.domain.model.preferences.ThemeMode
import com.alfikri.rizky.realtimepricetracker.domain.usecase.StockUseCase
import com.alfikri.rizky.realtimepricetracker.domain.usecase.SortOrder
import com.alfikri.rizky.realtimepricetracker.domain.usecase.SortType
import com.alfikri.rizky.realtimepricetracker.domain.usecase.preferences.ThemePreferencesUseCase
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.FlashColor
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.toUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Stock screen using MVI pattern
 * Handles user intents and manages UI state
 */
class StockViewModel(
    private val stockUseCase: StockUseCase,
    private val themePreferencesUseCase: ThemePreferencesUseCase
) : ViewModel() {

    // Mutable state for internal updates
    private val _state = MutableStateFlow(StockContract.State())
    val state: StateFlow<StockContract.State> = _state.asStateFlow()

    // One-time effects (e.g., show toast, navigate)
    private val _effect = MutableSharedFlow<StockContract.Effect>()
    val effect = _effect.asSharedFlow()

    // Job for observing stocks
    private var stockObserverJob: Job? = null

    init {
        // Observe theme changes
        observeThemeMode()

        // Observe connection state
        observeConnectionState()
    }

    /**
     * Handle user intents
     */
    fun handleIntent(intent: StockContract.Intent) {
        when (intent) {
            is StockContract.Intent.StartPriceFeed -> startPriceFeed()
            is StockContract.Intent.StopPriceFeed -> stopPriceFeed()
            is StockContract.Intent.TogglePriceFeed -> togglePriceFeed()
            is StockContract.Intent.ToggleTheme -> toggleTheme()
            is StockContract.Intent.RefreshStocks -> refreshStocks()
        }
    }

    /**
     * Observe theme mode changes from preferences
     */
    private fun observeThemeMode() {
        themePreferencesUseCase.observeThemeMode()
            .onEach { themeMode ->
                val isDark = when (themeMode) {
                    ThemeMode.DARK -> true
                    ThemeMode.LIGHT -> false
                    ThemeMode.SYSTEM -> false // You can check system theme here if needed
                }
                _state.update { it.copy(isDarkTheme = isDark) }
            }
            .catch { error ->
                sendEffect(StockContract.Effect.ShowError("Theme error: ${error.message}"))
            }
            .launchIn(viewModelScope)
    }

    /**
     * Observe WebSocket connection state
     */
    private fun observeConnectionState() {
        stockUseCase.observeConnectionState()
            .onEach { isConnected ->
                _state.update { it.copy(isConnected = isConnected) }
            }
            .catch { error ->
                sendEffect(StockContract.Effect.ShowError("Connection error: ${error.message}"))
            }
            .launchIn(viewModelScope)
    }

    /**
     * Start the price feed and observe stock updates
     */
    private fun startPriceFeed() {
        viewModelScope.launch {
            try {
                // Update state to show loading
                _state.update { it.copy(isLoading = true, isActive = true, error = null) }

                // Start the feed from repository
                stockUseCase.startPriceFeed()

                // Observe stock updates
                observeStocks()

                // Update state
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isActive = false,
                        error = e.message ?: "Failed to start price feed"
                    )
                }
                sendEffect(StockContract.Effect.ShowError(e.message ?: "Failed to start price feed"))
            }
        }
    }

    /**
     * Stop the price feed
     */
    private fun stopPriceFeed() {
        viewModelScope.launch {
            try {
                // Stop observing stocks
                stockObserverJob?.cancel()
                stockObserverJob = null

                // Stop the feed from repository
                stockUseCase.stopPriceFeed()

                // Update state
                _state.update { it.copy(isActive = false, isConnected = false) }
            } catch (e: Exception) {
                sendEffect(StockContract.Effect.ShowError(e.message ?: "Failed to stop price feed"))
            }
        }
    }

    /**
     * Toggle between start and stop
     */
    private fun togglePriceFeed() {
        if (_state.value.isActive) {
            stopPriceFeed()
        } else {
            startPriceFeed()
        }
    }

    /**
     * Toggle theme between light and dark
     */
    private fun toggleTheme() {
        viewModelScope.launch {
            try {
                themePreferencesUseCase.toggleTheme()
            } catch (e: Exception) {
                sendEffect(StockContract.Effect.ShowError("Failed to toggle theme: ${e.message}"))
            }
        }
    }

    /**
     * Refresh stocks (useful for manual refresh)
     */
    private fun refreshStocks() {
        if (_state.value.isActive) {
            // Already observing, no need to refresh
            return
        }

        // If not active, you could fetch once here
        // For now, we'll just show a toast
        viewModelScope.launch {
            sendEffect(StockContract.Effect.ShowToast("Start price feed to see updates"))
        }
    }

    /**
     * Observe stock updates from the use case
     */
    private fun observeStocks() {
        stockObserverJob?.cancel()
        stockObserverJob = stockUseCase.observeStocks()
            .onEach { stocks ->
                // Sort stocks by price (highest first) as per Figma design
                val sortedStocks = stockUseCase.sortStocks(
                    stocks = stocks,
                    sortType = SortType.BY_PRICE,
                    sortOrder = SortOrder.DESCENDING
                )

                // Detect price changes for flash animation
                // Simply check if price != previousPrice (already in domain model)
                val stocksWithChanges = sortedStocks.filter { stock ->
                    stock.price != stock.previousPrice
                }

                // Convert to UI models with flash colors
                val stockUiModels = sortedStocks.map { stock ->
                    // Determine flash color based on price change
                    val flashColor = when {
                        stock.price > stock.previousPrice -> FlashColor.Green
                        stock.price < stock.previousPrice -> FlashColor.Red
                        else -> FlashColor.None
                    }
                    stock.toUiModel(flashColor)
                }

                // Update state with new stocks
                _state.update { it.copy(stocks = stockUiModels) }

                // Clear flash after 1 second for stocks that changed
                if (stocksWithChanges.isNotEmpty()) {
                    viewModelScope.launch {
                        delay(1000)
                        _state.update { currentState ->
                            currentState.copy(
                                stocks = currentState.stocks.map { it.copy(flashColor = FlashColor.None) }
                            )
                        }
                    }
                }
            }
            .catch { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to observe stocks"
                    )
                }
                sendEffect(StockContract.Effect.ShowError(error.message ?: "Failed to observe stocks"))
            }
            .launchIn(viewModelScope)
    }

    /**
     * Send one-time effect to the UI
     */
    private suspend fun sendEffect(effect: StockContract.Effect) {
        _effect.emit(effect)
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up when ViewModel is destroyed
        viewModelScope.launch {
            if (_state.value.isActive) {
                stockUseCase.stopPriceFeed()
            }
        }
    }
}