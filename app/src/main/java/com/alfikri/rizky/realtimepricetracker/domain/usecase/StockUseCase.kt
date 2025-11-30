package com.alfikri.rizky.realtimepricetracker.domain.usecase

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import com.alfikri.rizky.realtimepricetracker.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow

/**
 * Combined use case for stock operations
 *
 * Note: Originally had separate use cases but merged for simplicity
 */
class StockUseCase(
    private val stockRepository: StockRepository
) {

    // Observe real-time stock updates (every 2s via WebSocket)
    fun observeStocks(): Flow<List<Stock>> {
        return stockRepository.observeStocks()
    }

    // Connection state for UI indicator
    fun observeConnectionState(): Flow<Boolean> {
        return stockRepository.observeConnectionState()
    }

    /**
     * Starts the price feed
     * @throws Exception if connection fails
     */
    suspend fun startPriceFeed() {
        stockRepository.startPriceFeed()
    }

    // Stop feed and cleanup
    suspend fun stopPriceFeed() {
        stockRepository.stopPriceFeed()
    }

    fun isFeedActive(): Boolean {
        return stockRepository.isFeedActive()
    }

    // Filter by symbol - case insensitive
    fun filterStocksBySymbol(stocks: List<Stock>, query: String): List<Stock> {
        if (query.isBlank()) return stocks
        return stocks.filter { stock ->
            stock.symbol.contains(query, ignoreCase = true)
        }
    }

    // Sort stocks by name or price
    fun sortStocks(
        stocks: List<Stock>,
        sortType: SortType,
        sortOrder: SortOrder
    ): List<Stock> {
        return when (sortType) {
            SortType.BY_NAME -> when (sortOrder) {
                SortOrder.ASCENDING -> stocks.sortedBy { it.symbol }
                SortOrder.DESCENDING -> stocks.sortedByDescending { it.symbol }
            }
            SortType.BY_PRICE -> when (sortOrder) {
                SortOrder.ASCENDING -> stocks.sortedBy { it.price }
                SortOrder.DESCENDING -> stocks.sortedByDescending { it.price }
            }
        }
    }

    /**
     * Compare previous vs current to find changed stocks
     */
    fun detectPriceChanges(
        previousStocks: List<Stock>,
        currentStocks: List<Stock>
    ): Set<String> {
        if (previousStocks.isEmpty()) return emptySet()

        val previousMap = previousStocks.associateBy { it.symbol }
        return currentStocks.filter { stock ->
            previousMap[stock.symbol]?.price != stock.price
        }.map { it.symbol }.toSet()
    }

    fun getTrendingUpStocks(stocks: List<Stock>): List<Stock> {
        return stocks.filter { it.price > it.previousPrice }
    }

    fun getTrendingDownStocks(stocks: List<Stock>): List<Stock> {
        return stocks.filter { it.price < it.previousPrice }
    }

    fun calculateAveragePrice(stocks: List<Stock>): Double {
        if (stocks.isEmpty()) return 0.0
        return stocks.sumOf { it.price } / stocks.size
    }

    // Get top performers by percentage change
    fun getTopPerformers(stocks: List<Stock>, count: Int = 5): List<Stock> {
        return stocks
            .filter { it.previousPrice > 0 } // avoid division by zero
            .sortedByDescending { (it.price - it.previousPrice) / it.previousPrice }
            .take(count)
    }
}

enum class SortType {
    BY_NAME,
    BY_PRICE
}

enum class SortOrder {
    ASCENDING,
    DESCENDING
}