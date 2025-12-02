package com.alfikri.rizky.realtimepricetracker.presentation.stock.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alfikri.rizky.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme

/**
 * Top bar component showing connection status and controls
 * Matches Figma design with connection indicator, theme toggle, and start/stop button
 */
@Composable
fun TopBar(
    isConnected: Boolean,
    isActive: Boolean,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onToggleFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Add status bar padding
            Spacer(modifier = Modifier.statusBarsPadding())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            // Left side: Connection status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Connection status dot
                ConnectionStatusIndicator(isConnected = isConnected)

                Spacer(modifier = Modifier.width(8.dp))

                // Connection status text
                Text(
                    text = if (isConnected) "Connected" else "Disconnected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            // Right side: Theme toggle and Start/Stop button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // Theme toggle button
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggle = onToggleTheme
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Start/Stop button
                StartStopButton(
                    isActive = isActive,
                    onToggle = onToggleFeed
                )
            }
        }
        }
    }
}

/**
 * Connection status indicator dot
 * Green when connected, red when disconnected
 */
@Composable
private fun ConnectionStatusIndicator(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val indicatorColor = if (isConnected) {
        Color(0xFF22C55E) // Green-500
    } else {
        Color(0xFFEF4444) // Red-500
    }

    Spacer(
        modifier = modifier
            .size(12.dp)
            .background(color = indicatorColor, shape = CircleShape)
    )
}

/**
 * Theme toggle button (Sun for dark mode, Moon for light mode)
 */
@Composable
private fun ThemeToggleButton(
    isDarkTheme: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (isDarkTheme) "Switch to light mode" else "Switch to dark mode",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Start/Stop button
 * Green when stopped, red when active
 */
@Composable
private fun StartStopButton(
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColor = if (isActive) {
        Color(0xFFEF4444) // Red-500
    } else {
        Color(0xFF22C55E) // Green-500
    }

    val hoverColor = if (isActive) {
        Color(0xFFDC2626) // Red-600
    } else {
        Color(0xFF16A34A) // Green-600
    }

    Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White
        ),
        modifier = modifier
    ) {
        Text(
            text = if (isActive) "Stop" else "Start",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

// Preview for TopBar
@Preview(showBackground = true)
@Composable
private fun TopBarPreviewConnected() {
    RealTimePriceTrackerTheme {
        TopBar(
            isConnected = true,
            isActive = true,
            isDarkTheme = false,
            onToggleTheme = {},
            onToggleFeed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TopBarPreviewDisconnected() {
    RealTimePriceTrackerTheme {
        TopBar(
            isConnected = false,
            isActive = false,
            isDarkTheme = false,
            onToggleTheme = {},
            onToggleFeed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TopBarPreviewDark() {
    RealTimePriceTrackerTheme(darkTheme = true) {
        TopBar(
            isConnected = true,
            isActive = true,
            isDarkTheme = true,
            onToggleTheme = {},
            onToggleFeed = {}
        )
    }
}