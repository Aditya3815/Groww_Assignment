package com.example.groww_assignment.data.repository

import com.example.groww_assignment.domain.util.Result
import com.example.groww_assignment.data.local.database.dao.StockDao
import com.example.groww_assignment.data.local.database.dao.WatchlistDao
import com.example.groww_assignment.data.mappers.StockMapper
import com.example.groww_assignment.data.mappers.WatchlistMapper
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val watchlistDao: WatchlistDao,
    private val stockDao: StockDao
) : WatchlistRepository {
    override fun getAllWatchlists(): Flow<List<Watchlist>> {
        return watchlistDao.getAllWatchlists().map { entities ->
            entities.map { WatchlistMapper.fromEntity(it) }
        }
    }

    override suspend fun getWatchlistById(id: Long): Result<Watchlist> {
        return try {
            val entity = watchlistDao.getWatchlistById(id)
            if (entity != null) {
                val watchlist = WatchlistMapper.fromEntity(entity)
                Result.Success(watchlist)
            } else {
                Result.Error(Exception("Watchlist not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createWatchlist(name: String): Result<Long> {
        return try {
            val entity = WatchlistMapper.toEntity(
                Watchlist(name = name)
            )
            val watchlistId = watchlistDao.insertWatchlist(entity)
            Result.Success(watchlistId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteWatchlist(watchlist: Watchlist): Result<Unit> {
        return try {
            val entity = WatchlistMapper.toEntity(watchlist)
            watchlistDao.deleteWatchlist(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addStockToWatchlist(
        watchlistId: Long,
        stock: Stock
    ): Result<Unit> {
        return try {
            // First ensure the stock is in the stocks table
            stockDao.insertStock(StockMapper.toEntity(stock))

            // Then add to watchlist
            val crossRef = WatchlistMapper.createCrossRef(watchlistId, stock.symbol)
            watchlistDao.addStockToWatchlist(crossRef)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeStockFromWatchlist(
        watchlistId: Long,
        stockSymbol: String
    ): Result<Unit> {
        return try {
            val crossRef = WatchlistMapper.createCrossRef(watchlistId, stockSymbol)
            watchlistDao.removeStockFromWatchlist(crossRef)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getStocksInWatchlist(watchlistId: Long): Flow<List<Stock>> {
        return watchlistDao.getStocksInWatchlist(watchlistId).map { entities ->
            entities.map { StockMapper.fromEntity(it) }
        }
    }

    override suspend fun isStockInWatchlist(
        watchlistId: Long,
        stockSymbol: String
    ): Boolean {
        return try {
            watchlistDao.isStockInWatchlist(watchlistId, stockSymbol)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearWatchlist(watchlistId: Long): Result<Unit> {
        return try {
            watchlistDao.clearWatchlist(watchlistId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getWatchlistsContainingStock(stockSymbol: String): List<Watchlist> {
        return try {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
