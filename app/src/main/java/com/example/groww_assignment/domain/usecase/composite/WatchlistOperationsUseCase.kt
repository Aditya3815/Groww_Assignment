package com.example.groww_assignment.domain.usecase.composite

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.usecase.validation.ValidateWatchlistNameUseCase
import com.example.groww_assignment.domain.usecase.watchlist.AddToWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.CreateWatchlistUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result


class WatchlistOperationsUseCase @Inject constructor(
    private val createWatchlistUseCase: CreateWatchlistUseCase,
    private val addToWatchlistUseCase: AddToWatchlistUseCase,
    private val validateWatchlistNameUseCase: ValidateWatchlistNameUseCase
) : BaseUseCase<WatchlistOperationsUseCase.Params, WatchlistOperationResult>() {

    override suspend fun execute(parameters: Params): WatchlistOperationResult {
        return when (parameters.operation) {
            WatchlistOperation.CREATE_AND_ADD -> createWatchlistAndAddStock(parameters)
            WatchlistOperation.ADD_TO_EXISTING -> addToExistingWatchlist(parameters)
        }
    }

    private suspend fun createWatchlistAndAddStock(parameters: Params): WatchlistOperationResult {
        val watchlistName = parameters.watchlistName ?: throw IllegalArgumentException("Watchlist name required")
        val stock = parameters.stock ?: throw IllegalArgumentException("Stock required")

        // Validate watchlist name
        val validationResult = validateWatchlistNameUseCase(watchlistName)
        val validation = when (validationResult) {
            is   Result.Success -> validationResult.data
            is   Result.Error -> throw validationResult.exception
            is   Result.Loading -> throw Exception("Validating name")
        }

        if (validation.isInvalid) {
            return WatchlistOperationResult.ValidationError(validation.getErrorOrNull() ?: "Invalid name")
        }

        // Create watchlist
        val createResult = createWatchlistUseCase(CreateWatchlistUseCase.Params(watchlistName))
        val watchlistId = when (createResult) {
            is   Result.Success -> createResult.data
            is   Result.Error -> throw createResult.exception
            is   Result.Loading -> throw Exception("Creating watchlist")
        }


        val addResult = addToWatchlistUseCase(AddToWatchlistUseCase.Params(watchlistId, stock))
        when (addResult) {
            is   Result.Success -> {
                return WatchlistOperationResult.Success(
                    message = "Created watchlist '$watchlistName' and added ${stock.symbol}",
                    watchlistId = watchlistId
                )
            }
            is   Result.Error -> throw addResult.exception
            is   Result.Loading -> throw Exception("Adding stock")
        }
    }

    private suspend fun addToExistingWatchlist(parameters: Params): WatchlistOperationResult {
        val watchlistId = parameters.watchlistId ?: throw IllegalArgumentException("Watchlist ID required")
        val stock = parameters.stock ?: throw IllegalArgumentException("Stock required")

        val addResult = addToWatchlistUseCase(AddToWatchlistUseCase.Params(watchlistId, stock))
        return when (addResult) {
            is   Result.Success -> {
                WatchlistOperationResult.Success(
                    message = "Added ${stock.symbol} to watchlist",
                    watchlistId = watchlistId
                )
            }
            is   Result.Error -> throw addResult.exception
            is   Result.Loading -> throw Exception("Adding stock")
        }
    }

    data class Params(
        val operation: WatchlistOperation,
        val stock: Stock? = null,
        val watchlistId: Long? = null,
        val watchlistName: String? = null
    )
}

enum class WatchlistOperation {
    CREATE_AND_ADD,
    ADD_TO_EXISTING
}

sealed class WatchlistOperationResult {
    data class Success(val message: String, val watchlistId: Long) : WatchlistOperationResult()
    data class ValidationError(val error: String) : WatchlistOperationResult()
}