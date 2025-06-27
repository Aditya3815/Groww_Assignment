package com.example.groww_assignment.domain.usecase.pagination

import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.utils.Constants.PAGE_SIZE
import javax.inject.Inject

class PaginationUseCase<T> @Inject constructor() : BaseUseCase<PaginationUseCase.Params, PaginationResult<T>>() {

    var allItems: List<T> = emptyList()
    override suspend fun execute(parameters: Params): PaginationResult<T> {
        val totalItems = allItems.size
        val pageSize = parameters.pageSize.coerceAtLeast(1)
        val currentPage = parameters.currentPage.coerceAtLeast(0)

        val totalPages = (totalItems + pageSize - 1) / pageSize
        val startIndex = currentPage * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(totalItems)

        val paginatedItems = allItems.subList(startIndex, endIndex)
        val hasNextPage = currentPage < totalPages - 1
        val hasPreviousPage = currentPage > 0

        return PaginationResult(
            currentPage = currentPage,
            pageSize = pageSize,
            totalItems = totalItems,
            totalPages = totalPages,
            startIndex = startIndex,
            endIndex = endIndex,
            hasNextPage = hasNextPage,
            hasPreviousPage = hasPreviousPage,
            items = paginatedItems
        )
    }

    data class Params(
        val totalItems: Int,
        val currentPage: Int = 0,
        val pageSize: Int = PAGE_SIZE
    )
}

data class PaginationResult<T>(
    val currentPage: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val startIndex: Int,
    val endIndex: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val items: List<T>
)