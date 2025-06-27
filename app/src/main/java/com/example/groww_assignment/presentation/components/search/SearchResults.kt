package com.example.groww_assignment.presentation.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groww_assignment.domain.model.SearchResult
import com.example.groww_assignment.presentation.components.common.EmptyState
import com.example.groww_assignment.utils.Dimensions

@Composable
fun SearchResults(
    results: List<SearchResult>,
    onResultClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No stocks found"
) {
    if (results.isEmpty()) {
        EmptyState(
            title = "No Results",
            message = emptyMessage,
            icon = Icons.Default.SearchOff,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            items(
                items = results,
                key = { result -> result.symbol }
            ) { result ->
                SearchResultItem(
                    searchResult = result,
                    onClick = { onResultClick(result.symbol) }
                )
            }
        }
    }
}