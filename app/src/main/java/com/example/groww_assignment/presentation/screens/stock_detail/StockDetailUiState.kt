package com.example.groww_assignment.presentation.screens.stock_detail

import com.example.groww_assignment.domain.model.StockDetail
import com.example.groww_assignment.domain.model.TimeSeriesData
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.util.ErrorResult
import com.example.groww_assignment.presentation.components.chart.ChartTimeRange


data class StockDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val stockDetail: StockDetail? = null,
    val timeSeriesData: TimeSeriesData? = null,
    val error: ErrorResult? = null,
    val symbol: String = "",

    // Chart state
    val selectedTimeRange: ChartTimeRange = ChartTimeRange.ONE_MONTH,
    val isChartLoading: Boolean = false,
    val chartError: String? = null,

    // Watchlist state
    val watchlists: List<Watchlist> = emptyList(),
    val isInWatchlists: Map<Long, Boolean> = emptyMap(),
    val isWatchlistLoading: Boolean = false,
    val showAddToWatchlistDialog: Boolean = false,

    // UI state
    val isAddingToWatchlist: Boolean = false,
    val lastUpdated: String = ""
) {
    val hasData: Boolean = stockDetail != null
    val hasError: Boolean = error != null
    val isEmpty: Boolean = !hasData && !isLoading && !hasError
    val hasChartData: Boolean = timeSeriesData != null && timeSeriesData.dailyData.isNotEmpty()

    val isInAnyWatchlist: Boolean = isInWatchlists.values.any { it }

    val displayName: String = stockDetail?.name?.takeIf { it.isNotBlank() } ?: symbol
    val currentPrice: Double = stockDetail?.currentPrice ?: 0.0
    val priceChange: Double = stockDetail?.change ?: 0.0
    val priceChangePercent: String = stockDetail?.changePercent ?: "0.00%"
    val isPositive: Boolean = stockDetail?.isPositive ?: false

    val chartData: TimeSeriesData? = timeSeriesData?.let { data ->
        val filteredData = when (selectedTimeRange) {
            ChartTimeRange.ONE_DAY -> data.dailyData.take(1)
            ChartTimeRange.ONE_WEEK -> data.dailyData.take(7)
            ChartTimeRange.ONE_MONTH -> data.dailyData.take(30)
            ChartTimeRange.THREE_MONTHS -> data.dailyData.take(90)
            ChartTimeRange.SIX_MONTHS -> data.dailyData.take(180)
            ChartTimeRange.ONE_YEAR -> data.dailyData.take(365)
            ChartTimeRange.ALL_TIME -> data.dailyData
        }
        data.copy(dailyData = filteredData)
    }
}