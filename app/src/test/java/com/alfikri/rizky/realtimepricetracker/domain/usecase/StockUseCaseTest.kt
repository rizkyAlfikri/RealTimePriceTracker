package com.alfikri.rizky.realtimepricetracker.domain.usecase

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import com.alfikri.rizky.realtimepricetracker.domain.repository.StockRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive unit tests for StockUseCase
 * Tests all business logic operations with various scenarios
 */
class StockUseCaseTest {

    private lateinit var stockRepository: StockRepository
    private lateinit var stockUseCase: StockUseCase

    // Test data
    private val testStocks = listOf(
        Stock(
            symbol = "AAPL",
            price = 175.00,
            previousPrice = 170.00,
            growth = "+2.94%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = 1234567890L
        ),
        Stock(
            symbol = "GOOGL",
            price = 140.00,
            previousPrice = 145.00,
            growth = "-3.45%",
            iconUrl = "https://example.com/googl.png",
            timestamp = 1234567891L
        ),
        Stock(
            symbol = "MSFT",
            price = 380.00,
            previousPrice = 380.00,
            growth = "0.00%",
            iconUrl = "https://example.com/msft.png",
            timestamp = 1234567892L
        ),
        Stock(
            symbol = "AMZN",
            price = 180.00,
            previousPrice = 175.00,
            growth = "+2.86%",
            iconUrl = "https://example.com/amzn.png",
            timestamp = 1234567893L
        ),
        Stock(
            symbol = "NVDA",
            price = 480.00,
            previousPrice = 490.00,
            growth = "-2.04%",
            iconUrl = "https://example.com/nvda.png",
            timestamp = 1234567894L
        )
    )

    @Before
    fun setup() {
        stockRepository = mockk()
        stockUseCase = StockUseCase(stockRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // ========== observeStocks Tests ==========

    @Test
    fun `observeStocks returns flow from repository`() = runTest {
        // Given
        every { stockRepository.observeStocks() } returns flowOf(testStocks)

        // When
        val result = stockUseCase.observeStocks().first()

        // Then
        assertThat(result).isEqualTo(testStocks)
        verify { stockRepository.observeStocks() }
    }

    @Test
    fun `observeStocks returns empty list when repository returns empty`() = runTest {
        // Given
        every { stockRepository.observeStocks() } returns flowOf(emptyList())

        // When
        val result = stockUseCase.observeStocks().first()

        // Then
        assertThat(result).isEmpty()
        verify { stockRepository.observeStocks() }
    }

    // ========== observeConnectionState Tests ==========

    @Test
    fun `observeConnectionState returns true when connected`() = runTest {
        // Given
        every { stockRepository.observeConnectionState() } returns flowOf(true)

        // When
        val result = stockUseCase.observeConnectionState().first()

        // Then
        assertThat(result).isTrue()
        verify { stockRepository.observeConnectionState() }
    }

    @Test
    fun `observeConnectionState returns false when disconnected`() = runTest {
        // Given
        every { stockRepository.observeConnectionState() } returns flowOf(false)

        // When
        val result = stockUseCase.observeConnectionState().first()

        // Then
        assertThat(result).isFalse()
        verify { stockRepository.observeConnectionState() }
    }

    // ========== startPriceFeed Tests ==========

    @Test
    fun `startPriceFeed calls repository startPriceFeed`() = runTest {
        // Given
        coEvery { stockRepository.startPriceFeed() } just Runs

        // When
        stockUseCase.startPriceFeed()

        // Then
        coVerify { stockRepository.startPriceFeed() }
    }

    @Test
    fun `startPriceFeed propagates exception from repository`() = runTest {
        // Given
        val exception = Exception("Connection failed")
        coEvery { stockRepository.startPriceFeed() } throws exception

        // When/Then
        try {
            stockUseCase.startPriceFeed()
            throw AssertionError("Expected exception to be thrown")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Connection failed")
        }

        coVerify { stockRepository.startPriceFeed() }
    }

    // ========== stopPriceFeed Tests ==========

    @Test
    fun `stopPriceFeed calls repository stopPriceFeed`() = runTest {
        // Given
        coEvery { stockRepository.stopPriceFeed() } just Runs

        // When
        stockUseCase.stopPriceFeed()

        // Then
        coVerify { stockRepository.stopPriceFeed() }
    }

    // ========== isFeedActive Tests ==========

    @Test
    fun `isFeedActive returns true when feed is active`() {
        // Given
        every { stockRepository.isFeedActive() } returns true

        // When
        val result = stockUseCase.isFeedActive()

        // Then
        assertThat(result).isTrue()
        verify { stockRepository.isFeedActive() }
    }

    @Test
    fun `isFeedActive returns false when feed is inactive`() {
        // Given
        every { stockRepository.isFeedActive() } returns false

        // When
        val result = stockUseCase.isFeedActive()

        // Then
        assertThat(result).isFalse()
        verify { stockRepository.isFeedActive() }
    }

    // ========== filterStocksBySymbol Tests ==========

    @Test
    fun `filterStocksBySymbol returns matching stocks case insensitive`() {
        // When
        val result = stockUseCase.filterStocksBySymbol(testStocks, "aapl")

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().symbol).isEqualTo("AAPL")
    }

    @Test
    fun `filterStocksBySymbol returns multiple matches`() {
        // When
        val result = stockUseCase.filterStocksBySymbol(testStocks, "A")

        // Then - "A" matches AAPL, AMZN, and NVDA (contains "A")
        assertThat(result).hasSize(3)
        assertThat(result.map { it.symbol }).containsExactly("AAPL", "AMZN", "NVDA")
    }

    @Test
    fun `filterStocksBySymbol returns all stocks when query is blank`() {
        // When
        val result = stockUseCase.filterStocksBySymbol(testStocks, "")

        // Then
        assertThat(result).isEqualTo(testStocks)
    }

    @Test
    fun `filterStocksBySymbol returns all stocks when query is whitespace`() {
        // When
        val result = stockUseCase.filterStocksBySymbol(testStocks, "   ")

        // Then
        assertThat(result).isEqualTo(testStocks)
    }

    @Test
    fun `filterStocksBySymbol returns empty list when no matches`() {
        // When
        val result = stockUseCase.filterStocksBySymbol(testStocks, "XYZ")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `filterStocksBySymbol handles empty stock list`() {
        // When
        val result = stockUseCase.filterStocksBySymbol(emptyList(), "AAPL")

        // Then
        assertThat(result).isEmpty()
    }

    // ========== sortStocks Tests ==========

    @Test
    fun `sortStocks by name ascending`() {
        // When
        val result = stockUseCase.sortStocks(testStocks, SortType.BY_NAME, SortOrder.ASCENDING)

        // Then
        assertThat(result.map { it.symbol }).containsExactly("AAPL", "AMZN", "GOOGL", "MSFT", "NVDA").inOrder()
    }

    @Test
    fun `sortStocks by name descending`() {
        // When
        val result = stockUseCase.sortStocks(testStocks, SortType.BY_NAME, SortOrder.DESCENDING)

        // Then
        assertThat(result.map { it.symbol }).containsExactly("NVDA", "MSFT", "GOOGL", "AMZN", "AAPL").inOrder()
    }

    @Test
    fun `sortStocks by price ascending`() {
        // When
        val result = stockUseCase.sortStocks(testStocks, SortType.BY_PRICE, SortOrder.ASCENDING)

        // Then
        assertThat(result.map { it.price }).containsExactly(140.0, 175.0, 180.0, 380.0, 480.0).inOrder()
    }

    @Test
    fun `sortStocks by price descending`() {
        // When
        val result = stockUseCase.sortStocks(testStocks, SortType.BY_PRICE, SortOrder.DESCENDING)

        // Then
        assertThat(result.map { it.price }).containsExactly(480.0, 380.0, 180.0, 175.0, 140.0).inOrder()
    }

    @Test
    fun `sortStocks handles empty list`() {
        // When
        val result = stockUseCase.sortStocks(emptyList(), SortType.BY_NAME, SortOrder.ASCENDING)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `sortStocks handles single stock`() {
        // Given
        val singleStock = listOf(testStocks.first())

        // When
        val result = stockUseCase.sortStocks(singleStock, SortType.BY_PRICE, SortOrder.ASCENDING)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().symbol).isEqualTo("AAPL")
    }

    // ========== detectPriceChanges Tests ==========

    @Test
    fun `detectPriceChanges identifies stocks with changed prices`() {
        // Given
        val previousStocks = listOf(
            testStocks[0].copy(price = 170.00),
            testStocks[1].copy(price = 140.00),
            testStocks[2].copy(price = 380.00)
        )
        val currentStocks = listOf(
            testStocks[0].copy(price = 175.00), // Changed
            testStocks[1].copy(price = 140.00), // Unchanged
            testStocks[2].copy(price = 385.00)  // Changed
        )

        // When
        val result = stockUseCase.detectPriceChanges(previousStocks, currentStocks)

        // Then
        assertThat(result).containsExactly("AAPL", "MSFT")
    }

    @Test
    fun `detectPriceChanges returns empty set when no changes`() {
        // Given
        val previousStocks = testStocks.take(3)
        val currentStocks = testStocks.take(3)

        // When
        val result = stockUseCase.detectPriceChanges(previousStocks, currentStocks)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `detectPriceChanges returns empty set when previous list is empty`() {
        // When
        val result = stockUseCase.detectPriceChanges(emptyList(), testStocks)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `detectPriceChanges handles all stocks changed`() {
        // Given
        val previousStocks = testStocks.map { it.copy(price = it.price - 10.0) }

        // When
        val result = stockUseCase.detectPriceChanges(previousStocks, testStocks)

        // Then
        assertThat(result).hasSize(5)
        assertThat(result).containsExactly("AAPL", "GOOGL", "MSFT", "AMZN", "NVDA")
    }

    @Test
    fun `detectPriceChanges handles new stocks in current list`() {
        // Given
        val previousStocks = testStocks.take(2)
        val currentStocks = testStocks

        // When
        val result = stockUseCase.detectPriceChanges(previousStocks, currentStocks)

        // Then - New stocks (not in previous) should be detected as changed
        assertThat(result).contains("MSFT")
        assertThat(result).contains("AMZN")
        assertThat(result).contains("NVDA")
    }

    // ========== getTrendingUpStocks Tests ==========

    @Test
    fun `getTrendingUpStocks returns only stocks with positive price change`() {
        // When
        val result = stockUseCase.getTrendingUpStocks(testStocks)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.symbol }).containsExactly("AAPL", "AMZN")
        assertThat(result.all { it.price > it.previousPrice }).isTrue()
    }

    @Test
    fun `getTrendingUpStocks returns empty list when no stocks trending up`() {
        // Given
        val decliningStocks = testStocks.map { it.copy(price = it.previousPrice - 10.0) }

        // When
        val result = stockUseCase.getTrendingUpStocks(decliningStocks)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getTrendingUpStocks handles empty list`() {
        // When
        val result = stockUseCase.getTrendingUpStocks(emptyList())

        // Then
        assertThat(result).isEmpty()
    }

    // ========== getTrendingDownStocks Tests ==========

    @Test
    fun `getTrendingDownStocks returns only stocks with negative price change`() {
        // When
        val result = stockUseCase.getTrendingDownStocks(testStocks)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.symbol }).containsExactly("GOOGL", "NVDA")
        assertThat(result.all { it.price < it.previousPrice }).isTrue()
    }

    @Test
    fun `getTrendingDownStocks returns empty list when no stocks trending down`() {
        // Given
        val risingStocks = testStocks.map { it.copy(price = it.previousPrice + 10.0) }

        // When
        val result = stockUseCase.getTrendingDownStocks(risingStocks)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getTrendingDownStocks handles empty list`() {
        // When
        val result = stockUseCase.getTrendingDownStocks(emptyList())

        // Then
        assertThat(result).isEmpty()
    }

    // ========== calculateAveragePrice Tests ==========

    @Test
    fun `calculateAveragePrice returns correct average`() {
        // When
        val result = stockUseCase.calculateAveragePrice(testStocks)

        // Then - (175 + 140 + 380 + 180 + 480) / 5 = 271.0
        assertThat(result).isWithin(0.01).of(271.0)
    }

    @Test
    fun `calculateAveragePrice returns zero for empty list`() {
        // When
        val result = stockUseCase.calculateAveragePrice(emptyList())

        // Then
        assertThat(result).isEqualTo(0.0)
    }

    @Test
    fun `calculateAveragePrice handles single stock`() {
        // Given
        val singleStock = listOf(testStocks.first())

        // When
        val result = stockUseCase.calculateAveragePrice(singleStock)

        // Then
        assertThat(result).isEqualTo(175.0)
    }

    @Test
    fun `calculateAveragePrice handles stocks with same price`() {
        // Given
        val samePrice = testStocks.map { it.copy(price = 100.0) }

        // When
        val result = stockUseCase.calculateAveragePrice(samePrice)

        // Then
        assertThat(result).isEqualTo(100.0)
    }

    // ========== getTopPerformers Tests ==========

    @Test
    fun `getTopPerformers returns stocks with highest percentage gain`() {
        // When
        val result = stockUseCase.getTopPerformers(testStocks, count = 2)

        // Then - AAPL: +2.94%, AMZN: +2.86% should be top 2
        assertThat(result).hasSize(2)
        assertThat(result.first().symbol).isEqualTo("AAPL")
        assertThat(result[1].symbol).isEqualTo("AMZN")
    }

    @Test
    fun `getTopPerformers returns all stocks when count exceeds list size`() {
        // When
        val result = stockUseCase.getTopPerformers(testStocks, count = 10)

        // Then
        assertThat(result).hasSize(5)
    }

    @Test
    fun `getTopPerformers returns empty list for empty input`() {
        // When
        val result = stockUseCase.getTopPerformers(emptyList(), count = 5)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getTopPerformers handles count of zero`() {
        // When
        val result = stockUseCase.getTopPerformers(testStocks, count = 0)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getTopPerformers filters out stocks with zero previousPrice`() {
        // Given
        val stocksWithZero = testStocks + Stock(
            symbol = "ZERO",
            price = 100.0,
            previousPrice = 0.0,
            growth = "+inf%",
            iconUrl = "https://example.com/zero.png",
            timestamp = 1234567895L
        )

        // When
        val result = stockUseCase.getTopPerformers(stocksWithZero, count = 10)

        // Then - Should not include ZERO stock
        assertThat(result.none { it.symbol == "ZERO" }).isTrue()
    }

    @Test
    fun `getTopPerformers orders by percentage correctly`() {
        // Given
        val stocks = listOf(
            Stock("A", price = 110.0, previousPrice = 100.0, growth = "+10%", iconUrl = "", timestamp = 0), // 10%
            Stock("B", price = 50.0, previousPrice = 40.0, growth = "+25%", iconUrl = "", timestamp = 0),   // 25%
            Stock("C", price = 105.0, previousPrice = 100.0, growth = "+5%", iconUrl = "", timestamp = 0)   // 5%
        )

        // When
        val result = stockUseCase.getTopPerformers(stocks, count = 3)

        // Then - Should be ordered: B (25%), A (10%), C (5%)
        assertThat(result.map { it.symbol }).containsExactly("B", "A", "C").inOrder()
    }

    // ========== Edge Cases and Integration Tests ==========

    @Test
    fun `multiple operations can be performed on same use case instance`() = runTest {
        // Given
        every { stockRepository.observeStocks() } returns flowOf(testStocks)
        every { stockRepository.observeConnectionState() } returns flowOf(true)
        coEvery { stockRepository.startPriceFeed() } just Runs
        coEvery { stockRepository.stopPriceFeed() } just Runs

        // When
        val stocks = stockUseCase.observeStocks().first()
        val connected = stockUseCase.observeConnectionState().first()
        stockUseCase.startPriceFeed()
        val sorted = stockUseCase.sortStocks(stocks, SortType.BY_NAME, SortOrder.ASCENDING)
        val filtered = stockUseCase.filterStocksBySymbol(sorted, "A")
        stockUseCase.stopPriceFeed()

        // Then
        assertThat(stocks).hasSize(5)
        assertThat(connected).isTrue()
        assertThat(sorted.first().symbol).isEqualTo("AAPL")
        assertThat(filtered).hasSize(3) // AAPL, AMZN, NVDA all contain "A"
        coVerify { stockRepository.startPriceFeed() }
        coVerify { stockRepository.stopPriceFeed() }
    }

    @Test
    fun `business logic methods work with repository data`() = runTest {
        // Given
        every { stockRepository.observeStocks() } returns flowOf(testStocks)

        // When
        val stocks = stockUseCase.observeStocks().first()
        val trendingUp = stockUseCase.getTrendingUpStocks(stocks)
        val average = stockUseCase.calculateAveragePrice(stocks)
        val topPerformers = stockUseCase.getTopPerformers(stocks, 3)

        // Then
        assertThat(trendingUp).isNotEmpty()
        assertThat(average).isGreaterThan(0.0)
        assertThat(topPerformers).isNotEmpty()
    }
}