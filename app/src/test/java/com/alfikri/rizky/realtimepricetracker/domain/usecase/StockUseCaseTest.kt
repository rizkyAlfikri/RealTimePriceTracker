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

// Tests for StockUseCase
class StockUseCaseTest {

    private lateinit var stockRepository: StockRepository
    private lateinit var stockUseCase: StockUseCase

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
    fun shouldReturnFlowFromRepository() = runTest {
        every { stockRepository.observeStocks() } returns flowOf(testStocks)

        val result = stockUseCase.observeStocks().first()

        assertThat(result).isEqualTo(testStocks)
        verify { stockRepository.observeStocks() }
    }

    // TODO: Add more connection state tests

    // ========== startPriceFeed Tests ==========

    @Test
    fun `should start price feed`() = runTest {
        coEvery { stockRepository.startPriceFeed() } just Runs
        stockUseCase.startPriceFeed()
        coVerify { stockRepository.startPriceFeed() }
    }

    // ========== stopPriceFeed Tests ==========

    @Test
    fun `stop price feed calls repository`() = runTest {
        coEvery { stockRepository.stopPriceFeed() } just Runs
        stockUseCase.stopPriceFeed()
        coVerify { stockRepository.stopPriceFeed() }
    }

    // ========== isFeedActive Tests ==========

    // ========== Filter Tests ==========

    @Test
    fun filterShouldBeCaseInsensitive() {
        val result = stockUseCase.filterStocksBySymbol(testStocks, "aapl")
        assertThat(result).hasSize(1)
        assertThat(result.first().symbol).isEqualTo("AAPL")
    }

    @Test
    fun `should return multiple matching stocks`() {
        val result = stockUseCase.filterStocksBySymbol(testStocks, "A")
        assertThat(result).hasSize(3) // AAPL, AMZN, NVDA
    }

    // TODO: Add test for null query handling

    // ========== sortStocks Tests ==========

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

    // ========== calculateAveragePrice Tests ==========

    @Test
    fun `calculateAveragePrice returns correct average`() {
        // When
        val result = stockUseCase.calculateAveragePrice(testStocks)

        // Then - (175 + 140 + 380 + 180 + 480) / 5 = 271.0
        assertThat(result).isWithin(0.01).of(271.0)
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

}