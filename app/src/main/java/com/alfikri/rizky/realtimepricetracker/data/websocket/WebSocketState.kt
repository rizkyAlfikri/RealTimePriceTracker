package com.alfikri.rizky.realtimepricetracker.data.websocket

// WebSocket connection states
sealed class WebSocketState {
    data object Disconnected : WebSocketState()
    data object Connecting : WebSocketState()
    data object Connected : WebSocketState()
    data class Error(val error: String) : WebSocketState()
    data object Closing : WebSocketState()

    fun isConnected(): Boolean = this is Connected
}