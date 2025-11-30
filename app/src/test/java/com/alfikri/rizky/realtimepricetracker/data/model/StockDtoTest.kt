package com.alfikri.rizky.realtimepricetracker.data.model

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

/**
 * Tests for StockDto
 */
class StockDtoTest {

    private lateinit var json: Json

    @Before
    fun setup() {
        json = Json {
            ignoreUnknownKeys = true
            prettyPrint = false
        }
    }

    // ========== Serialization Tests ==========

    @Test
    fun testSerializationWithCorrectFieldNames() {
        val stockDto = StockDto(
            symbol = "AAPL",
            price = 175.50,
            previousPrice = 170.00,
            growth = "+3.24%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = 1234567890L
        )

        val jsonString = json.encodeToString(stockDto)

        assertThat(jsonString).contains("\"symbol\":\"AAPL\"")
        assertThat(jsonString).contains("\"previous_price\":170.0")
        assertThat(jsonString).contains("\"icon_url\":\"https://example.com/aapl.png\"")
    }

    @Test
    fun `should deserialize JSON correctly`() {
        val jsonString = """
            {
                "symbol": "GOOGL",
                "price": 140.50,
                "previous_price": 145.00,
                "growth": "-3.10%",
                "icon_url": "https://example.com/googl.png",
                "timestamp": 9876543210
            }
        """.trimIndent()

        // When
        val stockDto = json.decodeFromString<StockDto>(jsonString)

        // Then
        assertThat(stockDto.symbol).isEqualTo("GOOGL")
        assertThat(stockDto.price).isEqualTo(140.50)
        assertThat(stockDto.previousPrice).isEqualTo(145.00)
        assertThat(stockDto.growth).isEqualTo("-3.10%")
        assertThat(stockDto.iconUrl).isEqualTo("https://example.com/googl.png")
        assertThat(stockDto.timestamp).isEqualTo(9876543210L)
    }

    @Test
    fun `serialization round trip preserves data`() {
        // Given
        val original = StockDto(
            symbol = "MSFT",
            price = 380.25,
            previousPrice = 375.50,
            growth = "+1.27%",
            iconUrl = "https://example.com/msft.png",
            timestamp = 1111111111L
        )

        // When
        val jsonString = json.encodeToString(original)
        val deserialized = json.decodeFromString<StockDto>(jsonString)

        // Then
        assertThat(deserialized).isEqualTo(original)
    }

    // ========== Domain Conversion Tests ==========

    @Test
    fun `toDomain converts StockDto to Stock correctly`() {
        // Given
        val stockDto = StockDto(
            symbol = "AMZN",
            price = 180.00,
            previousPrice = 175.00,
            growth = "+2.86%",
            iconUrl = "https://example.com/amzn.png",
            timestamp = 2222222222L
        )

        // When
        val stock = stockDto.toDomain()

        // Then
        assertThat(stock.symbol).isEqualTo("AMZN")
        assertThat(stock.price).isEqualTo(180.00)
        assertThat(stock.previousPrice).isEqualTo(175.00)
        assertThat(stock.growth).isEqualTo("+2.86%")
        assertThat(stock.iconUrl).isEqualTo("https://example.com/amzn.png")
        assertThat(stock.timestamp).isEqualTo(2222222222L)
    }

    @Test
    fun `fromDomain converts Stock to StockDto correctly`() {
        // Given
        val stock = Stock(
            symbol = "NVDA",
            price = 480.75,
            previousPrice = 490.00,
            growth = "-1.89%",
            iconUrl = "https://example.com/nvda.png",
            timestamp = 3333333333L
        )

        // When
        val stockDto = StockDto.fromDomain(stock)

        // Then
        assertThat(stockDto.symbol).isEqualTo("NVDA")
        assertThat(stockDto.price).isEqualTo(480.75)
        assertThat(stockDto.previousPrice).isEqualTo(490.00)
        assertThat(stockDto.growth).isEqualTo("-1.89%")
        assertThat(stockDto.iconUrl).isEqualTo("https://example.com/nvda.png")
        assertThat(stockDto.timestamp).isEqualTo(3333333333L)
    }

    @Test
    fun `toDomain and fromDomain are inverse operations`() {
        // Given
        val originalStock = Stock(
            symbol = "TSLA",
            price = 250.50,
            previousPrice = 245.00,
            growth = "+2.24%",
            iconUrl = "https://example.com/tsla.png",
            timestamp = 4444444444L
        )

        // When
        val stockDto = StockDto.fromDomain(originalStock)
        val convertedBack = stockDto.toDomain()

        // Then
        assertThat(convertedBack).isEqualTo(originalStock)
    }

    // ========== Price Change Tests ==========

    @Test
    fun testRandomPriceChangeUpdatesValues() {
        val original = StockDto(
            symbol = "META",
            price = 500.00,
            previousPrice = 495.00,
            growth = "+1.01%",
            iconUrl = "https://example.com/meta.png",
            timestamp = 5555555555L
        )

        val updated = original.withRandomPriceChange()

        assertThat(updated.previousPrice).isEqualTo(original.price)
        assertThat(updated.price).isNotEqualTo(original.price)
    }

    @Test
    fun `price changes stay within 2 percent range`() {
        val original = StockDto(
            symbol = "AAPL",
            price = 100.00,
            previousPrice = 100.00,
            growth = "0.00%",
            iconUrl = "https://example.com/aapl.png",
            timestamp = System.currentTimeMillis()
        )

        val updates = (1..100).map { original.withRandomPriceChange() }

        // Check range -2% to +2%
        updates.forEach { updated ->
            val changePercent = ((updated.price - original.price) / original.price) * 100
            assertThat(changePercent).isAtMost(2.1)
            assertThat(changePercent).isAtLeast(-2.1)
        }
    }

    // TODO: Add test for price change with zero previousPrice
    // TODO: Test concurrent price changes

    // ========== Edge Cases ==========

    @Test
    fun `handles zero price correctly`() {
        // Given
        val stockDto = StockDto(
            symbol = "TEST",
            price = 0.0,
            previousPrice = 10.0,
            growth = "-100.00%",
            iconUrl = "https://example.com/test.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val stock = stockDto.toDomain()

        // Then
        assertThat(stock.price).isEqualTo(0.0)
    }

    @Test
    fun `handles very large prices correctly`() {
        // Given
        val stockDto = StockDto(
            symbol = "AVGO",
            price = 999999.99,
            previousPrice = 999000.00,
            growth = "+0.10%",
            iconUrl = "https://example.com/avgo.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val jsonString = json.encodeToString(stockDto)
        val deserialized = json.decodeFromString<StockDto>(jsonString)

        // Then
        assertThat(deserialized.price).isEqualTo(999999.99)
    }

    @Test
    fun `handles negative growth percentage correctly`() {
        // Given
        val stockDto = StockDto(
            symbol = "INTC",
            price = 45.00,
            previousPrice = 50.00,
            growth = "-10.00%",
            iconUrl = "https://example.com/intc.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val stock = stockDto.toDomain()

        // Then
        assertThat(stock.growth).isEqualTo("-10.00%")
    }

    @Test
    fun `data class copy works correctly`() {
        // Given
        val original = StockDto(
            symbol = "AMD",
            price = 120.00,
            previousPrice = 115.00,
            growth = "+4.35%",
            iconUrl = "https://example.com/amd.png",
            timestamp = 7777777777L
        )

        // When
        val copied = original.copy(price = 125.00, growth = "+8.70%")

        // Then
        assertThat(copied.symbol).isEqualTo("AMD")
        assertThat(copied.price).isEqualTo(125.00)
        assertThat(copied.previousPrice).isEqualTo(115.00)
        assertThat(copied.growth).isEqualTo("+8.70%")
        assertThat(copied.iconUrl).isEqualTo("https://example.com/amd.png")
        assertThat(copied.timestamp).isEqualTo(7777777777L)
    }

    @Test
    fun `multiple withRandomPriceChange calls produce different results`() {
        // Given
        val original = StockDto(
            symbol = "NFLX",
            price = 450.00,
            previousPrice = 445.00,
            growth = "+1.12%",
            iconUrl = "https://example.com/nflx.png",
            timestamp = System.currentTimeMillis()
        )

        // When
        val update1 = original.withRandomPriceChange()
        val update2 = original.withRandomPriceChange()
        val update3 = original.withRandomPriceChange()

        // Then - At least some should be different (statistically very likely)
        val prices = listOf(update1.price, update2.price, update3.price)
        val uniquePrices = prices.toSet()
        assertThat(uniquePrices.size).isGreaterThan(1)
    }
}