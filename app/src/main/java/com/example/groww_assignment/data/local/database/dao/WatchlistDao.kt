package com.example.groww_assignment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.groww_assignment.data.local.database.entities.StockEntity
import com.example.groww_assignment.data.local.database.entities.WatchlistEntity
import com.example.groww_assignment.data.local.database.entities.WatchlistStockCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlists ORDER BY createdAt DESC")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlists WHERE id = :id")
    suspend fun getWatchlistById(id: Long): WatchlistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity): Long

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStockToWatchlist(crossRef: WatchlistStockCrossRef)

    @Delete
    suspend fun removeStockFromWatchlist(crossRef: WatchlistStockCrossRef)

    @Query("""
        SELECT s.* FROM stocks s 
        INNER JOIN watchlist_stock_cross_ref w ON s.symbol = w.stockSymbol 
        WHERE w.watchlistId = :watchlistId
        ORDER BY w.addedAt DESC
    """)
    fun getStocksInWatchlist(watchlistId: Long): Flow<List<StockEntity>>

    @Query("""
        SELECT EXISTS(SELECT 1 FROM watchlist_stock_cross_ref 
        WHERE watchlistId = :watchlistId AND stockSymbol = :symbol)
    """)
    suspend fun isStockInWatchlist(watchlistId: Long, symbol: String): Boolean

    @Query("DELETE FROM watchlist_stock_cross_ref WHERE watchlistId = :watchlistId")
    suspend fun clearWatchlist(watchlistId: Long)
}