package com.alfikri.rizky.realtimepricetracker.presentation.stock.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.FlashColor
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.PriceDirection
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.StockUiModel
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for StockList component
 */
class StockListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stockList_displaysHeader() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(
                    stocks = emptyList(),
                    isLoading = false
                )
            }
        }

        // Then - Check all header columns
        composeTestRule.onNodeWithText("Symbol").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price").assertIsDisplayed()
        composeTestRule.onNodeWithText("Change").assertIsDisplayed()
        composeTestRule.onNodeWithText("Change %").assertIsDisplayed()
    }

    @Test
    fun stockList_displaysEmptyState_whenNoStocks() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(
                    stocks = emptyList(),
                    isLoading = false
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("No stocks available.\nPress Start to begin tracking.")
            .assertIsDisplayed()
    }

    @Test
    fun stockList_displaysLoadingIndicator_whenLoading() {
        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(
                    stocks = emptyList(),
                    isLoading = true
                )
            }
        }

        // Then - Loading indicator should be displayed
        // Note: We can't easily test CircularProgressIndicator visibility,
        // but we can verify empty state is NOT shown
        composeTestRule
            .onNodeWithText("No stocks available.\nPress Start to begin tracking.")
            .assertDoesNotExist()
    }

    @Test
    fun stockList_displaysStocks_whenStocksAvailable() {
        // Given
        val stocks = listOf(
            createTestStock(symbol = "AAPL", price = "$178.50"),
            createTestStock(symbol = "GOOG", price = "$142.30"),
            createTestStock(symbol = "TSLA", price = "$238.72")
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(
                    stocks = stocks,
                    isLoading = false
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("AAPL").assertIsDisplayed()
        composeTestRule.onNodeWithText("$178.50").assertIsDisplayed()
        composeTestRule.onNodeWithText("GOOG").assertIsDisplayed()
        composeTestRule.onNodeWithText("$142.30").assertIsDisplayed()
        composeTestRule.onNodeWithText("TSLA").assertIsDisplayed()
        composeTestRule.onNodeWithText("$238.72").assertIsDisplayed()
    }

    @Test
    fun stockList_displaysStocksWithChanges() {
        // Given
        val stocks = listOf(
            createTestStock(
                symbol = "AAPL",
                price = "$178.50",
                change = "+2.34",
                changePercent = "+1.33%",
                priceDirection = PriceDirection.UP
            ),
            createTestStock(
                symbol = "GOOG",
                price = "$142.30",
                change = "-1.45",
                changePercent = "-1.01%",
                priceDirection = PriceDirection.DOWN
            )
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(
                    stocks = stocks,
                    isLoading = false
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("+2.34").assertIsDisplayed()
        composeTestRule.onNodeWithText("+1.33%").assertIsDisplayed()
        composeTestRule.onNodeWithText("-1.45").assertIsDisplayed()
        composeTestRule.onNodeWithText("-1.01%").assertIsDisplayed()
    }

    @Test
    fun stockList_doesNotDisplayEmptyState_whenStocksAvailable() {
        // Given
        val stocks = listOf(createTestStock())

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockList(
                    stocks = stocks,
                    isLoading = false
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("No stocks available.\nPress Start to begin tracking.")
            .assertDoesNotExist()
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