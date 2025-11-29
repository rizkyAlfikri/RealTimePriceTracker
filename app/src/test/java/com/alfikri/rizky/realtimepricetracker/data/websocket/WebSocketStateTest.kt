package com.alfikri.rizky.realtimepricetracker.data.websocket

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for WebSocketState sealed class
 * Tests all state types and helper methods
 */
class WebSocketStateTest {

    // ========== State Type Tests ==========

    @Test
    fun `Disconnected state is created correctly`() {
        // When
        val state = WebSocketState.Disconnected

        // Then
        assertThat(state).isInstanceOf(WebSocketState.Disconnected::class.java)
        assertThat(state.isConnected()).isFalse()
    }

    @Test
    fun `Connecting state is created correctly`() {
        // When
        val state = WebSocketState.Connecting

        // Then
        assertThat(state).isInstanceOf(WebSocketState.Connecting::class.java)
        assertThat(state.isConnected()).isFalse()
    }

    @Test
    fun `Connected state is created correctly`() {
        // When
        val state = WebSocketState.Connected

        // Then
        assertThat(state).isInstanceOf(WebSocketState.Connected::class.java)
        assertThat(state.isConnected()).isTrue()
    }

    @Test
    fun `Closing state is created correctly`() {
        // When
        val state = WebSocketState.Closing

        // Then
        assertThat(state).isInstanceOf(WebSocketState.Closing::class.java)
        assertThat(state.isConnected()).isFalse()
    }

    @Test
    fun `Error state is created with error message`() {
        // Given
        val errorMessage = "Connection failed"

        // When
        val state = WebSocketState.Error(errorMessage)

        // Then
        assertThat(state).isInstanceOf(WebSocketState.Error::class.java)
        assertThat(state.error).isEqualTo("Connection failed")
        assertThat(state.isConnected()).isFalse()
    }

    // ========== isConnected Tests ==========

    @Test
    fun `isConnected returns true only for Connected state`() {
        // When/Then
        assertThat(WebSocketState.Disconnected.isConnected()).isFalse()
        assertThat(WebSocketState.Connecting.isConnected()).isFalse()
        assertThat(WebSocketState.Connected.isConnected()).isTrue()
        assertThat(WebSocketState.Closing.isConnected()).isFalse()
        assertThat(WebSocketState.Error("test").isConnected()).isFalse()
    }

    // ========== Equality Tests ==========

    @Test
    fun `Disconnected states are equal`() {
        // Given
        val state1 = WebSocketState.Disconnected
        val state2 = WebSocketState.Disconnected

        // Then
        assertThat(state1).isEqualTo(state2)
    }

    @Test
    fun `Connected states are equal`() {
        // Given
        val state1 = WebSocketState.Connected
        val state2 = WebSocketState.Connected

        // Then
        assertThat(state1).isEqualTo(state2)
    }

    @Test
    fun `Error states with same message are equal`() {
        // Given
        val state1 = WebSocketState.Error("Network error")
        val state2 = WebSocketState.Error("Network error")

        // Then
        assertThat(state1).isEqualTo(state2)
    }

    @Test
    fun `Error states with different messages are not equal`() {
        // Given
        val state1 = WebSocketState.Error("Network error")
        val state2 = WebSocketState.Error("Timeout error")

        // Then
        assertThat(state1).isNotEqualTo(state2)
    }

    @Test
    fun `Different state types are not equal`() {
        // Given
        val states = listOf(
            WebSocketState.Disconnected,
            WebSocketState.Connecting,
            WebSocketState.Connected,
            WebSocketState.Closing,
            WebSocketState.Error("test")
        )

        // Then - Each state should not equal any other
        for (i in states.indices) {
            for (j in states.indices) {
                if (i != j) {
                    assertThat(states[i]).isNotEqualTo(states[j])
                }
            }
        }
    }

    // ========== Pattern Matching Tests ==========

    @Test
    fun `can pattern match on state type`() {
        // Given
        val states = listOf(
            WebSocketState.Disconnected,
            WebSocketState.Connecting,
            WebSocketState.Connected,
            WebSocketState.Closing,
            WebSocketState.Error("test error")
        )

        // When/Then
        states.forEach { state ->
            when (state) {
                is WebSocketState.Disconnected -> assertThat(state).isInstanceOf(WebSocketState.Disconnected::class.java)
                is WebSocketState.Connecting -> assertThat(state).isInstanceOf(WebSocketState.Connecting::class.java)
                is WebSocketState.Connected -> assertThat(state).isInstanceOf(WebSocketState.Connected::class.java)
                is WebSocketState.Closing -> assertThat(state).isInstanceOf(WebSocketState.Closing::class.java)
                is WebSocketState.Error -> {
                    assertThat(state).isInstanceOf(WebSocketState.Error::class.java)
                    assertThat(state.error).isNotEmpty()
                }
            }
        }
    }

    @Test
    fun `Error state holds error message correctly`() {
        // Given
        val errorMessages = listOf(
            "Connection timeout",
            "Invalid response",
            "Network unreachable",
            ""
        )

        // When/Then
        errorMessages.forEach { message ->
            val state = WebSocketState.Error(message)
            assertThat(state.error).isEqualTo(message)
        }
    }

    // ========== State Transitions ==========

    @Test
    fun `typical state transition sequence`() {
        // Given - Simulate typical connection lifecycle
        val states = mutableListOf<WebSocketState>()

        // When - Simulate state changes
        states.add(WebSocketState.Disconnected)      // 1. Initially disconnected
        states.add(WebSocketState.Connecting)        // 2. Start connecting
        states.add(WebSocketState.Connected)         // 3. Successfully connected
        states.add(WebSocketState.Closing)           // 4. User closes connection
        states.add(WebSocketState.Disconnected)      // 5. Connection closed

        // Then - Verify state sequence
        assertThat(states[0].isConnected()).isFalse()
        assertThat(states[1].isConnected()).isFalse()
        assertThat(states[2].isConnected()).isTrue()
        assertThat(states[3].isConnected()).isFalse()
        assertThat(states[4].isConnected()).isFalse()
    }

    @Test
    fun `error state transition sequence`() {
        // Given - Simulate connection failure
        val states = mutableListOf<WebSocketState>()

        // When - Simulate failed connection
        states.add(WebSocketState.Disconnected)           // 1. Initially disconnected
        states.add(WebSocketState.Connecting)             // 2. Start connecting
        states.add(WebSocketState.Error("Timeout"))       // 3. Connection fails
        states.add(WebSocketState.Disconnected)           // 4. Back to disconnected

        // Then
        assertThat(states[2]).isInstanceOf(WebSocketState.Error::class.java)
        assertThat((states[2] as WebSocketState.Error).error).isEqualTo("Timeout")
        assertThat(states[2].isConnected()).isFalse()
    }

    // ========== toString Tests ==========

    @Test
    fun `Error state toString includes error message`() {
        // Given
        val state = WebSocketState.Error("Connection failed: timeout")

        // When
        val string = state.toString()

        // Then
        assertThat(string).contains("Error")
        assertThat(string).contains("Connection failed: timeout")
    }
}