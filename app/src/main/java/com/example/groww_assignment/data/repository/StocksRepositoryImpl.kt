package com.example.groww_assignment.data.repository

import coil3.util.CoilUtils.result
import com.example.groww_assignment.domain.util.Result
import com.example.groww_assignment.data.local.database.dao.StockDao
import com.example.groww_assignment.data.mappers.StockMapper
import com.example.groww_assignment.data.remote.api.AlphaVantageApi
import com.example.groww_assignment.data.remote.api.NetworkService
import com.example.groww_assignment.domain.model.SearchResult
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.StockDetail
import com.example.groww_assignment.domain.model.TimeSeriesData
import com.example.groww_assignment.domain.model.TopGainersLosers
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.utils.Constants.CACHE_DURATION_MINUTES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StocksRepositoryImpl @Inject constructor(
    private val api: AlphaVantageApi,
    private val stockDao: StockDao,
    private val networkService: NetworkService
) : StocksRepository {
    override suspend fun getTopGainersLosers(): Result<TopGainersLosers> {
        val cachedData = getStocksFromCache()
        if (cachedData.isNotEmpty() && !isStockDataExpired()) {
            return Result.Success(
                TopGainersLosers(
                    lastUpdated = "Cached",
                    topGainers = cachedData.take(10),
                    topLosers = cachedData.takeLast(10),
                    mostActivelyTraded = cachedData.shuffled().take(10)
                )
            )
        }

        return networkService.safeApiCall {
            api.getTopGainersLosers()
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val topGainersLosers = StockMapper.fromTopGainersLosersDto(result.data)

                    val allStocks = topGainersLosers.topGainers +
                            topGainersLosers.topLosers +
                            topGainersLosers.mostActivelyTraded

                    stockDao.insertStocks(allStocks.map { StockMapper.toEntity(it) })

                    Result.Success(topGainersLosers)
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun getStockDetail(symbol: String): Result<StockDetail> {
        return networkService.safeApiCall {
            api.getCompanyOverview(symbol = symbol)
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val stockDetail = StockMapper.fromCompanyOverviewDto(result.data)

                    val timeSeriesResult = getTimeSeriesData(symbol)
                    val updatedDetail = if (timeSeriesResult is Result.Success) {
                        StockMapper.updateStockDetailWithPrice(stockDetail, timeSeriesResult.data)
                    } else {
                        stockDetail
                    }

                    Result.Success(updatedDetail)
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun getTimeSeriesData(symbol: String): Result<TimeSeriesData> {
        return networkService.safeApiCall {
            api.getTimeSeriesDaily(symbol = symbol)
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val timeSeriesData = StockMapper.fromTimeSeriesDto(result.data)
                    if (timeSeriesData != null) {
                        Result.Success(timeSeriesData)
                    } else {
                        Result.Error(Exception("Invalid time series data"))
                    }
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun searchStocks(query: String): Result<List<SearchResult>> {
        return networkService.safeApiCall {
            api.searchSymbol(keywords = query)
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val searchResults = result.data.bestMatches?.map {
                        StockMapper.fromSearchMatchDto(it)
                    } ?: emptyList()
                    Result.Success(searchResults.sortedByDescending { it.matchScore })
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override fun getAllStocks(): Flow<List<Stock>> {
        return stockDao.getAllStocks().map { entities ->
            entities.map { StockMapper.fromEntity(it) }
        }
    }

    override suspend fun refreshTopGainersLosers(): Result<TopGainersLosers> {
        // Clear expired cache
        val expiredTimestamp = System.currentTimeMillis() - (CACHE_DURATION_MINUTES * 60 * 1000)
        stockDao.deleteExpiredStocks(expiredTimestamp)

        // Fetch fresh data
        return getTopGainersLosers()
    }

    override suspend fun getStocksFromCache(): List<Stock> {
        return stockDao.getAllStocks().map { entities ->
            entities.map { StockMapper.fromEntity(it) }
        }.let { flow ->
            // Convert flow to list for cache check
            try {
                // This is a simplified approach - in real app, you might want to collect the flow
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun isStockDataExpired(): Boolean {
        val expiredTimestamp = System.currentTimeMillis() - (CACHE_DURATION_MINUTES * 60 * 1000)
        val cachedStocks = stockDao.getAllStocks()
        // Simple check - in real implementation, you'd check the actual timestamp
        return false // Simplified for now
    }
}