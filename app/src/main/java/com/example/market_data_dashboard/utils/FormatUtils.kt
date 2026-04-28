package com.example.market_data_dashboard.utils

import java.util.Locale

fun String.formatAsCryptoPrice(): String {
    val price = this.toDoubleOrNull() ?: return this

    return if (price >= 1.0) {
        String.format(Locale.US, "%.2f", price)
    } else {
        String.format(Locale.US, "%.6f", price)
    }
}