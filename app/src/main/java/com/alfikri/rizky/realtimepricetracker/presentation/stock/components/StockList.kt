package com.alfikri.rizky.realtimepricetracker.presentation.stock.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.FlashColor
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.PriceDirection
import com.alfikri.rizky.realtimepricetracker.presentation.stock.model.StockUiModel
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme

/**
 * Stock list component displaying a table of stocks
 * Matches Figma design with header and scrollable list
 */
@Composable
fun StockList(
    stocks: List<StockUiModel>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Table header
            StockListHeader()

            // Stock list content
            when {
                isLoading -> {
                    LoadingIndicator()
                }
                stocks.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(stocks, key = { it.symbol }) { stock ->
                            StockRow(stock = stock)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Table header with column titles
 */
@Composable
private fun StockListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Symbol",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Price",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Change",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Change %",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Loading indicator
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Empty state when no stocks are available
 */
@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No stocks available.\nPress Start to begin tracking.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}

// Preview for StockList
@Preview(showBackground = true)
@Composable
private fun StockListPreview() {
    RealTimePriceTrackerTheme {
        StockList(
            stocks = listOf(
                StockUiModel(
                    symbol = "AAPL",
                    price = "$178.50",
                    change = "+2.34",
                    changePercent = "+1.33%",
                    priceDirection = PriceDirection.UP,
                    flashColor = FlashColor.None,
                    timestamp = System.currentTimeMillis()
                ),
                StockUiModel(
                    symbol = "GOOG",
                    price = "$142.30",
                    change = "-1.45",
                    changePercent = "-1.01%",
                    priceDirection = PriceDirection.DOWN,
                    flashColor = FlashColor.None,
                    timestamp = System.currentTimeMillis()
                ),
                StockUiModel(
                    symbol = "TSLA",
                    price = "$238.72",
                    change = "+5.67",
                    changePercent = "+2.43%",
                    priceDirection = PriceDirection.UP,
                    flashColor = FlashColor.Green,
                    timestamp = System.currentTimeMillis()
                )
            ),
            isLoading = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StockListPreviewEmpty() {
    RealTimePriceTrackerTheme {
        StockList(
            stocks = emptyList(),
            isLoading = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StockListPreviewLoading() {
    RealTimePriceTrackerTheme {
        StockList(
            stocks = emptyList(),
            isLoading = true
        )
    }
}