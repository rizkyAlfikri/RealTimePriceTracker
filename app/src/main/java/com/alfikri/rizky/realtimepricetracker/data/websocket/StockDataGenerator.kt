package com.alfikri.rizky.realtimepricetracker.data.websocket

import com.alfikri.rizky.realtimepricetracker.data.model.StockDto

// Generate mock data for the 30 stocks we're tracking
class StockDataGenerator {

    // TODO: maybe add more stocks later?
    fun generateInitialStocks(): List<StockDto> {
        return listOf(
            createStock("AAPL", 175.0, "Apple Inc."),
            createStock("GOOGL", 140.0, "Alphabet Inc."),
            createStock("MSFT", 380.0, "Microsoft Corp."),
            createStock("AMZN", 180.0, "Amazon.com Inc."),
            createStock("NVDA", 480.0, "NVIDIA Corp."),
            createStock("META", 500.0, "Meta Platforms"),
            createStock("TSLA", 250.0, "Tesla Inc."),
            createStock("BRK.B", 360.0, "Berkshire Hathaway"),
            createStock("JPM", 170.0, "JPMorgan Chase"),
            createStock("V", 280.0, "Visa Inc."),
            createStock("JNJ", 160.0, "Johnson & Johnson"),
            createStock("WMT", 165.0, "Walmart Inc."),
            createStock("PG", 155.0, "Procter & Gamble"),
            createStock("MA", 450.0, "Mastercard Inc."),
            createStock("HD", 350.0, "Home Depot Inc."),
            createStock("CVX", 150.0, "Chevron Corp."),
            createStock("MRK", 125.0, "Merck & Co."),
            createStock("ABBV", 175.0, "AbbVie Inc."),
            createStock("PEP", 170.0, "PepsiCo Inc."),
            createStock("KO", 60.0, "Coca-Cola Co."),
            createStock("COST", 580.0, "Costco Wholesale"),
            createStock("AVGO", 900.0, "Broadcom Inc."),
            createStock("TMO", 530.0, "Thermo Fisher"),
            createStock("MCD", 290.0, "McDonald's Corp."),
            createStock("CSCO", 50.0, "Cisco Systems"),
            createStock("NFLX", 450.0, "Netflix Inc."),
            createStock("ADBE", 520.0, "Adobe Inc."),
            createStock("CRM", 280.0, "Salesforce Inc."),
            createStock("INTC", 45.0, "Intel Corp."),
            createStock("AMD", 120.0, "AMD Inc.")
        )
    }

    private fun createStock(symbol: String, price: Double, companyName: String): StockDto {
        return StockDto(
            symbol = symbol,
            price = price,
            previousPrice = price,
            growth = "0.00%",
            iconUrl = "https://logo.clearbit.com/${getCompanyDomain(symbol)}",
            timestamp = System.currentTimeMillis()
        )
    }

    // Map symbols to domains for clearbit logo URLs
    private fun getCompanyDomain(symbol: String): String {
        return when (symbol) {
            "AAPL" -> "apple.com"
            "GOOGL" -> "google.com"
            "MSFT" -> "microsoft.com"
            "AMZN" -> "amazon.com"
            "NVDA" -> "nvidia.com"
            "META" -> "meta.com"
            "TSLA" -> "tesla.com"
            "BRK.B" -> "berkshirehathaway.com"
            "JPM" -> "jpmorganchase.com"
            "V" -> "visa.com"
            "JNJ" -> "jnj.com"
            "WMT" -> "walmart.com"
            "PG" -> "pg.com"
            "MA" -> "mastercard.com"
            "HD" -> "homedepot.com"
            "CVX" -> "chevron.com"
            "MRK" -> "merck.com"
            "ABBV" -> "abbvie.com"
            "PEP" -> "pepsico.com"
            "KO" -> "coca-cola.com"
            "COST" -> "costco.com"
            "AVGO" -> "broadcom.com"
            "TMO" -> "thermofisher.com"
            "MCD" -> "mcdonalds.com"
            "CSCO" -> "cisco.com"
            "NFLX" -> "netflix.com"
            "ADBE" -> "adobe.com"
            "CRM" -> "salesforce.com"
            "INTC" -> "intel.com"
            "AMD" -> "amd.com"
            else -> "example.com"
        }
    }
}