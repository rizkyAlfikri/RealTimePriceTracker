package com.alfikri.rizky.realtimepricetracker.domain.model

/**
 * Domain model representing a stock with real-time price data
 */
data class Stock(
    val symbol: String,
    val price: Double,
    val previousPrice: Double,
    val growth: String,
    val iconUrl: String,
    val timestamp: Long
) {
    /**
     * Determines the price change direction
     */
    val priceChange: PriceChange
        get() = when {
            price > previousPrice -> PriceChange.INCREASE
            price < previousPrice -> PriceChange.DECREASE
            else -> PriceChange.UNCHANGED
        }

    /**
     * Formatted price with currency symbol
     */
    val formattedPrice: String
        get() = "$${String.format("%.2f", price)}"

    /**
     * Calculate the absolute price change amount
     */
    val priceChangeAmount: Double
        get() = price - previousPrice
}

/**
 * Enum representing the direction of price change
 */
enum class PriceChange {
    INCREASE,
    DECREASE,
    UNCHANGED
}