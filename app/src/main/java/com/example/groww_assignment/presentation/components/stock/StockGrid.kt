package com.example.groww_assignment.presentation.components.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.utils.Dimensions

@Composable
fun StockGrid(
    stocks: List<Stock>,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(Dimensions.paddingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
    ) {
        items(
            items = stocks,
            key = { stock -> stock.symbol }
        ) { stock ->
            StockCard(
                stock = stock,
                onClick = { onStockClick(stock.symbol) },
                modifier = Modifier.aspectRatio(1.2f)
            )
        }
    }
}