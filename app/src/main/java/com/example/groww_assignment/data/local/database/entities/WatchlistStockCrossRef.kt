package com.example.groww_assignment.data.local.database.entities

import androidx.room.Entity

@Entity(
    tableName = "watchlist_stock_cross_ref",
    primaryKeys = ["watchlistId", "stockSymbol"]
)
data class WatchlistStockCrossRef(
    val watchlistId: Long,
    val stockSymbol: String,
    val addedAt: Long = System.currentTimeMillis()
)
