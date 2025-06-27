package com.example.groww_assignment.presentation.util

import com.example.groww_assignment.domain.util.ErrorResult

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val errorResult: ErrorResult) : UiState<Nothing>()
    object Empty : UiState<Nothing>()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty
    val isIdle: Boolean get() = this is Idle

}

