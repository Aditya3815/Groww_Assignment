package com.example.groww_assignment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.groww_assignment.data.local.database.entities.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM stocks WHERE symbol = :symbol")
    suspend fun getStock(symbol: String): StockEntity?

    @Query("SELECT * FROM stocks")
    fun getAllStocks(): Flow<List<StockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<StockEntity>)

    @Delete
    suspend fun deleteStock(stock: StockEntity)

    @Query("DELETE FROM stocks WHERE lastUpdated < :timestamp")
    suspend fun deleteExpiredStocks(timestamp: Long)

    @Query("SELECT * FROM stocks WHERE symbol IN (:symbols)")
    suspend fun getStocksBySymbols(symbols: List<String>): List<StockEntity>
}