package com.alfikri.rizky.realtimepricetracker.domain.usecase

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import com.alfikri.rizky.realtimepricetracker.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow

/**
 * Combined use case class that encapsulates all stock-related business logic
 * This single class replaces multiple individual use case classes for simplicity
 *
 * Responsibilities:
 * - Observe real-time stock price updates
 * - Start/Stop price feed
 * - Monitor WebSocket connection state
 * - Provide business logic for stock operations
 */
class StockUseCase(
    private val stockRepository: StockRepository
) {

    /**
     * Observes real-time stock price updates
     *
     * This function provides a Flow of stock list that updates every 2 seconds
     * with new price data from the WebSocket connection.
     *
     * @return Flow<List<Stock>> - Continuous stream of stock price updates
     */
    fun observeStocks(): Flow<List<Stock>> {
        return stockRepository.observeStocks()
    }

    /**
     * Observes the WebSocket connection state
     *
     * Useful for UI to display connection status indicator (green/red dot)
     *
     * @return Flow<Boolean> - true when connected, false when disconnected
     */
    fun observeConnectionState(): Flow<Boolean> {
        return stockRepository.observeConnectionState()
    }

    /**
     * Starts the real-time price feed
     *
     * This will:
     * 1. Establish WebSocket connection
     * 2. Begin sending stock data every 2 seconds
     * 3. Start receiving echoed data back from server
     *
     * @throws Exception if connection fails
     */
    suspend fun startPriceFeed() {
        stockRepository.startPriceFeed()
    }

    /**
     * Stops the real-time price feed
     *
     * This will:
     * 1. Stop sending stock data
     * 2. Close WebSocket connection gracefully
     * 3. Clean up resources
     */
    suspend fun stopPriceFeed() {
        stockRepository.stopPriceFeed()
    }

    /**
     * Checks if the price feed is currently active
     *
     * @return true if feed is running, false otherwise
     */
    fun isFeedActive(): Boolean {
        return stockRepository.isFeedActive()
    }

    /**
     * Filters stocks by symbol (case-insensitive search)
     *
     * Business logic example: Filter operation in domain layer
     *
     * @param stocks List of stocks to filter
     * @param query Search query string
     * @return Filtered list of stocks matching the query
     */
    fun filterStocksBySymbol(stocks: List<Stock>, query: String): List<Stock> {
        if (query.isBlank()) return stocks
        return stocks.filter { stock ->
            stock.symbol.contains(query, ignoreCase = true)
        }
    }

    /**
     * Sorts stocks based on sort type and order
     *
     * Business logic: Sorting operations in domain layer
     *
     * @param stocks List of stocks to sort
     * @param sortType Type of sorting (BY_NAME or BY_PRICE)
     * @param sortOrder Order of sorting (ASCENDING or DESCENDING)
     * @return Sorted list of stocks
     */
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
     * Detects which stocks have had price changes
     *
     * Business logic: Compare previous and current stock lists to identify changes
     *
     * @param previousStocks Previous list of stocks
     * @param currentStocks Current list of stocks
     * @return Set of stock symbols that had price changes
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

    /**
     * Gets stocks that are currently trending up (positive growth)
     *
     * @param stocks List of stocks
     * @return List of stocks with positive price changes
     */
    fun getTrendingUpStocks(stocks: List<Stock>): List<Stock> {
        return stocks.filter { it.price > it.previousPrice }
    }

    /**
     * Gets stocks that are currently trending down (negative growth)
     *
     * @param stocks List of stocks
     * @return List of stocks with negative price changes
     */
    fun getTrendingDownStocks(stocks: List<Stock>): List<Stock> {
        return stocks.filter { it.price < it.previousPrice }
    }

    /**
     * Calculates average price across all stocks
     *
     * @param stocks List of stocks
     * @return Average price, or 0.0 if list is empty
     */
    fun calculateAveragePrice(stocks: List<Stock>): Double {
        if (stocks.isEmpty()) return 0.0
        return stocks.sumOf { it.price } / stocks.size
    }

    /**
     * Gets top N performing stocks by price change percentage
     *
     * @param stocks List of stocks
     * @param count Number of top stocks to return
     * @return List of top performing stocks
     */
    fun getTopPerformers(stocks: List<Stock>, count: Int = 5): List<Stock> {
        return stocks
            .filter { it.previousPrice > 0 }
            .sortedByDescending { (it.price - it.previousPrice) / it.previousPrice }
            .take(count)
    }
}

/**
 * Enum for sort type options
 */
enum class SortType {
    BY_NAME,
    BY_PRICE
}

/**
 * Enum for sort order options
 */
enum class SortOrder {
    ASCENDING,
    DESCENDING
}