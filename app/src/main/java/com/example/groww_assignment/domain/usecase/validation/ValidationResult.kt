package com.example.groww_assignment.domain.usecase.validation

sealed class ValidationResult {
    data class Valid(val value: String) : ValidationResult()
    data class Invalid(val error: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Valid

    val isInvalid: Boolean
        get() = this is Invalid

    fun getValueOrNull(): String? = if (this is Valid) value else null
    fun getErrorOrNull(): String? = if (this is Invalid) error else null
}
