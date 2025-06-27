package com.example.groww_assignment.domain.model

data class TimeSeriesData(
    val symbol: String,
    val lastRefreshed: String,
    val timeZone: String,
    val dailyData: List<DailyData>
)
