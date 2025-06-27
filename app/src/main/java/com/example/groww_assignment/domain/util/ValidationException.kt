package com.example.groww_assignment.domain.util

sealed class ValidationException(message: String) : Exception(message) {

    data class EmptyInput(val fieldName: String) : ValidationException(
        "$fieldName cannot be empty"
    )

    data class InvalidFormat(val fieldName: String, val expectedFormat: String) : ValidationException(
        "$fieldName has invalid format. Expected: $expectedFormat"
    )

    data class OutOfRange(val fieldName: String, val min: Int, val max: Int, val actual: Int) : ValidationException(
        "$fieldName must be between $min and $max characters. Got: $actual"
    )

    data class InvalidCharacters(val fieldName: String, val allowedPattern: String) : ValidationException(
        "$fieldName contains invalid characters. Allowed pattern: $allowedPattern"
    )

    data class InvalidSymbol(val symbol: String) : ValidationException(
        "Invalid stock symbol: $symbol. Must be 1-5 uppercase letters."
    )
}