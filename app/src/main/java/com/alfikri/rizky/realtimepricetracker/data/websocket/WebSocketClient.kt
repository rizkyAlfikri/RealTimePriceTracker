package com.alfikri.rizky.realtimepricetracker.data.websocket

import android.util.Log
import com.alfikri.rizky.realtimepricetracker.data.model.StockDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * WebSocket client for real-time stock updates
 * Uses Postman Echo server for testing
 */
class WebSocketClient(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val stockDataGenerator: StockDataGenerator
) {
    private val TAG = "WebSocketClient"

    private val _connectionState = MutableStateFlow<WebSocketState>(WebSocketState.Disconnected)
    val connectionState: StateFlow<WebSocketState> = _connectionState.asStateFlow()

    // TODO: Consider using StateFlow for better state management
    private val _stockUpdates = MutableSharedFlow<StockDto>(
        replay = 0,
        extraBufferCapacity = 64, // should be enough for most cases
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val stockUpdates: SharedFlow<StockDto> = _stockUpdates.asSharedFlow()

    private var webSocket: WebSocket? = null
    private var sendJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentStocks: List<StockDto> = emptyList()

    fun connect() {
        if (_connectionState.value.isConnected()) {
            Log.d(TAG, "Already connected")
            return
        }

        Log.d(TAG, "Connecting to WebSocket...")
        _connectionState.value = WebSocketState.Connecting

        val request = Request.Builder()
            .url(WEBSOCKET_URL)
            .build()

        webSocket = okHttpClient.newWebSocket(request, createWebSocketListener())
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting...")
        _connectionState.value = WebSocketState.Closing

        sendJob?.cancel()
        sendJob = null

        webSocket?.close(NORMAL_CLOSURE_STATUS, "Client disconnected")
        webSocket = null

        _connectionState.value = WebSocketState.Disconnected
    }

    // Start periodic price updates every 2s
    fun startSendingPrices() {
        if (sendJob?.isActive == true) {
            Log.d(TAG, "Already sending prices")
            return
        }

        if (currentStocks.isEmpty()) {
            currentStocks = stockDataGenerator.generateInitialStocks()
        }

        Log.d(TAG, "Starting price updates every ${UPDATE_INTERVAL_MS}ms")

        sendJob = scope.launch {
            while (isActive && _connectionState.value.isConnected()) {
                currentStocks.forEach { stock ->
                    val updatedStock = stock.withRandomPriceChange()

                    // Update our local list
                    currentStocks = currentStocks.map {
                        if (it.symbol == updatedStock.symbol) updatedStock else it
                    }

                    val message = json.encodeToString(updatedStock)
                    val sent = webSocket?.send(message) ?: false

                    if (!sent) {
                        Log.w(TAG, "Failed to send message for ${updatedStock.symbol}")
                    }
                }

                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    fun stopSendingPrices() {
        Log.d(TAG, "Stopping price updates")
        sendJob?.cancel()
        sendJob = null
    }

    fun isConnected(): Boolean {
        return _connectionState.value.isConnected()
    }

    /**
     * Creates WebSocket listener for handling events
     */
    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected: ${response.message}")
                _connectionState.value = WebSocketState.Connected
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received message: ${text.take(100)}...")

                try {
                    // Parse the received stock data
                    val stockDto = json.decodeFromString<StockDto>(text)

                    // Emit to subscribers
                    scope.launch {
                        _stockUpdates.emit(stockDto)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse message: ${e.message}", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code - $reason")
                _connectionState.value = WebSocketState.Closing
                webSocket.close(NORMAL_CLOSURE_STATUS, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code - $reason")
                _connectionState.value = WebSocketState.Disconnected
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}", t)
                _connectionState.value = WebSocketState.Error(
                    t.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Resets the stock data to initial values
     */
    fun resetStocks() {
        currentStocks = stockDataGenerator.generateInitialStocks()
    }

    companion object {
        // WebSocket server URL (Postman Echo)
        private const val WEBSOCKET_URL = "wss://ws.postman-echo.com/raw"

        // Update interval: 2 seconds
        private const val UPDATE_INTERVAL_MS = 2000L

        // Normal closure status code
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}