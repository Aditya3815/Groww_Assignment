package com.example.groww_assignment.domain.util

sealed class NetworkException(message:String): Exception(message) {
    object NetworkError : NetworkException("Network connection error")
    object ServerError : NetworkException("Server error occurred")
    object ApiLimitExceeded : NetworkException("API rate limit exceeded")
    data class ApiError(val code: Int, override val message: String) : NetworkException(message)
    object UnknownError : NetworkException("Unknown error occurred")
}