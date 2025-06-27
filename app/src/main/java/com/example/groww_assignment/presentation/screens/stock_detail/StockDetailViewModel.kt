package com.example.groww_assignment.presentation.screens.stock_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.usecase.composite.WatchlistOperation
import com.example.groww_assignment.domain.usecase.composite.WatchlistOperationsUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStockDetailUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStockTimeSeriesUseCase
import com.example.groww_assignment.domain.usecase.watchlist.GetWatchlistsUseCase
import com.example.groww_assignment.domain.usecase.watchlist.IsStockInWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import com.example.groww_assignment.domain.util.ErrorHandler
import com.example.groww_assignment.presentation.components.chart.ChartTimeRange
import com.example.groww_assignment.presentation.util.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result
import com.example.groww_assignment.utils.DateUtils

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val getStockTimeSeriesUseCase: GetStockTimeSeriesUseCase,
    private val getWatchlistsUseCase: GetWatchlistsUseCase,
    private val isStockInWatchlistUseCase: IsStockInWatchlistUseCase,
    private val watchlistOperationsUseCase: WatchlistOperationsUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
    private val snackbarController: SnackbarController,
    private val errorHandler: ErrorHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val symbol: String = savedStateHandle.get<String>("symbol") ?: ""

    private val _uiState = MutableStateFlow(StockDetailUiState(symbol = symbol))
    val uiState: StateFlow<StockDetailUiState> = _uiState.asStateFlow()

    init {
        loadStockDetail()
        loadWatchlists()
    }

    fun loadStockDetail(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh,
                isRefreshing = forceRefresh,
                error = null
            )

            try {
                // Load stock detail
                val stockDetailResult = getStockDetailUseCase(
                    GetStockDetailUseCase.Params(symbol)
                )

                when (stockDetailResult) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            stockDetail = stockDetailResult.data,
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            lastUpdated = DateUtils.formatTimestamp(System.currentTimeMillis())
                        )

                        // Load time series data
                        loadTimeSeriesData()

                        if (forceRefresh) {
                            snackbarController.showSuccessSnackbar("Stock data refreshed")
                        }
                    }

                    is Result.Error -> {
                        val errorResult = errorHandler.handleError(stockDetailResult.exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = errorResult
                        )

                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                val errorResult = errorHandler.handleError(e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = errorResult
                )

                snackbarController.showErrorSnackbar(errorResult.message)
            }
        }
    }

    private fun loadTimeSeriesData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChartLoading = true, chartError = null)

            try {
                val result = getStockTimeSeriesUseCase(
                    GetStockTimeSeriesUseCase.Params(
                        symbol = symbol,
                        maxDataPoints = 365
                    )
                )

                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            timeSeriesData = result.data,
                            isChartLoading = false,
                            chartError = null
                        )
                    }

                    is Result.Error -> {
                        val errorResult = errorHandler.handleError(result.exception)
                        _uiState.value = _uiState.value.copy(
                            isChartLoading = false,
                            chartError = errorResult.message
                        )
                    }

                    is Result.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                val errorResult = errorHandler.handleError(e)
                _uiState.value = _uiState.value.copy(
                    isChartLoading = false,
                    chartError = errorResult.message
                )
            }
        }
    }

    private fun loadWatchlists() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWatchlistLoading = true)

            getWatchlistsUseCase().collect { watchlists ->
                _uiState.value = _uiState.value.copy(
                    watchlists = watchlists,
                    isWatchlistLoading = false
                )

                // Check which watchlists contain this stock
                checkWatchlistStatus(watchlists)
            }
        }
    }

    private fun checkWatchlistStatus(watchlists: List<Watchlist>) {
        viewModelScope.launch {
            val statusMap = mutableMapOf<Long, Boolean>()

            watchlists.forEach { watchlist ->
                try {
                    val result = isStockInWatchlistUseCase(
                        IsStockInWatchlistUseCase.Params(watchlist.id, symbol)
                    )

                    when (result) {
                        is Result.Success -> {
                            statusMap[watchlist.id] = result.data
                        }
                        is Result.Error -> {
                            statusMap[watchlist.id] = false
                        }
                        is Result.Loading -> {
                        }
                    }
                } catch (e: Exception) {
                    statusMap[watchlist.id] = false
                }
            }

            _uiState.value = _uiState.value.copy(isInWatchlists = statusMap)
        }
    }

    fun onTimeRangeSelected(timeRange: ChartTimeRange) {
        _uiState.value = _uiState.value.copy(selectedTimeRange = timeRange)
    }

    fun onAddToWatchlistClick() {
        _uiState.value = _uiState.value.copy(showAddToWatchlistDialog = true)
    }

    fun onDismissWatchlistDialog() {
        _uiState.value = _uiState.value.copy(showAddToWatchlistDialog = false)
    }

    fun onAddToExistingWatchlist(watchlistId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToWatchlist = true)

            try {
                val stockDetail = _uiState.value.stockDetail ?: return@launch
                val stock = Stock(
                    symbol = stockDetail.symbol,
                    name = stockDetail.name,
                    price = stockDetail.currentPrice,
                    change = stockDetail.change,
                    changePercent = stockDetail.changePercent,
                    volume = stockDetail.volume
                )

                val result = watchlistOperationsUseCase(
                    WatchlistOperationsUseCase.Params(
                        operation = WatchlistOperation.ADD_TO_EXISTING,
                        stock = stock,
                        watchlistId = watchlistId
                    )
                )

                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isAddingToWatchlist = false,
                            showAddToWatchlistDialog = false
                        )

                        // Update watchlist status
                        checkWatchlistStatus(_uiState.value.watchlists)

                        snackbarController.showSuccessSnackbar("Added to watchlist")
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isAddingToWatchlist = false)
                        val errorResult = errorHandler.handleError(result.exception)
                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isAddingToWatchlist = false)
                val errorResult = errorHandler.handleError(e)
                snackbarController.showErrorSnackbar(errorResult.message)
            }
        }
    }

    fun onCreateNewWatchlist(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToWatchlist = true)

            try {
                val stockDetail = _uiState.value.stockDetail ?: return@launch
                val stock = Stock(
                    symbol = stockDetail.symbol,
                    name = stockDetail.name,
                    price = stockDetail.currentPrice,
                    change = stockDetail.change,
                    changePercent = stockDetail.changePercent,
                    volume = stockDetail.volume
                )

                val result = watchlistOperationsUseCase(
                    WatchlistOperationsUseCase.Params(
                        operation = WatchlistOperation.CREATE_AND_ADD,
                        stock = stock,
                        watchlistName = name
                    )
                )

                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isAddingToWatchlist = false,
                            showAddToWatchlistDialog = false
                        )

                        snackbarController.showSuccessSnackbar("Created watchlist '$name' and added ${stock.symbol}")
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isAddingToWatchlist = false)
                        val errorResult = errorHandler.handleError(result.exception)
                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isAddingToWatchlist = false)
                val errorResult = errorHandler.handleError(e)
                snackbarController.showErrorSnackbar(errorResult.message)
            }
        }
    }

    fun onRemoveFromWatchlist(watchlistId: Long) {
        viewModelScope.launch {
            try {
                val result = removeFromWatchlistUseCase(
                    RemoveFromWatchlistUseCase.Params(watchlistId, symbol)
                )

                when (result) {
                    is Result.Success -> {
                        // Update watchlist status
                        val updatedStatus = _uiState.value.isInWatchlists.toMutableMap()
                        updatedStatus[watchlistId] = false
                        _uiState.value = _uiState.value.copy(isInWatchlists = updatedStatus)

                        snackbarController.showSuccessSnackbar("Removed from watchlist")
                    }

                    is Result.Error -> {
                        val errorResult = errorHandler.handleError(result.exception)
                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        // Handle if needed
                    }
                }
            } catch (e: Exception) {
                val errorResult = errorHandler.handleError(e)
                snackbarController.showErrorSnackbar(errorResult.message)
            }
        }
    }

    fun onRefresh() {
        loadStockDetail(forceRefresh = true)
    }

    fun onRetry() {
        loadStockDetail()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}