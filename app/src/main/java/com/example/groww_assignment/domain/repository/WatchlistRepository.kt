package com.example.groww_assignment.domain.repository

import com.example.groww_assignment.domain.util.Result
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.Watchlist
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAllWatchlists(): Flow<List<Watchlist>>

    suspend fun getWatchlistById(id: Long): Result<Watchlist>

    suspend fun createWatchlist(name: String): Result<Long>

    suspend fun deleteWatchlist(watchlist: Watchlist): Result<Unit>

    suspend fun addStockToWatchlist(watchlistId: Long, stock: Stock): Result<Unit>

    suspend fun removeStockFromWatchlist(watchlistId: Long, stockSymbol: String): Result<Unit>

    fun getStocksInWatchlist(watchlistId: Long): Flow<List<Stock>>

    suspend fun isStockInWatchlist(watchlistId: Long, stockSymbol: String): Boolean

    suspend fun clearWatchlist(watchlistId: Long): Result<Unit>

    suspend fun getWatchlistsContainingStock(stockSymbol: String): List<Watchlist>
}