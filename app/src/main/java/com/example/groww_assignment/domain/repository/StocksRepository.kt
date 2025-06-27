package com.example.groww_assignment.domain.repository

import com.example.groww_assignment.domain.model.SearchResult
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.StockDetail
import com.example.groww_assignment.domain.model.TimeSeriesData
import com.example.groww_assignment.domain.model.TopGainersLosers
import kotlinx.coroutines.flow.Flow
import com.example.groww_assignment.domain.util.Result

interface StocksRepository {
    suspend fun getTopGainersLosers(): Result<TopGainersLosers>

    suspend fun getStockDetail(symbol: String): Result<StockDetail>

    suspend fun getTimeSeriesData(symbol: String): Result<TimeSeriesData>

    suspend fun searchStocks(query: String): Result<List<SearchResult>>

    fun getAllStocks(): Flow<List<Stock>>

    suspend fun refreshTopGainersLosers(): Result<TopGainersLosers>

    suspend fun getStocksFromCache(): List<Stock>

    suspend fun isStockDataExpired(): Boolean
}