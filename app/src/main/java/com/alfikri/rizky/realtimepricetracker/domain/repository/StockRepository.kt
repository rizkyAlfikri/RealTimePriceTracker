package com.alfikri.rizky.realtimepricetracker.domain.repository

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import kotlinx.coroutines.flow.Flow

// Repository contract for stock data
interface StockRepository {

    // Get real-time stock updates as a Flow
    fun observeStocks(): Flow<List<Stock>>

    // Track WebSocket connection status
    fun observeConnectionState(): Flow<Boolean>

    // Connect to the price feed
    suspend fun startPriceFeed()

    // Disconnect from the price feed
    suspend fun stopPriceFeed()

    // Check if we're currently connected
    fun isFeedActive(): Boolean
}