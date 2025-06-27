package com.example.groww_assignment.domain.usecase.stocks

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.util.Result
import javax.inject.Inject

class GetStocksByTypeUseCase @Inject constructor(
    private val repository: StocksRepository
) : BaseUseCase<GetStocksByTypeUseCase.Params, List<Stock>>() {

    override suspend fun execute(parameters: Params): List<Stock> {
        val result = repository.getTopGainersLosers()

        val topGainersLosers = when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Loading stock data")
        }

        return when (parameters.type.lowercase()) {
            "gainers", "top_gainers" -> topGainersLosers.topGainers
            "losers", "top_losers" -> topGainersLosers.topLosers
            "active", "most_active" -> topGainersLosers.mostActivelyTraded
            else -> throw IllegalArgumentException("Unknown stock type: ${parameters.type}")
        }
    }

    data class Params(
        val type: String
    )
}