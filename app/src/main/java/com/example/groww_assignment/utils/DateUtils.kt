package com.example.groww_assignment.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val API_DATE_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    private const val FULL_FORMAT = "MMM dd, yyyy HH:mm"

    fun formatApiDate(dateString: String): String {
        return try {
            val apiFormat = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
            val displayFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            val date = apiFormat.parse(dateString)
            displayFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat(FULL_FORMAT, Locale.getDefault())
        return format.format(date)
    }

    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
            else -> formatApiDate(SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).format(Date(timestamp)))
        }
    }
}