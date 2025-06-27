package com.example.groww_assignment.domain.usecase.stocks

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetStocksFromCacheUseCase @Inject constructor(
    private val repository: StocksRepository
) : BaseUseCase<Unit, List<Stock>>() {

    override suspend fun execute(parameters: Unit): List<Stock> {
        return repository.getAllStocks().first()
    }
}