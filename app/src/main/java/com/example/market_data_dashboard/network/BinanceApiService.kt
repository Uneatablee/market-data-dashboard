package com.example.market_data_dashboard.network

import retrofit2.http.GET
import com.example.market_data_dashboard.model.CoinData

interface BinanceApiService {
    @GET("api/v3/ticker/price")
    suspend fun getLatestPrices(): List<CoinData>
}