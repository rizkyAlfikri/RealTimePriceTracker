package com.alfikri.rizky.realtimepricetracker.presentation.stock

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.alfikri.rizky.realtimepricetracker.presentation.stock.components.StockList
import com.alfikri.rizky.realtimepricetracker.presentation.stock.components.TopBar
import kotlinx.coroutines.flow.collectLatest

/**
 * Main Stock screen composable
 * Displays real-time stock prices with MVI pattern
 */
@Composable
fun StockScreen(
    viewModel: StockViewModel,
    modifier: Modifier = Modifier
) {
    // Collect state from ViewModel
    val state by viewModel.state.collectAsState()

    // Snackbar state for showing messages
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Handle one-time effects
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is StockContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        withDismissAction = true
                    )
                }
                is StockContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Main UI
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBar(
                isConnected = state.isConnected,
                isActive = state.isActive,
                isDarkTheme = state.isDarkTheme,
                onToggleTheme = {
                    viewModel.handleIntent(StockContract.Intent.ToggleTheme)
                },
                onToggleFeed = {
                    viewModel.handleIntent(StockContract.Intent.TogglePriceFeed)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stock list
            StockList(
                stocks = state.stocks,
                isLoading = state.isLoading
            )
        }
    }
}