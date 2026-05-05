package com.example.market_data_dashboard.view

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun CoinChart(
    modifier: Modifier = Modifier,
    dataPoints: List<Pair<Float, Float>>
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = Color.GRAY
                }

                axisRight.isEnabled = false
                axisLeft.apply {
                    setDrawGridLines(true)
                    textColor = Color.GRAY
                }
            }
        },
        update = { chart ->
            if (dataPoints.isNotEmpty()) {
                val entries = dataPoints.map { Entry(it.first, it.second) }

                val dataSet = LineDataSet(entries, "Cena").apply {
                    color = Color.parseColor("#4CAF50")
                    setDrawCircles(false)
                    lineWidth = 2f
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }

                chart.data = LineData(dataSet)
                chart.invalidate()
            } else {
                chart.clear()
            }
        }
    )
}