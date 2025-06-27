package com.example.groww_assignment.domain.usecase.composite

import com.example.groww_assignment.domain.model.SearchResult
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.usecase.stocks.SearchStocksUseCase
import com.example.groww_assignment.domain.usecase.validation.ValidateStockSymbolUseCase
import com.example.groww_assignment.domain.usecase.validation.ValidationResult
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import com.example.groww_assignment.domain.util.Result


class SearchAndValidateStocksUseCase @Inject constructor(
    private val searchStocksUseCase: SearchStocksUseCase,
    private val validateStockSymbolUseCase: ValidateStockSymbolUseCase
) : BaseUseCase<SearchAndValidateStocksUseCase.Params, SearchValidationResult>() {

    override suspend fun execute(parameters: Params): SearchValidationResult {
        val validationResult = validateStockSymbolUseCase(parameters.query)

        val validation = when (validationResult) {
            is  Result.Success -> validationResult.data
            is  Result.Error -> throw validationResult.exception
            is  Result.Loading -> throw Exception("Validating query")
        }

        val shouldProceedWithSearch = validation.isValid || parameters.allowInvalidSymbols

        val searchResults = if (shouldProceedWithSearch) {
            val searchResult = searchStocksUseCase(
                SearchStocksUseCase.Params(
                    query = parameters.query,
                    maxResults = parameters.maxResults
                )
            )

            when (searchResult) {
                is  Result.Success -> searchResult.data
                is  Result.Error -> throw searchResult.exception
                is  Result.Loading -> throw Exception("Searching stocks")
            }
        } else {
            emptyList()
        }

        return SearchValidationResult(
            query = parameters.query,
            validation = validation,
            searchResults = searchResults,
            isValidSymbol = validation.isValid
        )
    }

    data class Params(
        val query: String,
        val maxResults: Int = 20,
        val allowInvalidSymbols: Boolean = true
    )
}

data class SearchValidationResult(
    val query: String,
    val validation: ValidationResult,
    val searchResults: List<SearchResult>,
    val isValidSymbol: Boolean
) {
    val hasResults: Boolean = searchResults.isNotEmpty()
    val errorMessage: String? = validation.getErrorOrNull()
}