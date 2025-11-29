package com.alfikri.rizky.realtimepricetracker.data.repository

import android.util.Log
import com.alfikri.rizky.realtimepricetracker.data.websocket.WebSocketClient
import com.alfikri.rizky.realtimepricetracker.data.websocket.WebSocketState
import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import com.alfikri.rizky.realtimepricetracker.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of StockRepository
 * Manages stock data from WebSocket connection
 */
class StockRepositoryImpl(
    private val webSocketClient: WebSocketClient
) : StockRepository {

    private val TAG = "StockRepositoryImpl"

    // Cache for current stocks (symbol -> Stock)
    private val stockCache = mutableMapOf<String, Stock>()

    override fun observeStocks(): Flow<List<Stock>> {
        return webSocketClient.stockUpdates
            .map { stockDto ->
                Log.d(TAG, "Received stock update: ${stockDto.symbol} = ${stockDto.price}")

                // Convert to domain model
                val stock = stockDto.toDomain()

                // Update cache
                stockCache[stock.symbol] = stock

                // Return all cached stocks as a list
                stockCache.values.toList()
            }
    }

    override fun observeConnectionState(): Flow<Boolean> {
        return webSocketClient.connectionState
            .map { state ->
                val isConnected = state.isConnected()
                Log.d(TAG, "Connection state changed: $state (connected: $isConnected)")
                isConnected
            }
    }

    override suspend fun startPriceFeed() {
        Log.d(TAG, "Starting price feed...")

        try {
            // Connect to WebSocket if not already connected
            if (!webSocketClient.isConnected()) {
                webSocketClient.connect()

                // Wait a bit for connection to establish
                kotlinx.coroutines.delay(1000)

                // Check if connection was successful
                if (!webSocketClient.isConnected()) {
                    val currentState = webSocketClient.connectionState.value
                    val errorMessage = when (currentState) {
                        is WebSocketState.Error -> currentState.error
                        else -> "Failed to connect to WebSocket"
                    }
                    throw Exception(errorMessage)
                }
            }

            // Start sending price updates
            webSocketClient.startSendingPrices()

            Log.d(TAG, "Price feed started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start price feed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun stopPriceFeed() {
        Log.d(TAG, "Stopping price feed...")

        // Stop sending updates
        webSocketClient.stopSendingPrices()

        // Disconnect from WebSocket
        webSocketClient.disconnect()

        // Clear cache
        stockCache.clear()

        Log.d(TAG, "Price feed stopped")
    }

    override fun isFeedActive(): Boolean {
        val isActive = webSocketClient.isConnected()
        Log.d(TAG, "Feed active: $isActive")
        return isActive
    }

    /**
     * Resets all stock data to initial values
     */
    fun resetStocks() {
        Log.d(TAG, "Resetting stocks to initial values")
        stockCache.clear()
        webSocketClient.resetStocks()
    }
}