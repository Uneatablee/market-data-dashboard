package com.example.market_data_dashboard.model

data class CoinState(
    val isLoading: Boolean = false,
    val coinSymbol : String,
    val coinDayData : List<String> = emptyList(),
    val error: String? = null
)