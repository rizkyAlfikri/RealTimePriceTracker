package com.alfikri.rizky.realtimepricetracker.presentation.stock.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.FlashColor
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.PriceDirection
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.StockUiModel
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for StockRow component
 */
class StockRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stockRow_displaysSymbol() {
        // Given
        val stock = createTestStock(symbol = "AAPL")

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("AAPL")
            .assertIsDisplayed()
    }

    @Test
    fun stockRow_displaysPrice() {
        // Given
        val stock = createTestStock(price = "$178.50")

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("$178.50")
            .assertIsDisplayed()
    }

    @Test
    fun stockRow_displaysChange() {
        // Given
        val stock = createTestStock(change = "+2.34")

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("+2.34")
            .assertIsDisplayed()
    }

    @Test
    fun stockRow_displaysChangePercent() {
        // Given
        val stock = createTestStock(changePercent = "+1.33%")

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("+1.33%")
            .assertIsDisplayed()
    }

    @Test
    fun stockRow_showsUpArrow_whenPriceIncreases() {
        // Given
        val stock = createTestStock(
            priceDirection = PriceDirection.UP,
            change = "+2.34"
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Price up")
            .assertIsDisplayed()
    }

    @Test
    fun stockRow_showsDownArrow_whenPriceDecreases() {
        // Given
        val stock = createTestStock(
            priceDirection = PriceDirection.DOWN,
            change = "-1.45"
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Price down")
            .assertIsDisplayed()
    }

    @Test
    fun stockRow_displaysMultipleStocksCorrectly() {
        // Given
        val stocks = listOf(
            createTestStock(symbol = "AAPL", price = "$178.50"),
            createTestStock(symbol = "GOOG", price = "$142.30"),
            createTestStock(symbol = "TSLA", price = "$238.72")
        )

        // When
        composeTestRule.setContent {
            RealTimePriceTrackerTheme {
                androidx.compose.foundation.layout.Column {
                    stocks.forEach { stock ->
                        StockRow(stock = stock)
                    }
                }
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