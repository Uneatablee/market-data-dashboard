package com.example.market_data_dashboard.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.market_data_dashboard.model.CoinData
import com.example.market_data_dashboard.model.MarketState
import com.example.market_data_dashboard.intent.MarketIntent
import com.example.market_data_dashboard.network.RetrofitClient


class MarketViewModel : ViewModel() {

    private val _state = MutableStateFlow(MarketState())
    val state: StateFlow<MarketState> = _state.asStateFlow()


    fun handleIntent(intent: MarketIntent) {
        when (intent) {
            is MarketIntent.FetchMarketData -> loadMarketData()
            is MarketIntent.RefreshData -> loadMarketData()
            is MarketIntent.ChangeChartInterval -> {
                _state.value.selectedCoin?.let { symbol ->
                    fetchCoinHistory(symbol, intent.interval)
                }
            }
            is MarketIntent.CoinClicked -> fetchCoinHistory(intent.symbolString, "24h")
            is MarketIntent.GoBack -> _state.update { it.copy(selectedCoin = null) }
        }
    }

    private fun loadMarketData() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true, error = null)
            }

            try {
                val allCoins = RetrofitClient.api.getLatestPrices()

                val filteredCoins = allCoins
                    .filter { it.symbol.endsWith("USDT") }
                    //filter for clearing coins that price is lower than 5 decimal length
                    .filter { coin ->
                        if (coin.price.contains("e", ignoreCase = true)) {
                            return@filter false
                        }

                        val numericPrice = coin.price.toDoubleOrNull() ?: 0.0
                        if (numericPrice == 0.0) {
                            return@filter false
                        }

                        val decimals =
                            coin.price.substringAfter(".", missingDelimiterValue = "").trimEnd('0')
                        decimals.length <= 5
                    }
                    .take(50)

                _state.update { currentState ->
                    currentState.copy(isLoading = false, coins = filteredCoins)
                }

            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Błąd pobierania danych: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun fetchCoinHistory(symbol: String, intervalLabel: String) {
        viewModelScope.launch {
            _state.update { it.copy(
                selectedCoin = symbol,
                selectedInterval = intervalLabel,
                isHistoryLoading = true,
                historicalData = emptyList(),
                priceChangePercent = null
            ) }

            try {
                val (apiInterval, apiLimit) = when (intervalLabel) {
                    //Change possibility to choose interval mapping (point every x hours)
                    "24h" -> Pair("1h", 24)
                    "1W" -> Pair("4h", 42)
                    "1M" -> Pair("1d", 30)
                    else -> Pair("1h", 24)
                }

                val response = RetrofitClient.api.getCoinHistory(
                    symbol = symbol,
                    interval = apiInterval,
                    limit = apiLimit
                )

                val chartPoints = response.mapIndexed { index, klineData ->
                    val closePrice = klineData[4].toFloat()
                    Pair(index.toFloat(), closePrice)
                }

                var percentChange: Float? = null
                if (response.isNotEmpty()) {
                    val firstPrice = response.first()[1].toFloat()
                    val lastPrice = response.last()[4].toFloat()
                    if (firstPrice > 0) {
                        percentChange = ((lastPrice - firstPrice) / firstPrice) * 100
                    }
                }

                _state.update { it.copy(
                    isHistoryLoading = false,
                    historicalData = chartPoints,
                    priceChangePercent = percentChange
                ) }

            } catch (e: Exception) {
                _state.update { it.copy(isHistoryLoading = false) }
                Log.e("MarketViewModel", "Błąd historii: ${e.message}")
            }
        }
    }
}

