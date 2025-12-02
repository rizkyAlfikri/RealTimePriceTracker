package com.alfikri.rizky.realtimepricetracker.presentation.stock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.alfikri.rizky.realtimepricetracker.presentation.stock.components.StockList
import com.alfikri.rizky.realtimepricetracker.presentation.stock.components.TopBar
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.FlashColor
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.PriceDirection
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.StockUiModel
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import org.junit.Rule
import org.junit.Test

/**
 * Integration tests for StockScreen components
 * Tests the full screen layout composition
 */
class StockScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stockScreen_displaysTopBarAndStockList() {
        // Given
        val stocks = listOf(
            createTestStock(symbol = "AAPL", price = "$178.50")
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                Scaffold(
                    topBar = {
                        TopBar(
                            isConnected = false,
                            isActive = false,
                            isDarkTheme = false,
                            onToggleTheme = {},
                            onToggleFeed = {}
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        StockList(stocks = stocks, isLoading = false)
                    }
                }
            }
        }

        // Then - Both TopBar and StockList should be visible
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
        composeTestRule.onNodeWithText("AAPL").assertIsDisplayed()
    }

    @Test
    fun stockScreen_displaysAllStockInformation() {
        // Given
        val stocks = listOf(
            createTestStock(
                symbol = "AAPL",
                price = "$178.50",
                change = "+2.34",
                changePercent = "+1.33%",
                priceDirection = PriceDirection.UP
            )
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                Scaffold(
                    topBar = {
                        TopBar(
                            isConnected = true,
                            isActive = true,
                            isDarkTheme = false,
                            onToggleTheme = {},
                            onToggleFeed = {}
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        StockList(stocks = stocks, isLoading = false)
                    }
                }
            }
        }

        // Then - All stock data should be visible
        composeTestRule.onNodeWithText("AAPL").assertIsDisplayed()
        composeTestRule.onNodeWithText("$178.50").assertIsDisplayed()
        composeTestRule.onNodeWithText("+2.34").assertIsDisplayed()
        composeTestRule.onNodeWithText("+1.33%").assertIsDisplayed()
        composeTestRule.onNodeWithText("Connected").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stop").assertIsDisplayed()
    }

    @Test
    fun stockScreen_displaysMultipleStocks() {
        // Given
        val stocks = listOf(
            createTestStock(symbol = "AAPL", price = "$178.50"),
            createTestStock(symbol = "GOOG", price = "$142.30"),
            createTestStock(symbol = "TSLA", price = "$238.72"),
            createTestStock(symbol = "AMZN", price = "$151.94"),
            createTestStock(symbol = "MSFT", price = "$378.91")
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(stocks = stocks, isLoading = false)
            }
        }

        // Then
        stocks.forEach { stock ->
            composeTestRule.onNodeWithText(stock.symbol).assertIsDisplayed()
        }
    }

    @Test
    fun stockScreen_displaysEmptyState_whenNoStocks() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(stocks = emptyList(), isLoading = false)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("No stocks available.\nPress Start to begin tracking.")
            .assertIsDisplayed()
    }

    // Helper function to create test stock
    private fun createTestStock(
        symbol: String = "AAPL",
        price: String = "$178.50",
        change: String = "+2.34",
        changePercent: String = "+1.33%",
        priceDirection: PriceDirection = PriceDirection.UP,
        flashColor: FlashColor = FlashColor.None
    ) = StockUiModel(
        symbol = symbol,
        price = price,
        change = change,
        changePercent = changePercent,
        priceDirection = priceDirection,
        flashColor = flashColor,
        timestamp = System.currentTimeMillis()
    )
}