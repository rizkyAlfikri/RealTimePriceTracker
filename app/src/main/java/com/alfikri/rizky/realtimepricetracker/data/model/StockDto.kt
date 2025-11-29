package com.alfikri.rizky.realtimepricetracker.data.model

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.RoundingMode
import kotlin.random.Random

/**
 * Data Transfer Object for Stock data
 * Used for JSON serialization/deserialization with WebSocket
 */
@Serializable
data class StockDto(
    val symbol: String,
    val price: Double,
    @SerialName("previous_price")
    val previousPrice: Double,
    val growth: String,
    @SerialName("icon_url")
    val iconUrl: String,
    val timestamp: Long
) {
    /**
     * Converts DTO to Domain model
     */
    fun toDomain(): Stock {
        return Stock(
            symbol = symbol,
            price = price,
            previousPrice = previousPrice,
            growth = growth,
            iconUrl = iconUrl,
            timestamp = timestamp
        )
    }

    /**
     * Creates a new StockDto with randomly changed price
     * Price change range: -2% to +2%
     */
    fun withRandomPriceChange(): StockDto {
        // Random change between -2% and +2%
        val changePercent = (Random.nextDouble() - 0.5) * 4

        // Calculate new price with rounding
        val newPrice = (price * (1 + changePercent / 100))
            .toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        // Calculate growth percentage
        val growthValue = ((newPrice - previousPrice) / previousPrice * 100)
        val growthString = if (growthValue >= 0) {
            "+${String.format("%.2f", growthValue)}%"
        } else {
            "${String.format("%.2f", growthValue)}%"
        }

        return copy(
            previousPrice = price,
            price = newPrice,
            growth = growthString,
            timestamp = System.currentTimeMillis()
        )
    }

    companion object {
        /**
         * Creates a StockDto from Domain model
         */
        fun fromDomain(stock: Stock): StockDto {
            return StockDto(
                symbol = stock.symbol,
                price = stock.price,
                previousPrice = stock.previousPrice,
                growth = stock.growth,
                iconUrl = stock.iconUrl,
                timestamp = stock.timestamp
            )
        }
    }
}
