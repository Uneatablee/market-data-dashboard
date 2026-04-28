package com.example.market_data_dashboard.model

data class MarketState(
    val isLoading: Boolean = false,
    val coins: List<CoinData> = emptyList(),
    val error: String? = null
)