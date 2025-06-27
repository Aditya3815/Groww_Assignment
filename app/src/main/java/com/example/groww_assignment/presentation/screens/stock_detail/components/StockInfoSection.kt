package com.example.groww_assignment.presentation.screens.stock_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.model.StockDetail
import com.example.groww_assignment.utils.Dimensions

@Composable
fun StockInfoSection(
    stockDetail: StockDetail,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium)
        ) {
            Text(
                text = "Company Information",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Dimensions.paddingMedium)
            )

            // Key metrics grid
            StockMetricsGrid(stockDetail = stockDetail)

            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))

            // Company description
            if (stockDetail.description.isNotBlank()) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = Dimensions.paddingSmall)
                )

                Text(
                    text = stockDetail.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@Composable
private fun StockMetricsGrid(
    stockDetail: StockDetail,
    modifier: Modifier = Modifier
) {
    val metrics = listOf(
        "Market Cap" to stockDetail.marketCap,
        "P/E Ratio" to stockDetail.peRatio,
        "EPS" to stockDetail.eps,
        "Dividend Yield" to stockDetail.dividendYield,
        "52W High" to stockDetail.fiftyTwoWeekHigh,
        "52W Low" to stockDetail.fiftyTwoWeekLow,
        "Beta" to stockDetail.beta,
        "Exchange" to stockDetail.exchange,
        "Sector" to stockDetail.sector,
        "Industry" to stockDetail.industry,
        "Country" to stockDetail.country,
        "Currency" to stockDetail.currency
    ).filter { it.second.isNotBlank() } // Only show metrics with data

    Column(modifier = modifier) {
        metrics.chunked(2).forEach { rowMetrics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                rowMetrics.forEach { (label, value) ->
                    MetricItem(
                        label = label,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fill remaining space if odd number of items
                if (rowMetrics.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}