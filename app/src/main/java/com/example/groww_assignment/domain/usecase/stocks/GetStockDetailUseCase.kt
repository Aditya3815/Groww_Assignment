package com.example.groww_assignment.domain.usecase.stocks

import com.example.groww_assignment.domain.model.StockDetail
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result

class GetStockDetailUseCase @Inject constructor(
    private val repository: StocksRepository
) : BaseUseCase<GetStockDetailUseCase.Params, StockDetail>() {

    override suspend fun execute(parameters: Params): StockDetail {
        if (parameters.symbol.isBlank()) {
            throw IllegalArgumentException("Stock symbol cannot be empty")
        }

        if (!isValidStockSymbol(parameters.symbol)) {
            throw IllegalArgumentException("Invalid stock symbol format")
        }

        val result = repository.getStockDetail(parameters.symbol.uppercase())

        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Data is loading")
        }
    }

    private fun isValidStockSymbol(symbol: String): Boolean {
        return symbol.matches(Regex("^[A-Za-z]{1,5}$"))
    }

    data class Params(
        val symbol: String
    )
}