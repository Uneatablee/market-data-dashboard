package com.example.market_data_dashboard.view

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
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


//Example LineChart view

@Composable
fun SpecificAssetScreen (
    entries: List<Entry>,
    modifier: Modifier = Modifier){

    AndroidView(
        modifier = Modifier,
        factory = {context ->
            LineChart(context).apply{

            }
        },
        update = {chart ->

            if(entries.isNotEmpty()) {
                val dataSet = LineDataSet(entries, "Moje Dane").apply{}

                chart.data = LineData(dataSet)
                chart.invalidate()
            }
        }
    )

}