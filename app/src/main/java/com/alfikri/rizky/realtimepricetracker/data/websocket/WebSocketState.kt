package com.alfikri.rizky.realtimepricetracker.data.websocket

/**
 * Represents the connection state of WebSocket
 */
sealed class WebSocketState {
    /**
     * WebSocket is disconnected
     */
    data object Disconnected : WebSocketState()

    /**
     * WebSocket is attempting to connect
     */
    data object Connecting : WebSocketState()

    /**
     * WebSocket is connected and ready
     */
    data object Connected : WebSocketState()

    /**
     * WebSocket connection failed
     * @param error The error message
     */
    data class Error(val error: String) : WebSocketState()

    /**
     * WebSocket is closing
     */
    data object Closing : WebSocketState()

    /**
     * Helper to check if connected
     */
    fun isConnected(): Boolean = this is Connected
}