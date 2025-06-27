package com.example.groww_assignment.domain.model

data class Watchlist(
    val id: Long = 0,
    val name: String,
    val stocks: List<Stock> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val stockCount: Int
        get() = stocks.size
}
