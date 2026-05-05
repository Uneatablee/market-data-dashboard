package com.example.market_data_dashboard.model

data class MarketState(
    val isLoading: Boolean = false,
    val coins: List<CoinData> = emptyList(),
    val error: String? = null,

    //coin selected and corespondent chart loading
    val selectedCoin: String? = null,
    val isHistoryLoading: Boolean = false,
    val historicalData: List<Pair<Float, Float>> = emptyList(),

    //Currently chosen interval buttons
    val selectedInterval: String = "24h",
    val priceChangePercent: Float? = null
)

