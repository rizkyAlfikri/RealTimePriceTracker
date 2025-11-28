package com.alfikri.rizky.realtimepricetracker.domain.repository

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for stock data operations
 * This interface defines the contract for data layer implementation
 */
interface StockRepository {

    /**
     * Observes the real-time stock price updates
     * @return Flow of list of stocks with updated prices
     */
    fun observeStocks(): Flow<List<Stock>>

    /**
     * Observes the WebSocket connection state
     * @return Flow of boolean indicating connection status (true = connected, false = disconnected)
     */
    fun observeConnectionState(): Flow<Boolean>

    /**
     * Starts the real-time price feed by connecting to WebSocket
     */
    suspend fun startPriceFeed()

    /**
     * Stops the real-time price feed by disconnecting from WebSocket
     */
    suspend fun stopPriceFeed()

    /**
     * Checks if the price feed is currently active
     * @return true if feed is active, false otherwise
     */
    fun isFeedActive(): Boolean
}