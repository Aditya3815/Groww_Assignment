package com.example.groww_assignment.domain.util

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.ui.theme.GreenPositive
import com.example.groww_assignment.ui.theme.RedNegative
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

// Stock Extensions
fun Stock.getChangeColor(): Color {
    return if (isPositive) GreenPositive else RedNegative
}

fun Double.formatAsPrice(): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(this)
}

@SuppressLint("DefaultLocale")
fun Double.formatAsPercentage(): String {
    return String.format("%.2f%%", this)
}

@SuppressLint("DefaultLocale")
fun Long.formatAsVolume(): String {
    return when {
        this >= 1_000_000_000 -> String.format("%.1fB", this / 1_000_000_000.0)
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> this.toString()
    }
}

fun String.formatPercentage(): String {
    return this.replace("%", "").toDoubleOrNull()?.let {
        "${if (it >= 0) "+" else ""}${String.format("%.2f", it)}%"
    } ?: this
}

fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is NetworkException.NetworkError -> "No internet connection. Please check your network."
        is NetworkException.ServerError -> "Server is currently unavailable. Please try again later."
        is NetworkException.ApiLimitExceeded -> "API rate limit exceeded. Please try again in a few minutes."
        is NetworkException.ApiError -> "API Error: ${this.message}"
        else -> "Something went wrong. Please try again."
    }
}

// String Extensions
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}

fun String.toDoubleOrZero(): Double {
    return this.toDoubleOrNull() ?: 0.0
}

fun String.toLongOrZero(): Long {
    return this.toLongOrNull() ?: 0L
}

fun String.isValidWatchlistName(): Boolean {
    return this.isNotBlank() && this.length >= 2 && this.length <= 50
}

fun String.isValidStockSymbol(): Boolean {
    return this.matches(Regex("^[A-Z]{1,5}$"))
}

// Date Extensions
fun String.parseApiDate(): Date? {
    return try {
        java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

@SuppressLint("DefaultLocale")
fun Double.formatMarketCap(): String {
    return when {
        this >= 1_000_000_000_000 -> String.format("$%.2fT", this / 1_000_000_000_000)
        this >= 1_000_000_000 -> String.format("$%.2fB", this / 1_000_000_000)
        this >= 1_000_000 -> String.format("$%.2fM", this / 1_000_000)
        this >= 1_000 -> String.format("$%.2fK", this / 1_000)
        else -> String.format("$%.2f", this)
    }
}