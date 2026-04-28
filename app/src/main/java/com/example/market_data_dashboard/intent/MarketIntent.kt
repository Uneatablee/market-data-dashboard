package com.example.market_data_dashboard.intent

sealed class MarketIntent {
    object FetchMarketData : MarketIntent()
    object RefreshData : MarketIntent()

    data class CoinClicked(
        val symbolString: String
    ) : MarketIntent()
}