package com.example.groww_assignment.presentation.util

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale


fun Double.formatAsPrice(): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(this)
}



fun getMarketStatusMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour in 9..16 -> "Market Open"
        hour < 9 -> "Pre-Market"
        else -> "After Hours"
    }
}

fun isMarketOpen(): Boolean {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    return dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY && hour in 9..16
}
