package com.alfikri.rizky.realtimepricetracker

import android.app.Application
import com.alfikri.rizky.realtimepricetracker.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class for initializing dependencies
 */
class StockTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin for dependency injection
        startKoin {
            // Use Android logger for debugging
            androidLogger(Level.ERROR)

            // Provide Android context
            androidContext(this@StockTrackerApplication)

            // Load all Koin modules
            modules(allModules)
        }
    }
}