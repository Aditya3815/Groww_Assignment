package com.example.groww_assignment.domain.usecase.stocks

import android.util.Log
import com.example.groww_assignment.domain.model.TopGainersLosers
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result


class GetTopGainersLosersUseCase @Inject constructor(
    private val repository: StocksRepository
) : BaseUseCase<GetTopGainersLosersUseCase.Params, TopGainersLosers>() {

    override suspend fun execute(parameters: Params): TopGainersLosers {
        if (parameters.forceRefresh) {
            repository.refreshTopGainersLosers()
        } else {
            repository.getTopGainersLosers()
        }

        val result = repository.getTopGainersLosers()
        return when (result) {
            is Result.Success ->{
                result.data
            }
            is Result.Error ->{
                throw result.exception
            }
            is Result.Loading ->{
                throw Exception("Data is loading")
            }
        }
    }

    data class Params(
        val forceRefresh: Boolean = false
    )
}