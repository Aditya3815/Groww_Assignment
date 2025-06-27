package com.example.groww_assignment.domain.usecase.base

import com.example.groww_assignment.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
){
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(parameters).let { Result.Success(it) }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}