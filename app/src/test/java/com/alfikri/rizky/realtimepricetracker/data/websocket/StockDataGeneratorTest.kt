package com.alfikri.rizky.realtimepricetracker.data.websocket

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for StockDataGenerator
 * Tests stock data generation and initialization
 */
class StockDataGeneratorTest {

    private lateinit var generator: StockDataGenerator

    @Before
    fun setup() {
        generator = StockDataGenerator()
    }

    // ========== Stock Generation Tests ==========

    @Test
    fun `generateInitialStocks returns 30 stocks`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        assertThat(stocks).hasSize(30)
    }

    @Test
    fun `generated stocks have unique symbols`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        val symbols = stocks.map { it.symbol }
        val uniqueSymbols = symbols.toSet()
        assertThat(uniqueSymbols).hasSize(30)
    }

    @Test
    fun `generated stocks contain expected symbols`() {
        // When
        val stocks = generator.generateInitialStocks()
        val symbols = stocks.map { it.symbol }.toSet()

        // Then - Check all 30 expected symbols
        val expectedSymbols = setOf(
            "AAPL", "GOOGL", "MSFT", "AMZN", "NVDA",
            "META", "TSLA", "BRK.B", "JPM", "V",
            "JNJ", "WMT", "PG", "MA", "HD",
            "CVX", "MRK", "ABBV", "PEP", "KO",
            "COST", "AVGO", "TMO", "MCD", "CSCO",
            "NFLX", "ADBE", "CRM", "INTC", "AMD"
        )

        assertThat(symbols).containsExactlyElementsIn(expectedSymbols)
    }

    @Test
    fun `generated stocks have correct base prices`() {
        // When
        val stocks = generator.generateInitialStocks()
        val stockMap = stocks.associateBy { it.symbol }

        // Then - Verify specific base prices from architecture doc
        assertThat(stockMap["AAPL"]?.price).isEqualTo(175.0)
        assertThat(stockMap["GOOGL"]?.price).isEqualTo(140.0)
        assertThat(stockMap["MSFT"]?.price).isEqualTo(380.0)
        assertThat(stockMap["AMZN"]?.price).isEqualTo(180.0)
        assertThat(stockMap["NVDA"]?.price).isEqualTo(480.0)
        assertThat(stockMap["META"]?.price).isEqualTo(500.0)
        assertThat(stockMap["TSLA"]?.price).isEqualTo(250.0)
        assertThat(stockMap["KO"]?.price).isEqualTo(60.0)
        assertThat(stockMap["AVGO"]?.price).isEqualTo(900.0)
        assertThat(stockMap["CSCO"]?.price).isEqualTo(50.0)
    }

    @Test
    fun `initial stocks have price equal to previousPrice`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        stocks.forEach { stock ->
            assertThat(stock.price).isEqualTo(stock.previousPrice)
        }
    }

    @Test
    fun `initial stocks have zero growth`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        stocks.forEach { stock ->
            assertThat(stock.growth).isEqualTo("0.00%")
        }
    }

    @Test
    fun `generated stocks have valid icon URLs`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        stocks.forEach { stock ->
            assertThat(stock.iconUrl).startsWith("https://logo.clearbit.com/")
            assertThat(stock.iconUrl).isNotEmpty()
        }
    }

    @Test
    fun `icon URLs use correct company domains`() {
        // When
        val stocks = generator.generateInitialStocks()
        val stockMap = stocks.associateBy { it.symbol }

        // Then - Verify specific domains
        assertThat(stockMap["AAPL"]?.iconUrl).isEqualTo("https://logo.clearbit.com/apple.com")
        assertThat(stockMap["GOOGL"]?.iconUrl).isEqualTo("https://logo.clearbit.com/google.com")
        assertThat(stockMap["MSFT"]?.iconUrl).isEqualTo("https://logo.clearbit.com/microsoft.com")
        assertThat(stockMap["AMZN"]?.iconUrl).isEqualTo("https://logo.clearbit.com/amazon.com")
        assertThat(stockMap["TSLA"]?.iconUrl).isEqualTo("https://logo.clearbit.com/tesla.com")
        assertThat(stockMap["META"]?.iconUrl).isEqualTo("https://logo.clearbit.com/meta.com")
    }

    @Test
    fun `generated stocks have timestamps`() {
        // Given
        val timeBefore = System.currentTimeMillis()

        // When
        val stocks = generator.generateInitialStocks()

        val timeAfter = System.currentTimeMillis()

        // Then
        stocks.forEach { stock ->
            assertThat(stock.timestamp).isAtLeast(timeBefore)
            assertThat(stock.timestamp).isAtMost(timeAfter)
        }
    }

    @Test
    fun `all generated stocks have positive prices`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        stocks.forEach { stock ->
            assertThat(stock.price).isGreaterThan(0.0)
        }
    }

    @Test
    fun `stocks are generated in consistent order`() {
        // When
        val stocks1 = generator.generateInitialStocks()
        val stocks2 = generator.generateInitialStocks()

        // Then - Order should be consistent
        val symbols1 = stocks1.map { it.symbol }
        val symbols2 = stocks2.map { it.symbol }

        assertThat(symbols1).isEqualTo(symbols2)
    }

    @Test
    fun `first stock is AAPL`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        assertThat(stocks.first().symbol).isEqualTo("AAPL")
    }

    @Test
    fun `last stock is AMD`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        assertThat(stocks.last().symbol).isEqualTo("AMD")
    }

    // ========== Stock Data Validation ==========

    @Test
    fun `all tech stocks are included`() {
        // When
        val stocks = generator.generateInitialStocks()
        val symbols = stocks.map { it.symbol }.toSet()

        // Then
        val techStocks = setOf(
            "AAPL", "GOOGL", "MSFT", "AMZN", "NVDA",
            "META", "CSCO", "NFLX", "ADBE", "CRM",
            "INTC", "AMD", "AVGO"
        )

        assertThat(symbols).containsAtLeastElementsIn(techStocks)
    }

    @Test
    fun `all finance stocks are included`() {
        // When
        val stocks = generator.generateInitialStocks()
        val symbols = stocks.map { it.symbol }.toSet()

        // Then
        val financeStocks = setOf("JPM", "V", "MA", "BRK.B")
        assertThat(symbols).containsAtLeastElementsIn(financeStocks)
    }

    @Test
    fun `all consumer stocks are included`() {
        // When
        val stocks = generator.generateInitialStocks()
        val symbols = stocks.map { it.symbol }.toSet()

        // Then
        val consumerStocks = setOf("KO", "PEP", "WMT", "COST", "MCD", "PG")
        assertThat(symbols).containsAtLeastElementsIn(consumerStocks)
    }

    @Test
    fun `all health stocks are included`() {
        // When
        val stocks = generator.generateInitialStocks()
        val symbols = stocks.map { it.symbol }.toSet()

        // Then
        val healthStocks = setOf("JNJ", "MRK", "ABBV", "TMO")
        assertThat(symbols).containsAtLeastElementsIn(healthStocks)
    }

    @Test
    fun `price range is realistic`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then - Prices should be between $40 and $1000
        stocks.forEach { stock ->
            assertThat(stock.price).isAtLeast(40.0)
            assertThat(stock.price).isAtMost(1000.0)
        }
    }

    @Test
    fun `lowest priced stock is INTC at $45`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        val lowestPrice = stocks.minByOrNull { it.price }
        assertThat(lowestPrice?.symbol).isEqualTo("INTC")
        assertThat(lowestPrice?.price).isEqualTo(45.0)
    }

    @Test
    fun `highest priced stock is AVGO at $900`() {
        // When
        val stocks = generator.generateInitialStocks()

        // Then
        val highestPrice = stocks.maxByOrNull { it.price }
        assertThat(highestPrice?.symbol).isEqualTo("AVGO")
        assertThat(highestPrice?.price).isEqualTo(900.0)
    }

    // ========== Multiple Generation Tests ==========

    @Test
    fun `multiple generations produce same data`() {
        // When
        val stocks1 = generator.generateInitialStocks()
        val stocks2 = generator.generateInitialStocks()

        // Then - Symbols and prices should match (timestamps may differ slightly)
        stocks1.forEachIndexed { index, stock1 ->
            val stock2 = stocks2[index]
            assertThat(stock2.symbol).isEqualTo(stock1.symbol)
            assertThat(stock2.price).isEqualTo(stock1.price)
            assertThat(stock2.previousPrice).isEqualTo(stock1.previousPrice)
            assertThat(stock2.growth).isEqualTo(stock1.growth)
            assertThat(stock2.iconUrl).isEqualTo(stock1.iconUrl)
        }
    }

    @Test
    fun `generator can be reused`() {
        // When
        val stocks1 = generator.generateInitialStocks()
        val stocks2 = generator.generateInitialStocks()
        val stocks3 = generator.generateInitialStocks()

        // Then
        assertThat(stocks1).hasSize(30)
        assertThat(stocks2).hasSize(30)
        assertThat(stocks3).hasSize(30)
    }

    // ========== Stock with Special Characters ==========

    @Test
    fun `handles stock symbol with dot correctly`() {
        // When
        val stocks = generator.generateInitialStocks()
        val brkStock = stocks.find { it.symbol == "BRK.B" }

        // Then
        assertThat(brkStock).isNotNull()
        assertThat(brkStock?.symbol).isEqualTo("BRK.B")
        assertThat(brkStock?.price).isEqualTo(360.0)
    }
}