package com.example.groww_assignment.domain.usecase.validation

import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject

class ValidateStockSymbolUseCase @Inject constructor() : BaseUseCase<String, ValidationResult>() {

    override suspend fun execute(parameters: String): ValidationResult {
        val symbol = parameters.trim().uppercase()

        return when {
            symbol.isBlank() -> ValidationResult.Invalid("Stock symbol cannot be empty")
            symbol.length < 1 -> ValidationResult.Invalid("Stock symbol is too short")
            symbol.length > 5 -> ValidationResult.Invalid("Stock symbol cannot exceed 5 characters")
            !symbol.matches(Regex("^[A-Z]+$")) -> ValidationResult.Invalid("Stock symbol can only contain letters")
            else -> ValidationResult.Valid(symbol)
        }
    }
}