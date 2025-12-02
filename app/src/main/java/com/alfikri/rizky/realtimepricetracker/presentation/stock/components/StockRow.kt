package com.alfikri.rizky.realtimepricetracker.presentation.stock.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.FlashColor
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.PriceDirection
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.StockUiModel
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme

/**
 * Stock row component displaying stock information
 * Matches Figma design with symbol, price, change, and change percentage
 * Includes flash animation on price updates
 */
@Composable
fun StockRow(
    stock: StockUiModel,
    modifier: Modifier = Modifier
) {
    // Animate background color for flash effect with reduced opacity
    val backgroundColor by animateColorAsState(
        targetValue = when (stock.flashColor) {
            FlashColor.Green -> Color(0xFF4ADE80).copy(alpha = 0.3f)
            FlashColor.Red -> Color(0xFFF87171).copy(alpha = 0.3f)
            FlashColor.None -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 500),
        label = "flashAnimation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stock.symbol,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stock.price,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            PriceChangeIndicator(
                change = stock.change,
                direction = stock.priceDirection,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stock.changePercent,
                style = MaterialTheme.typography.bodyMedium,
                color = when (stock.priceDirection) {
                    PriceDirection.UP -> Color(0xFF16A34A) // Green-600
                    PriceDirection.DOWN -> Color(0xFFDC2626) // Red-600
                    PriceDirection.UNCHANGED -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Divider
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 1.dp
        )
    }
}

/**
 * Price change indicator with arrow icon
 */
@Composable
private fun PriceChangeIndicator(
    change: String,
    direction: PriceDirection,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Show arrow icon for up or down
        when (direction) {
            PriceDirection.UP -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = "Price up",
                    tint = Color(0xFF16A34A), // Green-600
                    modifier = Modifier.size(16.dp)
                )
            }

            PriceDirection.DOWN -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = "Price down",
                    tint = Color(0xFFDC2626), // Red-600
                    modifier = Modifier.size(16.dp)
                )
            }

            PriceDirection.UNCHANGED -> {
                // No icon for unchanged
            }
        }

        // Change amount text
        Text(
            text = change,
            style = MaterialTheme.typography.bodyMedium,
            color = when (direction) {
                PriceDirection.UP -> Color(0xFF16A34A)
                PriceDirection.DOWN -> Color(0xFFDC2626)
                PriceDirection.UNCHANGED -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontSize = 14.sp,
            modifier = Modifier.padding(start = if (direction != PriceDirection.UNCHANGED) 4.dp else 0.dp)
        )
    }
}

// Preview for StockRow
@Preview(showBackground = true)
@Composable
private fun StockRowPreviewUp() {
    RealTimePriceTrackerTheme {
        StockRow(
            stock = StockUiModel(
                symbol = "AAPL",
                price = "$178.50",
                change = "+2.34",
                changePercent = "+1.33%",
                priceDirection = PriceDirection.UP,
                flashColor = FlashColor.None,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StockRowPreviewDown() {
    RealTimePriceTrackerTheme {
        StockRow(
            stock = StockUiModel(
                symbol = "GOOG",
                price = "$142.30",
                change = "-1.45",
                changePercent = "-1.01%",
                priceDirection = PriceDirection.DOWN,
                flashColor = FlashColor.None,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StockRowPreviewFlashGreen() {
    RealTimePriceTrackerTheme {
        StockRow(
            stock = StockUiModel(
                symbol = "TSLA",
                price = "$238.72",
                change = "+5.67",
                changePercent = "+2.43%",
                priceDirection = PriceDirection.UP,
                flashColor = FlashColor.Green,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}