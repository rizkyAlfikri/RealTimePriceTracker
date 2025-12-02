package com.alfikri.rizky.realtimepricetracker.presentation.stock.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for TopBar component
 */
class TopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topBar_displaysConnectedStatus_whenConnected() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = true,
                    isActive = false,
                    isDarkTheme = false,
                    onToggleTheme = {},
                    onToggleFeed = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Connected")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_displaysDisconnectedStatus_whenNotConnected() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = false,
                    isActive = false,
                    isDarkTheme = false,
                    onToggleTheme = {},
                    onToggleFeed = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Disconnected")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_displaysStartButton_whenInactive() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = false,
                    isActive = false,
                    isDarkTheme = false,
                    onToggleTheme = {},
                    onToggleFeed = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Start")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_displaysStopButton_whenActive() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = true,
                    isActive = true,
                    isDarkTheme = false,
                    onToggleTheme = {},
                    onToggleFeed = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Stop")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_callsOnToggleFeed_whenStartButtonClicked() {
        // Given
        var toggleFeedCalled = false

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = false,
                    isActive = false,
                    isDarkTheme = false,
                    onToggleTheme = {},
                    onToggleFeed = { toggleFeedCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Start")
            .performClick()

        // Then
        assert(toggleFeedCalled)
    }

    @Test
    fun topBar_callsOnToggleFeed_whenStopButtonClicked() {
        // Given
        var toggleFeedCalled = false

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = true,
                    isActive = true,
                    isDarkTheme = false,
                    onToggleTheme = {},
                    onToggleFeed = { toggleFeedCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Stop")
            .performClick()

        // Then
        assert(toggleFeedCalled)
    }

    @Test
    fun topBar_displaysThemeToggleButton() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = false,
                    isActive = false,
                    isDarkTheme = false,
                    onToggleTheme = {},
                    onToggleFeed = {}
                )
            }
        }

        // Then - Check for dark mode toggle
        composeTestRule
            .onNodeWithContentDescription("Switch to dark mode")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_displaysLightModeToggle_whenInDarkMode() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme(darkTheme = true) {
                TopBar(
                    isConnected = false,
                    isActive = false,
                    isDarkTheme = true,
                    onToggleTheme = {},
                    onToggleFeed = {}
                )
            }
        }

        // Then - Check for light mode toggle
        composeTestRule
            .onNodeWithContentDescription("Switch to light mode")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_callsOnToggleTheme_whenThemeButtonClicked() {
        // Given
        var toggleThemeCalled = false

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                TopBar(
                    isConnected = false,
                    isActive = false,
                    isDarkTheme = false,
                    onToggleTheme = { toggleThemeCalled = true },
                    onToggleFeed = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Switch to dark mode")
            .performClick()

        // Then
        assert(toggleThemeCalled)
    }
}