package com.example.groww_assignment.presentation.screens.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_assignment.domain.usecase.composite.RefreshDataUseCase
import com.example.groww_assignment.domain.usecase.composite.SearchAndValidateStocksUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetTopGainersLosersUseCase
import com.example.groww_assignment.domain.util.ErrorHandler
import com.example.groww_assignment.presentation.util.SnackbarController
import com.example.groww_assignment.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import com.example.groww_assignment.domain.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getTopGainersLosersUseCase: GetTopGainersLosersUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val searchAndValidateStocksUseCase: SearchAndValidateStocksUseCase,
    private val snackbarController: SnackbarController,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadData()
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh,
                isRefreshing = forceRefresh,
                error = null
            )
            Log.d("ExploreViewModel", "📱 UI State updated - isLoading: ${!forceRefresh}, isRefreshing: $forceRefresh")


            try {
                Log.d("ExploreViewModel", "🔄 Calling getTopGainersLosersUseCase...")
                val result = getTopGainersLosersUseCase(
                    GetTopGainersLosersUseCase.Params(forceRefresh = forceRefresh)
                )
                Log.d("ExploreViewModel", "📦 Use case result type: ${result::class.simpleName}")
                when (result) {
                    is Result.Success -> {
                        Log.d("ExploreViewModel", "✅ SUCCESS - Data received!")
                        Log.d("ExploreViewModel", "📊 Top Gainers count: ${result.data.topGainers.size}")
                        Log.d("ExploreViewModel", "📊 Top Losers count: ${result.data.topLosers.size}")
                        Log.d("ExploreViewModel", "📊 Most Active count: ${result.data.mostActivelyTraded.size}")
                        Log.d("ExploreViewModel", "📊 Last Updated: ${result.data.lastUpdated}")

                        // Log first few stocks for verification
                        result.data.topGainers.take(3).forEachIndexed { index, stock ->
                            Log.d("ExploreViewModel", "🔥 Gainer $index: ${stock.symbol} - ${stock.price} (${stock.changePercent})")
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            topGainersLosers = result.data,
                            error = null,
                            lastUpdated = DateUtils.formatTimestamp(System.currentTimeMillis())
                        )

                        Log.d("ExploreViewModel", "✅ UI State updated with data")

                        if (forceRefresh) {
                            snackbarController.showSuccessSnackbar("Data refreshed successfully")
                        }
                    }

                    is Result.Error -> {
                        Log.e("ExploreViewModel", "❌ ERROR occurred!")
                        Log.e("ExploreViewModel", "❌ Error type: ${result.exception::class.simpleName}")
                        Log.e("ExploreViewModel", "❌ Error message: ${result.exception.message}")
                        Log.e("ExploreViewModel", "❌ Error stack trace:", result.exception)
                        val errorResult = errorHandler.handleError(result.exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = errorResult
                        )

                        Log.e("ExploreViewModel", "❌ Error result message: ${errorResult.message}")

                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        Log.d("ExploreViewModel", "⏳ Still loading...")
                    }
                }
            } catch (e: Exception) {
                Log.e("ExploreViewModel", "💥 EXCEPTION in loadData!")
                Log.e("ExploreViewModel", "💥 Exception type: ${e::class.simpleName}")
                Log.e("ExploreViewModel", "💥 Exception message: ${e.message}")
                Log.e("ExploreViewModel", "💥 Exception stack trace:", e)
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

    fun onRefresh() {
        loadData(forceRefresh = true)
    }

    fun onRetry() {
        loadData(forceRefresh = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.length >= 2) {
            searchStocks(query)
        } else {
            clearSearchResults()
        }
    }

    fun onSearchActiveChanged(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSearchActive = isActive,
            searchQuery = if (!isActive) "" else _uiState.value.searchQuery
        )

        if (!isActive) {
            clearSearchResults()
        }
    }

    private fun searchStocks(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)

            _uiState.value = _uiState.value.copy(
                isSearchLoading = true,
                searchError = null
            )

            try {
                val result = searchAndValidateStocksUseCase(
                    SearchAndValidateStocksUseCase.Params(
                        query = query,
                        maxResults = 20,
                        allowInvalidSymbols = true
                    )
                )

                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSearchLoading = false,
                            searchResults = result.data.searchResults,
                            searchError = if (result.data.hasResults) null else "No stocks found for '$query'"
                        )
                    }

                    is Result.Error -> {
                        val errorResult = errorHandler.handleError(result.exception)
                        _uiState.value = _uiState.value.copy(
                            isSearchLoading = false,
                            searchError = errorResult.message
                        )
                    }

                    is Result.Loading -> {
                        Log.d("ExploreViewModel", "Search loading")
                    }
                }
            } catch (e: Exception) {
                val errorResult = errorHandler.handleError(e)
                _uiState.value = _uiState.value.copy(
                    isSearchLoading = false,
                    searchError = errorResult.message
                )
            }
        }
    }

    private fun clearSearchResults() {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            isSearchLoading = false,
            searchError = null
        )
    }

    fun onSearchSubmitted(query: String) {
        if (query.trim().isNotEmpty()) {
            searchStocks(query.trim())
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

}