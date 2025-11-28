package com.alfikri.rizky.realtimepricetracker.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for Stock domain model
 * Tests all computed properties and edge cases
 */
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
    fun `formattedPrice handles large numbers correctly`() {
        // Given
        val stock = Stock(
            symbol = "AVGO",
            price = 9999.99,
            previousPrice = 9800.00,
            growth = "+2.04%",
            iconUrl = "https://example.com/avgo.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val formattedPrice = stock.formattedPrice

        // Then
        assertThat(formattedPrice).isEqualTo("$9999.99")
    }

    @Test
    fun `formattedPrice handles small decimal values`() {
        // Given
        val stock = Stock(
            symbol = "CSCO",
            price = 50.01,
            previousPrice = 50.00,
            growth = "+0.02%",
            iconUrl = "https://example.com/csco.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val formattedPrice = stock.formattedPrice

        // Then
        assertThat(formattedPrice).isEqualTo("$50.01")
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
    fun `stock data class inequality works when symbols differ`() {
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
            symbol = "GOOGL",
            price = 175.00,
            previousPrice = 170.00,
            growth = "+2.94%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = 1234567890L
        )

        // Then
        assertThat(stock1).isNotEqualTo(stock2)
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

    @Test
    fun `priceChange handles very small price differences`() {
        // Given
        val stock = Stock(
            symbol = "KO",
            price = 60.001,
            previousPrice = 60.000,
            growth = "+0.00%",
            iconUrl = "https://example.com/ko.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val priceChange = stock.priceChange

        // Then
        assertThat(priceChange).isEqualTo(PriceChange.INCREASE)
    }

    @Test
    fun `priceChangeAmount handles very small differences accurately`() {
        // Given
        val stock = Stock(
            symbol = "PEP",
            price = 170.12,
            previousPrice = 170.10,
            growth = "+0.01%",
            iconUrl = "https://example.com/pep.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val changeAmount = stock.priceChangeAmount

        // Then
        assertThat(changeAmount).isWithin(0.001).of(0.02)
    }

    @Test
    fun `stock handles zero price correctly`() {
        // Given
        val stock = Stock(
            symbol = "TEST",
            price = 0.0,
            previousPrice = 10.0,
            growth = "-100.00%",
            iconUrl = "https://example.com/test.png",
            timestamp = System.currentTimeMillis()
        )

        // Then
        assertThat(stock.formattedPrice).isEqualTo("$0.00")
        assertThat(stock.priceChange).isEqualTo(PriceChange.DECREASE)
        assertThat(stock.priceChangeAmount).isEqualTo(-10.0)
    }

    @Test
    fun `stock handles negative growth string`() {
        // Given
        val stock = Stock(
            symbol = "AMD",
            price = 115.00,
            previousPrice = 120.00,
            growth = "-4.17%",
            iconUrl = "https://example.com/amd.png",
            timestamp = System.currentTimeMillis()
        )

        // Then
        assertThat(stock.growth).contains("-")
        assertThat(stock.priceChange).isEqualTo(PriceChange.DECREASE)
    }

    @Test
    fun `stock handles positive growth string`() {
        // Given
        val stock = Stock(
            symbol = "INTC",
            price = 48.00,
            previousPrice = 45.00,
            growth = "+6.67%",
            iconUrl = "https://example.com/intc.png",
            timestamp = System.currentTimeMillis()
        )

        // Then
        assertThat(stock.growth).contains("+")
        assertThat(stock.priceChange).isEqualTo(PriceChange.INCREASE)
    }
}