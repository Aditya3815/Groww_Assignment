package com.example.groww_assignment.domain.model

data class StockDetail(
    val symbol: String,
    val name: String,
    val description: String,
    val exchange: String,
    val currency: String,
    val country: String,
    val sector: String,
    val industry: String,
    val marketCap: String,
    val peRatio: String,
    val eps: String,
    val dividendYield: String,
    val fiftyTwoWeekHigh: String,
    val fiftyTwoWeekLow: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: String,
    val volume: Long,
    val beta: String,
    val address: String
){
    val isPositive: Boolean
        get() = change >= 0
}
