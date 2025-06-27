package com.example.groww_assignment.domain.usecase.stocks

import com.example.groww_assignment.domain.model.SearchResult
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.util.Result
import javax.inject.Inject

class SearchStocksUseCase @Inject constructor(
    private val repository: StocksRepository
) : BaseUseCase<SearchStocksUseCase.Params, List<SearchResult>>() {

    override suspend fun execute(parameters: Params): List<SearchResult> {
        if (parameters.query.isBlank()) {
            throw IllegalArgumentException("Search query cannot be empty")
        }

        if (parameters.query.length < 2) {
            throw IllegalArgumentException("Search query must be at least 2 characters")
        }

        val result = repository.searchStocks(parameters.query.trim())

        return when (result) {
            is Result.Success -> {
                result.data
                    .filter { it.matchScore >= 0.5 }
                    .sortedByDescending { it.matchScore }
                    .take(parameters.maxResults)
            }
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Search is in progress")
        }
    }

    data class Params(
        val query: String,
        val maxResults: Int = 20
    )
}