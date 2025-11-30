package com.alfikri.rizky.realtimepricetracker.domain.model

/**
 * Stock domain model with price tracking
 */
data class Stock(
    val symbol: String,
    val price: Double,
    val previousPrice: Double,
    val growth: String,
    val iconUrl: String,
    val timestamp: Long
) {
    // Calculate price change direction
    val priceChange: PriceChange
        get() = when {
            price > previousPrice -> PriceChange.INCREASE
            price < previousPrice -> PriceChange.DECREASE
            else -> PriceChange.UNCHANGED
        }

    val formattedPrice: String
        get() = "$${String.format("%.2f", price)}"

    val priceChangeAmount: Double
        get() = price - previousPrice
}

enum class PriceChange {
    INCREASE,
    DECREASE,
    UNCHANGED
}