package com.example.market_data_dashboard.viewmodel

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
            is MarketIntent.CoinClicked -> fetchCoin()
        }
    }

    //    private fun loadMarketData() {
//        viewModelScope.launch {
//            _state.update { currentState ->
//                currentState.copy(isLoading = true, error = null)
//            }
//
//            delay(1500)
//
//            val fakeData = listOf(
//                CoinData(symbol = "BTCUSDT", price = "64250.50"),
//                CoinData(symbol = "ETHUSDT", price = "3410.20"),
//                CoinData(symbol = "BNBUSDT", price = "598.00")
//            )
//
//            _state.update { currentState ->
//                currentState.copy(isLoading = false, coins = fakeData)
//            }
//        }
//    }
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

    private fun fetchCoin() {
        //fetch all specific coin data
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true, error = null)
            }

            try{

            }
            catch (e: Exception){

            }
        }

        //coin state??
    }
}