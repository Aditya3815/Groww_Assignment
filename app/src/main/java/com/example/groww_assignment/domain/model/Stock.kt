package com.example.groww_assignment.domain.model

data class Stock(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePercent: String,
    val volume: Long,
    val lastUpdated: String = ""
){
    val isPositive: Boolean
        get() = change >= 0
}
