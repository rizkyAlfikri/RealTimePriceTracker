package com.alfikri.rizky.realtimepricetracker.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Basic tests for Stock model
class StockTest {

    @Test
    fun `priceChange returns INCREASE when price is higher than previousPrice`() {
        // Given
        val stock = Stock(
            symbol = "AAPL",
            price = 175.50,
            previousPrice = 170.00,
            growth = "+3.24%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val priceChange = stock.priceChange

        // Then
        assertThat(priceChange).isEqualTo(PriceChange.INCREASE)
    }

    @Test
    fun `priceChange returns DECREASE when price is lower than previousPrice`() {
        // Given
        val stock = Stock(
            symbol = "GOOGL",
            price = 135.00,
            previousPrice = 140.00,
            growth = "-3.57%",
            iconUrl = "https://example.com/googl.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val priceChange = stock.priceChange

        // Then
        assertThat(priceChange).isEqualTo(PriceChange.DECREASE)
    }

    @Test
    fun `priceChange returns UNCHANGED when price equals previousPrice`() {
        // Given
        val stock = Stock(
            symbol = "MSFT",
            price = 380.00,
            previousPrice = 380.00,
            growth = "0.00%",
            iconUrl = "https://example.com/msft.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val priceChange = stock.priceChange

        // Then
        assertThat(priceChange).isEqualTo(PriceChange.UNCHANGED)
    }

    @Test
    fun `formattedPrice returns price with dollar sign and two decimal places`() {
        // Given
        val stock = Stock(
            symbol = "AMZN",
            price = 180.5,
            previousPrice = 175.0,
            growth = "+3.14%",
            iconUrl = "https://example.com/amzn.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val formattedPrice = stock.formattedPrice

        // Then
        assertThat(formattedPrice).isEqualTo("$180.50")
    }

    @Test
    fun `formattedPrice handles whole numbers correctly`() {
        // Given
        val stock = Stock(
            symbol = "NVDA",
            price = 500.0,
            previousPrice = 495.0,
            growth = "+1.01%",
            iconUrl = "https://example.com/nvda.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val formattedPrice = stock.formattedPrice

        // Then
        assertThat(formattedPrice).isEqualTo("$500.00")
    }

    @Test
    fun `priceChangeAmount returns positive value when price increases`() {
        // Given
        val stock = Stock(
            symbol = "TSLA",
            price = 250.75,
            previousPrice = 245.50,
            growth = "+2.14%",
            iconUrl = "https://example.com/tsla.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val changeAmount = stock.priceChangeAmount

        // Then
        assertThat(changeAmount).isWithin(0.01).of(5.25)
    }

    @Test
    fun `priceChangeAmount returns negative value when price decreases`() {
        // Given
        val stock = Stock(
            symbol = "META",
            price = 495.00,
            previousPrice = 505.00,
            growth = "-1.98%",
            iconUrl = "https://example.com/meta.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val changeAmount = stock.priceChangeAmount

        // Then
        assertThat(changeAmount).isWithin(0.01).of(-10.00)
    }

    @Test
    fun `priceChangeAmount returns zero when price unchanged`() {
        // Given
        val stock = Stock(
            symbol = "JPM",
            price = 170.00,
            previousPrice = 170.00,
            growth = "0.00%",
            iconUrl = "https://example.com/jpm.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val changeAmount = stock.priceChangeAmount

        // Then
        assertThat(changeAmount).isEqualTo(0.0)
    }

    @Test
    fun `stock data class equality works correctly`() {
        // Given
        val stock1 = Stock(
            symbol = "AAPL",
            price = 175.00,
            previousPrice = 170.00,
            growth = "+2.94%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = 1234567890L
        )
        val stock2 = Stock(
            symbol = "AAPL",
            price = 175.00,
            previousPrice = 170.00,
            growth = "+2.94%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = 1234567890L
        )

        // Then
        assertThat(stock1).isEqualTo(stock2)
    }

    @Test
    fun `stock copy function works correctly`() {
        // Given
        val original = Stock(
            symbol = "AAPL",
            price = 175.00,
            previousPrice = 170.00,
            growth = "+2.94%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = 1234567890L
        )

        // When
        val copied = original.copy(price = 180.00, growth = "+5.88%")

        // Then
        assertThat(copied.symbol).isEqualTo("AAPL")
        assertThat(copied.price).isEqualTo(180.00)
        assertThat(copied.previousPrice).isEqualTo(170.00)
        assertThat(copied.growth).isEqualTo("+5.88%")
    }

}