package com.example.groww_assignment.presentation.components.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.utils.Dimensions

@Composable
fun StockList(
    stocks: List<Stock>,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    showDividers: Boolean = true,
    showVolume: Boolean = true
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(Dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
    ) {
        items(
            items = stocks,
            key = { stock -> stock.symbol }
        ) { stock ->
            StockCard(
                stock = stock,
                onClick = { onStockClick(stock.symbol) },
                showVolume = showVolume
            )

            if (showDividers && stock != stocks.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp
                )
            }
        }
    }
}
