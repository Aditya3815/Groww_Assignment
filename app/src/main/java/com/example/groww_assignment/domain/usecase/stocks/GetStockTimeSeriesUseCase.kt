package com.example.groww_assignment.domain.usecase.stocks

import com.example.groww_assignment.domain.model.TimeSeriesData
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.util.Result
import javax.inject.Inject

class GetStockTimeSeriesUseCase @Inject constructor(
    private val repository: StocksRepository
) : BaseUseCase<GetStockTimeSeriesUseCase.Params, TimeSeriesData>() {

    override suspend fun execute(parameters: Params): TimeSeriesData {
        if (parameters.symbol.isBlank()) {
            throw IllegalArgumentException("Stock symbol cannot be empty")
        }

        val result = repository.getTimeSeriesData(parameters.symbol.uppercase())

        return when (result) {
            is Result.Success -> {
                val data = result.data
                if (parameters.maxDataPoints > 0 && data.dailyData.size > parameters.maxDataPoints) {
                    data.copy(
                        dailyData = data.dailyData.take(parameters.maxDataPoints)
                    )
                } else {
                    data
                }
            }
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Time series data is loading")
        }
    }

    data class Params(
        val symbol: String,
        val maxDataPoints: Int = 30
    )
}