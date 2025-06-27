package com.example.groww_assignment.presentation.screens.view_all

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.util.CoilUtils.result
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.usecase.pagination.PaginationResult
import com.example.groww_assignment.domain.usecase.pagination.PaginationUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStocksByTypeUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetTopGainersLosersUseCase
import com.example.groww_assignment.domain.util.ErrorHandler
import com.example.groww_assignment.presentation.util.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.drop
import com.example.groww_assignment.domain.util.Result
import com.example.groww_assignment.utils.Constants.PAGE_SIZE
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val getStocksByTypeUseCase: GetStocksByTypeUseCase,
    private val getTopGainersLosersUseCase: GetTopGainersLosersUseCase,
    private val paginationUseCase: PaginationUseCase<Stock>,
    private val snackbarController: SnackbarController,
    private val errorHandler: ErrorHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stockType: String = savedStateHandle.get<String>("type") ?: "gainers"

    private val _uiState = MutableStateFlow(ViewAllUiState(stockType = stockType))
    val uiState: StateFlow<ViewAllUiState> = _uiState.asStateFlow()

    private var allStocks: List<Stock> = emptyList()
    private var currentPage = 0
    private var searchJob: Job? = null

    init {
        loadStocks()
    }

    fun loadStocks(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh && _uiState.value.stocks.isEmpty(),
                isRefreshing = forceRefresh,
                error = null
            )

            try {
                val result = getStocksByTypeUseCase(
                    GetStocksByTypeUseCase.Params(stockType)
                )

                when (result) {
                    is Result.Success -> {
                        allStocks = result.data
                        updatePagination()

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )

                        if (forceRefresh) {
                            snackbarController.showSuccessSnackbar("Data refreshed successfully")
                        }
                    }

                    is Result.Error -> {
                        val errorResult = errorHandler.handleError(result.exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = errorResult
                        )

                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        // Already handled by setting loading state above
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

    fun loadMoreStocks() {
        val pagination = _uiState.value.pagination
        if (pagination?.hasNextPage != true || _uiState.value.isLoadingMore) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)

            try {
                delay(500)

                currentPage++
                updatePagination()

                _uiState.value = _uiState.value.copy(isLoadingMore = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
                snackbarController.showErrorSnackbar("Failed to load more stocks")
            }
        }
    }

    private suspend fun updatePagination() {
        paginationUseCase.allItems = allStocks
        val paginationResult = paginationUseCase(
            PaginationUseCase.Params(
                totalItems = allStocks.size,
                currentPage = currentPage,
                pageSize = PAGE_SIZE
            )
        )

        when (paginationResult) {
            is Result.Success -> {
                val paginationData = paginationResult.data
                val newStocks = allStocks.drop(paginationData.startIndex)
                    .take(paginationData.endIndex - paginationData.startIndex)

                val updatedStocks = if (currentPage == 0) {
                    newStocks
                } else {
                    _uiState.value.stocks + newStocks
                }

                _uiState.value = _uiState.value.copy(
                    stocks = updatedStocks,
                    pagination = paginationData.copy(items = updatedStocks)
                )
            }

            is Result.Error -> {
                val errorResult = errorHandler.handleError(paginationResult.exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = errorResult
                )
            }

            is Result.Loading -> {
                // Handle loading if needed
            }
        }
    }

    fun onRefresh() {
        currentPage = 0
        loadStocks(forceRefresh = true)
    }

    fun onRetry() {
        currentPage = 0
        loadStocks(forceRefresh = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchStocks(query)
    }

    fun onSearchActiveChanged(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSearchActive = isActive,
            searchQuery = if (!isActive) "" else _uiState.value.searchQuery,
            filteredStocks = if (!isActive) emptyList() else _uiState.value.filteredStocks
        )
    }

    private fun searchStocks(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300) // Debounce

            val filtered = if (query.isBlank()) {
                emptyList()
            } else {
                _uiState.value.stocks.filter { stock ->
                    stock.symbol.contains(query, ignoreCase = true) ||
                            stock.name.contains(query, ignoreCase = true)
                }
            }

            _uiState.value = _uiState.value.copy(filteredStocks = filtered)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}