package com.alfikri.rizky.realtimepricetracker.presentation.stock.model

import com.alfikri.rizky.realtimepricetracker.domain.model.Stock

/**
 * UI model for Stock display
 * Includes flash color for visual feedback
 */
data class StockUiModel(
    val symbol: String,
    val price: String,
    val change: String,
    val changePercent: String,
    val priceDirection: PriceDirection,
    val flashColor: FlashColor = FlashColor.None,
    val timestamp: Long
)

enum class PriceDirection {
    UP, DOWN, UNCHANGED
}

enum class FlashColor {
    None, Green, Red
}

/**
 * Mapper from domain model to UI model
 */
fun Stock.toUiModel(flashColor: FlashColor = FlashColor.None): StockUiModel {
    val changeAmount = priceChangeAmount
    val changePercent = if (previousPrice > 0) {
        ((price - previousPrice) / previousPrice) * 100
    } else {
        0.0
    }

    val direction = when {
        changeAmount > 0 -> PriceDirection.UP
        changeAmount < 0 -> PriceDirection.DOWN
        else -> PriceDirection.UNCHANGED
    }

    return StockUiModel(
        symbol = symbol,
        price = "$%.2f".format(price),
        change = "${if (changeAmount > 0) "+" else ""}%.2f".format(changeAmount),
        changePercent = "${if (changePercent > 0) "+" else ""}%.2f%%".format(changePercent),
        priceDirection = direction,
        flashColor = flashColor,
        timestamp = timestamp
    )
}