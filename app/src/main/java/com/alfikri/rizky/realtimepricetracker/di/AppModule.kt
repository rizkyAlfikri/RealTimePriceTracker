package com.alfikri.rizky.realtimepricetracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.alfikri.rizky.realtimepricetracker.data.repository.StockRepositoryImpl
import com.alfikri.rizky.realtimepricetracker.data.repository.preferences.ThemePreferencesRepositoryImpl
import com.alfikri.rizky.realtimepricetracker.data.websocket.StockDataGenerator
import com.alfikri.rizky.realtimepricetracker.data.websocket.WebSocketClient
import com.alfikri.rizky.realtimepricetracker.domain.repository.StockRepository
import com.alfikri.rizky.realtimepricetracker.domain.repository.preferences.ThemePreferencesRepository
import com.alfikri.rizky.realtimepricetracker.domain.usecase.StockUseCase
import com.alfikri.rizky.realtimepricetracker.domain.usecase.preferences.ThemePreferencesUseCase
import com.alfikri.rizky.realtimepricetracker.presentation.stock.StockViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * DataStore instance using property delegation
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * Koin module for data layer dependencies
 */
val dataModule = module {
    // DataStore
    single<DataStore<Preferences>> {
        androidContext().dataStore
    }

    // OkHttp client for WebSocket
    single {
        okhttp3.OkHttpClient.Builder()
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .pingInterval(20, java.util.concurrent.TimeUnit.SECONDS) // Keep connection alive
            .retryOnConnectionFailure(true)
            .build()
    }

    // Kotlinx Serialization Json
    single {
        kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }

    // WebSocket
    single { StockDataGenerator() }
    single { WebSocketClient(get(), get(), get()) }

    // Repositories
    single<StockRepository> { StockRepositoryImpl(get()) }
    single<ThemePreferencesRepository> { ThemePreferencesRepositoryImpl(get()) }
}

/**
 * Koin module for domain layer dependencies
 */
val domainModule = module {
    // Use cases
    factory { StockUseCase(get()) }
    factory { ThemePreferencesUseCase(get()) }
}

/**
 * Koin module for presentation layer dependencies
 */
val presentationModule = module {
    // ViewModels
    viewModel { StockViewModel(get(), get()) }
}

/**
 * List of all Koin modules
 */
val allModules = listOf(
    dataModule,
    domainModule,
    presentationModule
)