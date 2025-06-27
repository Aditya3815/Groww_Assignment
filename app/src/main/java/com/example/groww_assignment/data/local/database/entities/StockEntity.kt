package com.example.groww_assignment.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePercent: String,
    val volume: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)
