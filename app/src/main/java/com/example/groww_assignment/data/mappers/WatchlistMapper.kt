package com.example.groww_assignment.data.mappers

import com.example.groww_assignment.data.local.database.entities.WatchlistEntity
import com.example.groww_assignment.data.local.database.entities.WatchlistStockCrossRef
import com.example.groww_assignment.domain.model.Watchlist

object WatchlistMapper {
    fun toEntity(watchlist: Watchlist): WatchlistEntity {
        return WatchlistEntity(
            id = watchlist.id,
            name = watchlist.name,
            createdAt = watchlist.createdAt
        )
    }

    fun fromEntity(entity: WatchlistEntity): Watchlist {
        return Watchlist(
            id = entity.id,
            name = entity.name,
            createdAt = entity.createdAt
        )
    }

    fun createCrossRef(watchlistId: Long, stockSymbol: String): WatchlistStockCrossRef {
        return WatchlistStockCrossRef(
            watchlistId = watchlistId,
            stockSymbol = stockSymbol
        )
    }
}