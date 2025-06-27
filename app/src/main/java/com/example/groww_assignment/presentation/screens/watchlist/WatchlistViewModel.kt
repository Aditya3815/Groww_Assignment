package com.example.groww_assignment.presentation.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.usecase.watchlist.CreateWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.DeleteWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.GetStocksInWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.GetWatchlistsUseCase
import com.example.groww_assignment.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import com.example.groww_assignment.domain.util.ErrorHandler
import com.example.groww_assignment.presentation.util.SnackbarController
import com.example.groww_assignment.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val getWatchlistsUseCase: GetWatchlistsUseCase,
    private val getStocksInWatchlistUseCase: GetStocksInWatchlistUseCase,
    private val createWatchlistUseCase: CreateWatchlistUseCase,
    private val deleteWatchlistUseCase: DeleteWatchlistUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
    private val snackbarController: SnackbarController,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadWatchlists()
    }

    private fun loadWatchlists() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getWatchlistsUseCase().collect { watchlists ->
                _uiState.value = _uiState.value.copy(
                    watchlists = watchlists,
                    isLoading = false,
                    isRefreshing = false,
                    error = null
                )

                // If we have a selected watchlist, update its stocks
                _uiState.value.selectedWatchlist?.let { selected ->
                    val updatedWatchlist = watchlists.find { it.id == selected.id }
                    if (updatedWatchlist != null) {
                        loadStocksForWatchlist(updatedWatchlist)
                    } else {
                        // Watchlist was deleted, go back to all watchlists view
                        onBackToAllWatchlists()
                    }
                }
            }
        }
    }

    fun onWatchlistSelected(watchlist: Watchlist) {
        _uiState.value = _uiState.value.copy(
            selectedWatchlist = watchlist,
            viewMode = WatchlistViewMode.SINGLE_WATCHLIST,
            searchQuery = "",
            isSearchActive = false,
            filteredStocks = emptyList()
        )

        loadStocksForWatchlist(watchlist)
    }

    private fun loadStocksForWatchlist(watchlist: Watchlist) {
        viewModelScope.launch {
            getStocksInWatchlistUseCase(watchlist.id).collect { stocks ->
                _uiState.value = _uiState.value.copy(
                    stocksInSelectedWatchlist = stocks,
                    isUpdatingStocks = false
                )
            }
        }
    }

    fun onBackToAllWatchlists() {
        _uiState.value = _uiState.value.copy(
            selectedWatchlist = null,
            stocksInSelectedWatchlist = emptyList(),
            viewMode = WatchlistViewMode.ALL_WATCHLISTS,
            searchQuery = "",
            isSearchActive = false,
            filteredStocks = emptyList()
        )
    }

    fun onCreateWatchlistClick() {
        if (_uiState.value.canCreateWatchlist) {
            _uiState.value = _uiState.value.copy(showCreateDialog = true)
        } else {
            viewModelScope.launch {
                snackbarController.showErrorSnackbar("Maximum watchlists limit reached (10)")
            }
        }
    }

    fun onDismissCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun onCreateWatchlist(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingWatchlist = true)

            try {
                val result = createWatchlistUseCase(CreateWatchlistUseCase.Params(name))

                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isCreatingWatchlist = false,
                            showCreateDialog = false
                        )

                        snackbarController.showSuccessSnackbar("Watchlist '$name' created successfully")
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isCreatingWatchlist = false)
                        val errorResult = errorHandler.handleError(result.exception)
                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isCreatingWatchlist = false)
                val errorResult = errorHandler.handleError(e)
                snackbarController.showErrorSnackbar(errorResult.message)
            }
        }
    }

    fun onDeleteWatchlistClick(watchlist: Watchlist) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            watchlistToDelete = watchlist
        )
    }

    fun onDismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            watchlistToDelete = null
        )
    }

    fun onConfirmDeleteWatchlist() {
        val watchlistToDelete = _uiState.value.watchlistToDelete ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingWatchlist = true)

            try {
                val result = deleteWatchlistUseCase(
                    DeleteWatchlistUseCase.Params(watchlistToDelete)
                )

                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isDeletingWatchlist = false,
                            showDeleteDialog = false,
                            watchlistToDelete = null
                        )

                        // If we deleted the currently selected watchlist, go back to all watchlists
                        if (_uiState.value.selectedWatchlist?.id == watchlistToDelete.id) {
                            onBackToAllWatchlists()
                        }

                        snackbarController.showSuccessSnackbar("Watchlist '${watchlistToDelete.name}' deleted")
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isDeletingWatchlist = false)
                        val errorResult = errorHandler.handleError(result.exception)
                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isDeletingWatchlist = false)
                val errorResult = errorHandler.handleError(e)
                snackbarController.showErrorSnackbar(errorResult.message)
            }
        }
    }

    fun onRemoveStockFromWatchlist(stockSymbol: String) {
        val selectedWatchlist = _uiState.value.selectedWatchlist ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingStocks = true)

            try {
                val result = removeFromWatchlistUseCase(
                    RemoveFromWatchlistUseCase.Params(selectedWatchlist.id, stockSymbol)
                )

                when (result) {
                    is Result.Success -> {
                        snackbarController.showSuccessSnackbar("$stockSymbol removed from watchlist")
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isUpdatingStocks = false)
                        val errorResult = errorHandler.handleError(result.exception)
                        snackbarController.showErrorSnackbar(errorResult.message)
                    }

                    is Result.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isUpdatingStocks = false)
                val errorResult = errorHandler.handleError(e)
                snackbarController.showErrorSnackbar(errorResult.message)
            }
        }
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
                _uiState.value.stocksInSelectedWatchlist.filter { stock ->
                    stock.symbol.contains(query, ignoreCase = true) ||
                            stock.name.contains(query, ignoreCase = true)
                }
            }

            _uiState.value = _uiState.value.copy(filteredStocks = filtered)
        }
    }

    fun onRefresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        // Watchlists are observed via Flow, so they'll update automatically
        // Just update the refresh state
        viewModelScope.launch {
            delay(1000) // Simulate refresh delay
            _uiState.value = _uiState.value.copy(isRefreshing = false)
            snackbarController.showSuccessSnackbar("Watchlists refreshed")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}