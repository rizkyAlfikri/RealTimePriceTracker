package com.alfikri.rizky.realtimepricetracker.data.repository

import com.alfikri.rizky.realtimepricetracker.data.model.StockDto
import com.alfikri.rizky.realtimepricetracker.data.websocket.WebSocketClient
import com.alfikri.rizky.realtimepricetracker.data.websocket.WebSocketState
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for StockRepositoryImpl
 * Tests repository operations with mocked WebSocketClient
 */
class StockRepositoryImplTest {

    private lateinit var webSocketClient: WebSocketClient
    private lateinit var repository: StockRepositoryImpl

    private lateinit var stockUpdateFlow: MutableSharedFlow<StockDto>
    private lateinit var connectionStateFlow: MutableStateFlow<WebSocketState>

    private val testStockDto = StockDto(
        symbol = "AAPL",
        price = 175.00,
        previousPrice = 170.00,
        growth = "+2.94%",
        iconUrl = "https://example.com/aapl.png",
        timestamp = 1234567890L
    )

    @Before
    fun setup() {
        webSocketClient = mockk()
        stockUpdateFlow = MutableSharedFlow(replay = 0)
        connectionStateFlow = MutableStateFlow(WebSocketState.Disconnected)

        every { webSocketClient.stockUpdates } returns stockUpdateFlow
        every { webSocketClient.connectionState } returns connectionStateFlow

        repository = StockRepositoryImpl(webSocketClient)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // ========== observeStocks Tests ==========

    @Test
    fun `observeStocks converts StockDto to Stock domain model`() = runTest {
        // Given
        val job = launch {
            stockUpdateFlow.emit(testStockDto)
        }

        // When
        val stocks = repository.observeStocks().first()

        // Then
        assertThat(stocks).hasSize(1)
        val stock = stocks.first()
        assertThat(stock.symbol).isEqualTo("AAPL")
        assertThat(stock.price).isEqualTo(175.00)
        assertThat(stock.previousPrice).isEqualTo(170.00)
        assertThat(stock.growth).isEqualTo("+2.94%")
        assertThat(stock.iconUrl).isEqualTo("https://example.com/aapl.png")
        assertThat(stock.timestamp).isEqualTo(1234567890L)

        job.cancel()
    }

    @Test
    fun `observeStocks caches stocks and returns updated list`() = runTest {
        // Given
        val stock1 = testStockDto.copy(symbol = "AAPL", price = 175.00)
        val stock2 = testStockDto.copy(symbol = "GOOGL", price = 140.00)
        val stock3 = testStockDto.copy(symbol = "MSFT", price = 380.00)

        // When/Then
        val job = launch {
            stockUpdateFlow.emit(stock1)
            stockUpdateFlow.emit(stock2)
            stockUpdateFlow.emit(stock3)
        }

        val allStockLists = repository.observeStocks().take(3).toList()

        assertThat(allStockLists).hasSize(3)
        assertThat(allStockLists[0]).hasSize(1)
        assertThat(allStockLists[1]).hasSize(2)
        assertThat(allStockLists[2]).hasSize(3)

        job.cancel()
    }

    @Test
    fun `observeStocks updates existing stock in cache`() = runTest {
        // Given
        val stock1 = testStockDto.copy(symbol = "AAPL", price = 175.00)
        val stock2 = testStockDto.copy(symbol = "AAPL", price = 180.00)

        // When
        val job = launch {
            stockUpdateFlow.emit(stock1)
            stockUpdateFlow.emit(stock2)
        }

        val allStockLists = repository.observeStocks().take(2).toList()

        // Then
        assertThat(allStockLists).hasSize(2)
        assertThat(allStockLists[0]).hasSize(1)
        assertThat(allStockLists[0].first().price).isEqualTo(175.00)
        assertThat(allStockLists[1]).hasSize(1)
        assertThat(allStockLists[1].first().price).isEqualTo(180.00)

        job.cancel()
    }

    // ========== observeConnectionState Tests ==========

    @Test
    fun `observeConnectionState maps Connected to true`() = runTest {
        // Given
        connectionStateFlow.value = WebSocketState.Connected

        // When
        val isConnected = repository.observeConnectionState().first()

        // Then
        assertThat(isConnected).isTrue()
    }

    @Test
    fun `observeConnectionState maps Disconnected to false`() = runTest {
        // Given
        connectionStateFlow.value = WebSocketState.Disconnected

        // When
        val isConnected = repository.observeConnectionState().first()

        // Then
        assertThat(isConnected).isFalse()
    }

    @Test
    fun `observeConnectionState maps Error to false`() = runTest {
        // Given
        connectionStateFlow.value = WebSocketState.Error("Connection failed")

        // When
        val isConnected = repository.observeConnectionState().first()

        // Then
        assertThat(isConnected).isFalse()
    }

    // ========== startPriceFeed Tests ==========

    @Test
    fun `startPriceFeed connects and starts sending when not connected`() = runTest {
        // Given
        every { webSocketClient.isConnected() } returnsMany listOf(false, true)
        every { webSocketClient.connect() } just Runs
        every { webSocketClient.startSendingPrices() } just Runs

        // When
        repository.startPriceFeed()

        // Then
        verify { webSocketClient.connect() }
        verify { webSocketClient.startSendingPrices() }
    }

    @Test
    fun `startPriceFeed only starts sending when already connected`() = runTest {
        // Given
        every { webSocketClient.isConnected() } returns true
        every { webSocketClient.startSendingPrices() } just Runs

        // When
        repository.startPriceFeed()

        // Then
        verify(exactly = 0) { webSocketClient.connect() }
        verify { webSocketClient.startSendingPrices() }
    }

    @Test
    fun `startPriceFeed throws exception when connection fails`() = runTest {
        // Given
        every { webSocketClient.isConnected() } returns false
        every { webSocketClient.connect() } just Runs
        connectionStateFlow.value = WebSocketState.Error("Connection timeout")

        // When/Then
        try {
            repository.startPriceFeed()
            throw AssertionError("Expected exception to be thrown")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Connection timeout")
        }

        verify { webSocketClient.connect() }
        verify(exactly = 0) { webSocketClient.startSendingPrices() }
    }

    // ========== stopPriceFeed Tests ==========

    @Test
    fun `stopPriceFeed stops sending and disconnects`() = runTest {
        // Given
        every { webSocketClient.stopSendingPrices() } just Runs
        every { webSocketClient.disconnect() } just Runs

        // When
        repository.stopPriceFeed()

        // Then
        verify { webSocketClient.stopSendingPrices() }
        verify { webSocketClient.disconnect() }
    }

    @Test
    fun `stopPriceFeed clears cache`() = runTest {
        // Given
        val stock1 = testStockDto.copy(symbol = "AAPL")
        every { webSocketClient.stopSendingPrices() } just Runs
        every { webSocketClient.disconnect() } just Runs

        // Populate cache
        val job1 = launch { stockUpdateFlow.emit(stock1) }
        repository.observeStocks().first()
        job1.cancel()

        // When
        repository.stopPriceFeed()

        // Then - Cache should be cleared
        val stock2 = testStockDto.copy(symbol = "GOOGL")
        val job2 = launch { stockUpdateFlow.emit(stock2) }
        val stocks = repository.observeStocks().first()

        assertThat(stocks).hasSize(1)
        assertThat(stocks.first().symbol).isEqualTo("GOOGL")

        job2.cancel()
    }

    // ========== isFeedActive Tests ==========

    @Test
    fun `isFeedActive returns true when client is connected`() {
        // Given
        every { webSocketClient.isConnected() } returns true

        // When
        val isActive = repository.isFeedActive()

        // Then
        assertThat(isActive).isTrue()
        verify { webSocketClient.isConnected() }
    }

    @Test
    fun `isFeedActive returns false when client is not connected`() {
        // Given
        every { webSocketClient.isConnected() } returns false

        // When
        val isActive = repository.isFeedActive()

        // Then
        assertThat(isActive).isFalse()
        verify { webSocketClient.isConnected() }
    }

    // ========== resetStocks Tests ==========

    @Test
    fun `resetStocks clears cache and resets client`() = runTest {
        // Given
        val stock1 = testStockDto.copy(symbol = "AAPL")
        every { webSocketClient.resetStocks() } just Runs

        // Populate cache first
        val job1 = launch { stockUpdateFlow.emit(stock1) }
        repository.observeStocks().first()
        job1.cancel()

        // When
        repository.resetStocks()

        // Then
        verify { webSocketClient.resetStocks() }
    }

    // ========== Integration Tests ==========

    @Test
    fun `multiple stocks with same symbol updates the cached stock`() = runTest {
        // Given
        val update1 = testStockDto.copy(symbol = "AAPL", price = 175.00, timestamp = 1000L)
        val update2 = testStockDto.copy(symbol = "AAPL", price = 176.00, timestamp = 2000L)
        val update3 = testStockDto.copy(symbol = "AAPL", price = 177.00, timestamp = 3000L)

        // When
        val job = launch {
            stockUpdateFlow.emit(update1)
            stockUpdateFlow.emit(update2)
            stockUpdateFlow.emit(update3)
        }

        val stockLists = repository.observeStocks().take(3).toList()

        // Then
        assertThat(stockLists).hasSize(3)
        assertThat(stockLists[0]).hasSize(1)
        assertThat(stockLists[0].first().price).isEqualTo(175.00)
        assertThat(stockLists[1]).hasSize(1)
        assertThat(stockLists[1].first().price).isEqualTo(176.00)
        assertThat(stockLists[2]).hasSize(1)
        assertThat(stockLists[2].first().price).isEqualTo(177.00)

        job.cancel()
    }

    @Test
    fun `observeStocks handles rapid updates`() = runTest {
        // Given
        val updates = (1..10).map { i ->
            testStockDto.copy(symbol = "STOCK$i", price = 100.0 + i)
        }

        // When
        val job = launch {
            updates.forEach { stockUpdateFlow.emit(it) }
        }

        val finalStockList = repository.observeStocks().take(10).toList().last()

        // Then
        assertThat(finalStockList).hasSize(10)
        assertThat(finalStockList.map { it.symbol }).containsExactlyElementsIn(
            (1..10).map { "STOCK$it" }
        )

        job.cancel()
    }
}