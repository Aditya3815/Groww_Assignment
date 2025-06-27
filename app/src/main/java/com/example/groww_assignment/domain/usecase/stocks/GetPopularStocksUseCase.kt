package com.example.groww_assignment.domain.usecase.stocks

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result

class GetPopularStocksUseCase @Inject constructor(
    private val repository: StocksRepository
) : BaseUseCase<Unit, List<Stock>>() {

    override suspend fun execute(parameters: Unit): List<Stock> {

        val result = repository.getTopGainersLosers()

        return when (result) {
            is Result.Success -> {
                val popular = mutableListOf<Stock>()
                popular.addAll(result.data.topGainers.take(5))
                popular.addAll(result.data.mostActivelyTraded.take(5))
                popular.distinctBy { it.symbol }.take(10)
            }
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Loading popular stocks")
        }
    }
}