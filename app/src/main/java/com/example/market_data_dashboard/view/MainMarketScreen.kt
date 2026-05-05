package com.example.market_data_dashboard.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape

import com.example.market_data_dashboard.model.CoinData
import com.example.market_data_dashboard.intent.MarketIntent
import com.example.market_data_dashboard.utils.formatAsCryptoPrice
import com.example.market_data_dashboard.viewmodel.MarketViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.market_data_dashboard.model.MarketState
import java.util.Locale

@Composable
fun MarketMainScreen(
    viewModel: MarketViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(MarketIntent.FetchMarketData)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.selectedCoin != null) {

            CoinDetailScreen(state = state, viewModel = viewModel)

        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else if (state.error != null) {
                        Text(text = "Błąd: ${state.error}", color = MaterialTheme.colorScheme.error)
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            CategoryFilterRow()

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.coins) { coin ->
                                    CoinItem(
                                        coin = coin,
                                        onClick = {
                                            viewModel.handleIntent(MarketIntent.CoinClicked(coin.symbol))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoinItem(
    coin: CoinData,
    onClick: () -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = coin.symbol,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${coin.price.formatAsCryptoPrice()} $",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun CategoryTile(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun CategoryFilterRow() {
    val categories = listOf("Crypto", "ETF")

    var selectedCategory by remember { mutableStateOf(categories[0]) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            CategoryTile(
                title = category,
                isSelected = category == selectedCategory,
                onClick = {selectedCategory = category}
            )
        }
    }
}

//@Composable
//fun CoinDetailScreen(state: MarketState, viewModel: MarketViewModel) {
//    Column(modifier = Modifier.fillMaxSize()) {
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Button(onClick = { viewModel.handleIntent(MarketIntent.GoBack) }) {
//                Text("<- Back")
//            }
//            Spacer(modifier = Modifier.width(16.dp))
//            Text(
//                text = "${state.selectedCoin}",
//                style = MaterialTheme.typography.titleLarge
//            )
//        }
//
//        Box(
//            modifier = Modifier.fillMaxWidth().height(300.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            if (state.isHistoryLoading) {
//                CircularProgressIndicator()
//            } else if (state.historicalData.isNotEmpty()) {
//                CoinChart(
//                    modifier = Modifier.padding(16.dp),
//                    dataPoints = state.historicalData
//                )
//            }
//        }
//    }
//}

@Composable
fun CoinDetailScreen(state: MarketState, viewModel: MarketViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Text(
                text = "${state.selectedCoin}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!state.isHistoryLoading && state.priceChangePercent != null) {
                val isPositive = state.priceChangePercent >= 0
                val textColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFE53935)
                val sign = if (isPositive) "+" else ""

                Text(
                    text = "$sign${String.format(Locale.US, "%.2f", state.priceChangePercent)}%",
                    color = textColor,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val intervals = listOf("24h", "1W", "1M")
            intervals.forEach { interval ->
                val isSelected = state.selectedInterval == interval

                OutlinedButton(
                    onClick = { viewModel.handleIntent(MarketIntent.ChangeChartInterval(interval)) },
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = interval,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isHistoryLoading) {
                CircularProgressIndicator()
            } else if (state.historicalData.isNotEmpty()) {
                CoinChart(
                    modifier = Modifier.fillMaxSize(),
                    dataPoints = state.historicalData
                )
            }
        }

        MarketStatsGrid("5349.56", "4019.98", "$20B")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.handleIntent(MarketIntent.GoBack) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("<- Asset List")
        }
    }
}

@Composable
fun MarketStatsGrid(high: String, low: String, volume: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("24h Stats", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                StatItem(label = "Highest price", value = "$high $", modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                StatItem(label = "Something", value = "Something", modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                StatItem(label = "Something", value = "Something", modifier = Modifier)
            }
            Column(modifier = Modifier.weight(1f)) {
                StatItem(label = "Lowest price", value = "$low $", modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                StatItem(label = "Total trading volume", value = volume, modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                StatItem(label = "Something", value = "Something", modifier = Modifier)
            }
        }
    }
}


@Composable
fun StatItem(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ){
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ){
            Text(text = label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}