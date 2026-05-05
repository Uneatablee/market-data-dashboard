package com.example.market_data_dashboard.network

import retrofit2.http.GET
import com.example.market_data_dashboard.model.CoinData
import retrofit2.http.Query

interface BinanceApiService {
    @GET("api/v3/ticker/price")
    suspend fun getLatestPrices(): List<CoinData>

    @GET("api/v3/klines")
    suspend fun getCoinHistory(
        @Query("symbol") symbol: String,

        //1h Interval and 24h split to modify later
        @Query("interval") interval: String = "1h",
        @Query("limit") limit: Int = 24
    ): List<List<String>>
}
