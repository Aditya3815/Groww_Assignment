package com.example.groww_assignment.domain.util

import javax.inject.Inject

class ErrorHandler @Inject constructor() {

    fun handleError(exception: Throwable): ErrorResult {
        val message = ExceptionMapper.mapToUserFriendlyMessage(exception)
        val type = ExceptionMapper.getErrorType(exception)
        val isRetryable = isRetryableError(exception)
        val severity = getErrorSeverity(exception)

        return ErrorResult(
            message = message,
            type = type,
            isRetryable = isRetryable,
            severity = severity,
            originalException = exception
        )
    }

    private fun isRetryableError(exception: Throwable): Boolean {
        return when (exception) {
            is NetworkException.NetworkError,
            is NetworkException.ServerError,
            is CacheException.CacheUnavailable -> true
            is StockException.ApiRateLimitExceeded -> true
            else -> false
        }
    }

    private fun getErrorSeverity(exception: Throwable): ErrorSeverity {
        return when (exception) {
            is ValidationException -> ErrorSeverity.LOW
            is CacheException.CacheExpired -> ErrorSeverity.LOW
            is StockException.StockNotFound -> ErrorSeverity.MEDIUM
            is NetworkException.NetworkError -> ErrorSeverity.MEDIUM
            is StockException.ApiRateLimitExceeded -> ErrorSeverity.HIGH
            is NetworkException.ServerError -> ErrorSeverity.HIGH
            else -> ErrorSeverity.MEDIUM
        }
    }
}

data class ErrorResult(
    val message: String,
    val type: ErrorType,
    val isRetryable: Boolean,
    val severity: ErrorSeverity,
    val originalException: Throwable
)

enum class ErrorSeverity {
    LOW,
    MEDIUM,
    HIGH
}