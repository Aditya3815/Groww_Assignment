package com.example.groww_assignment.domain.usecase.validation

import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject

class ValidateWatchlistNameUseCase @Inject constructor() : BaseUseCase<String, ValidationResult>() {

    override suspend fun execute(parameters: String): ValidationResult {
        val name = parameters.trim()

        return when {
            name.isBlank() -> ValidationResult.Invalid("Watchlist name cannot be empty")
            name.length < 2 -> ValidationResult.Invalid("Watchlist name must be at least 2 characters")
            name.length > 50 -> ValidationResult.Invalid("Watchlist name cannot exceed 50 characters")
            !name.matches(Regex("^[a-zA-Z0-9\\s\\-_]+$")) -> ValidationResult.Invalid("Invalid characters in watchlist name")
            name.startsWith(" ") || name.endsWith(" ") -> ValidationResult.Invalid("Watchlist name cannot start or end with spaces")
            else -> ValidationResult.Valid(name)
        }
    }
}
