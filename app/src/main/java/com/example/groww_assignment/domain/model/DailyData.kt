package com.example.groww_assignment.domain.model

data class DailyData(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)
