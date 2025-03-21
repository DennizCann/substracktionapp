package com.denizcan.substracktionapp.util

import java.text.NumberFormat
import java.util.*

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(amount)
} 