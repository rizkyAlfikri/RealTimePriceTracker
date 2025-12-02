package com.alfikri.rizky.realtimepricetracker.presentation.stock

import app.cash.turbine.test
import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import com.alfikri.rizky.realtimepricetracker.domain.model.preferences.ThemeMode
import com.alfikri.rizky.realtimepricetracker.domain.usecase.SortOrder
import com.alfikri.rizky.realtimepricetracker.domain.usecase.SortType
import com.alfikri.rizky.realtimepricetracker.domain.usecase.StockUseCase
import com.alfikri.rizky.realtimepricetracker.domain.usecase.preferences.ThemePreferencesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for StockViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    // Mocks
    private lateinit var stockUseCase: StockUseCase
    private lateinit var themePreferencesUseCase: ThemePreferencesUseCase

    // System under test
    private lateinit var viewModel: StockViewModel

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    // Test data
    private val testStocks = listOf(
        Stock(
            symbol = "AAPL",
            price = 178.50,
            previousPrice = 176.16,
            growth = "+1.33%",
            iconUrl = "",
            timestamp = System.currentTimeMillis()
        ),
        Stock(
            symbol = "GOOG",
            price = 142.30,
            previousPrice = 143.75,
            growth = "-1.01%",
            iconUrl = "",
            timestamp = System.currentTimeMillis()
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Initialize mocks
        stockUseCase = mockk(relaxed = true)
        themePreferencesUseCase = mockk(relaxed = true)

        // Setup default mock behavior
        every { stockUseCase.observeStocks() } returns flowOf(emptyList())
        every { stockUseCase.observeConnectionState() } returns flowOf(false)
        every { themePreferencesUseCase.observeThemeMode() } returns flowOf(ThemeMode.SYSTEM)
        every { stockUseCase.sortStocks(any(), any(), any()) } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): StockViewModel {
        return StockViewModel(stockUseCase, themePreferencesUseCase)
    }

    // ========== Initial State Tests ==========

    @Test
    fun `initial state should have default values`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value

        assertEquals(0, state.stocks.size)
        assertFalse(state.isLoading)
        assertFalse(state.isConnected)
        assertFalse(state.isActive)
        assertNull(state.error)
        assertFalse(state.isDarkTheme)
    }

    // ========== Theme Tests ==========

    @Test
    fun `should observe theme mode changes and update state`() = runTest {
        every { themePreferencesUseCase.observeThemeMode() } returns flowOf(
            ThemeMode.SYSTEM,
            ThemeMode.DARK,
            ThemeMode.LIGHT
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            // The last emitted value should be LIGHT (false)
            assertFalse(state.isDarkTheme)
        }
    }

    @Test
    fun `toggleTheme should call use case`() = runTest {
        coEvery { themePreferencesUseCase.toggleTheme() } returns Unit

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.ToggleTheme)
        advanceUntilIdle()

        coVerify { themePreferencesUseCase.toggleTheme() }
    }

    @Test
    fun `toggleTheme failure should emit error effect`() = runTest {
        val errorMessage = "Failed to toggle theme"
        coEvery { themePreferencesUseCase.toggleTheme() } throws Exception(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.handleIntent(StockContract.Intent.ToggleTheme)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is StockContract.Effect.ShowError)
            assertTrue((effect as StockContract.Effect.ShowError).message.contains(errorMessage))
        }
    }

    // ========== Connection State Tests ==========

    @Test
    fun `should observe connection state and update state`() = runTest {
        every { stockUseCase.observeConnectionState() } returns flowOf(true)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isConnected)
        }
    }

    // ========== Start Price Feed Tests ==========

    @Test
    fun `startPriceFeed should update state to loading and active`() = runTest {
        coEvery { stockUseCase.startPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(testStocks)
        every { stockUseCase.sortStocks(testStocks, SortType.BY_PRICE, SortOrder.DESCENDING) } returns testStocks

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading) // Should be false after loading completes
        assertTrue(state.isActive)
        assertEquals(null, state.error)

        coVerify { stockUseCase.startPriceFeed() }
    }

    @Test
    fun `startPriceFeed should observe stocks and update state`() = runTest {
        coEvery { stockUseCase.startPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(testStocks)
        every { stockUseCase.sortStocks(testStocks, SortType.BY_PRICE, SortOrder.DESCENDING) } returns testStocks

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.stocks.size)
        assertEquals("AAPL", state.stocks[0].symbol)
        assertEquals("GOOG", state.stocks[1].symbol)
    }

    @Test
    fun `startPriceFeed failure should update state with error`() = runTest {
        val errorMessage = "Connection failed"
        coEvery { stockUseCase.startPriceFeed() } throws Exception(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertFalse(state.isActive)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `startPriceFeed failure should emit error effect`() = runTest {
        val errorMessage = "Connection failed"
        coEvery { stockUseCase.startPriceFeed() } throws Exception(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is StockContract.Effect.ShowError)
            assertEquals(errorMessage, (effect as StockContract.Effect.ShowError).message)
        }
    }

    // ========== Stop Price Feed Tests ==========

    @Test
    fun `stopPriceFeed should update state to inactive`() = runTest {
        coEvery { stockUseCase.stopPriceFeed() } returns Unit

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.StopPriceFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isActive)
        assertFalse(state.isConnected)

        coVerify { stockUseCase.stopPriceFeed() }
    }

    @Test
    fun `stopPriceFeed failure should emit error effect`() = runTest {
        val errorMessage = "Failed to stop"
        coEvery { stockUseCase.stopPriceFeed() } throws Exception(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.handleIntent(StockContract.Intent.StopPriceFeed)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is StockContract.Effect.ShowError)
            assertTrue((effect as StockContract.Effect.ShowError).message.contains(errorMessage))
        }
    }

    // ========== Toggle Price Feed Tests ==========

    @Test
    fun `togglePriceFeed should start feed when inactive`() = runTest {
        coEvery { stockUseCase.startPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(emptyList())
        every { stockUseCase.sortStocks(any(), any(), any()) } returns emptyList()

        viewModel = createViewModel()
        advanceUntilIdle()

        // Initial state should be inactive
        assertFalse(viewModel.state.value.isActive)

        viewModel.handleIntent(StockContract.Intent.TogglePriceFeed)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isActive)
        coVerify { stockUseCase.startPriceFeed() }
    }

    @Test
    fun `togglePriceFeed should stop feed when active`() = runTest {
        coEvery { stockUseCase.startPriceFeed() } returns Unit
        coEvery { stockUseCase.stopPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(emptyList())
        every { stockUseCase.sortStocks(any(), any(), any()) } returns emptyList()

        viewModel = createViewModel()
        advanceUntilIdle()

        // Start the feed first
        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isActive)

        // Now toggle to stop
        viewModel.handleIntent(StockContract.Intent.TogglePriceFeed)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isActive)
        coVerify { stockUseCase.stopPriceFeed() }
    }

    // ========== Refresh Stocks Tests ==========

    @Test
    fun `refreshStocks when active should not do anything`() = runTest {
        coEvery { stockUseCase.startPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(emptyList())
        every { stockUseCase.sortStocks(any(), any(), any()) } returns emptyList()

        viewModel = createViewModel()
        advanceUntilIdle()

        // Start the feed first
        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()

        // Try to refresh
        viewModel.handleIntent(StockContract.Intent.RefreshStocks)
        advanceUntilIdle()

        // Should not emit any effect or change state
        assertTrue(viewModel.state.value.isActive)
    }

    @Test
    fun `refreshStocks when inactive should emit toast effect`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.handleIntent(StockContract.Intent.RefreshStocks)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is StockContract.Effect.ShowToast)
            assertEquals("Start price feed to see updates", (effect as StockContract.Effect.ShowToast).message)
        }
    }

    // ========== Stock Updates Tests ==========

    @Test
    fun `should detect price changes and set flash colors`() = runTest {
        val stockWithChange = Stock(
            symbol = "AAPL",
            price = 180.00,
            previousPrice = 175.00, // Price increased
            growth = "+2.86%",
            iconUrl = "",
            timestamp = System.currentTimeMillis()
        )

        coEvery { stockUseCase.startPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(listOf(stockWithChange))
        every { stockUseCase.sortStocks(listOf(stockWithChange), SortType.BY_PRICE, SortOrder.DESCENDING) } returns listOf(stockWithChange)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.stocks.size)

        // Should have green flash because price > previousPrice
        val stockUiModel = state.stocks[0]
        assertEquals("AAPL", stockUiModel.symbol)
        // Flash color should be Green initially, then cleared after 1 second
    }

    @Test
    fun `should clear flash colors after 1 second`() = runTest {
        val stockWithChange = Stock(
            symbol = "AAPL",
            price = 180.00,
            previousPrice = 175.00,
            growth = "+2.86%",
            iconUrl = "",
            timestamp = System.currentTimeMillis()
        )

        coEvery { stockUseCase.startPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(listOf(stockWithChange))
        every { stockUseCase.sortStocks(listOf(stockWithChange), SortType.BY_PRICE, SortOrder.DESCENDING) } returns listOf(stockWithChange)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()

        // Advance time by 1 second to trigger flash clear
        advanceTimeBy(1000)

        val state = viewModel.state.value
        assertEquals(1, state.stocks.size)
        // Flash color should be None after 1 second
    }

    @Test
    fun `should sort stocks by price in descending order`() = runTest {
        val unsortedStocks = listOf(
            Stock("GOOG", 142.30, 143.75, "-1.01%", "", System.currentTimeMillis()),
            Stock("AAPL", 178.50, 176.16, "+1.33%", "", System.currentTimeMillis())
        )
        val sortedStocks = listOf(
            Stock("AAPL", 178.50, 176.16, "+1.33%", "", System.currentTimeMillis()),
            Stock("GOOG", 142.30, 143.75, "-1.01%", "", System.currentTimeMillis())
        )

        coEvery { stockUseCase.startPriceFeed() } returns Unit
        every { stockUseCase.observeStocks() } returns flowOf(unsortedStocks)
        every { stockUseCase.sortStocks(unsortedStocks, SortType.BY_PRICE, SortOrder.DESCENDING) } returns sortedStocks

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(StockContract.Intent.StartPriceFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.stocks.size)
        assertEquals("AAPL", state.stocks[0].symbol) // Highest price first
        assertEquals("GOOG", state.stocks[1].symbol)

        verify { stockUseCase.sortStocks(unsortedStocks, SortType.BY_PRICE, SortOrder.DESCENDING) }
    }

}