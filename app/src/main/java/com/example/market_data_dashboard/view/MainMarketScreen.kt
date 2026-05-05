package com.example.market_data_dashboard.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.market_data_dashboard.model.MarketState

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

@Composable
fun CoinDetailScreen(state: MarketState, viewModel: MarketViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Button(onClick = { viewModel.handleIntent(MarketIntent.GoBack) }) {
                Text("<- Wróć")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${state.selectedCoin} (Ostatnie 24h)",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isHistoryLoading) {
                CircularProgressIndicator()
            } else if (state.historicalData.isNotEmpty()) {
                CoinChart(
                    modifier = Modifier.padding(16.dp),
                    dataPoints = state.historicalData
                )
            }
        }
    }
}