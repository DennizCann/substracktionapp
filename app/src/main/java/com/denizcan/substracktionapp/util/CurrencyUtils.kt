package com.denizcan.substracktionapp.util

import java.text.NumberFormat
import java.util.*

fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("tr", "TR")).apply {
        maximumFractionDigits = 0 // Kuruş göstermeyelim
    }.format(amount)
} 