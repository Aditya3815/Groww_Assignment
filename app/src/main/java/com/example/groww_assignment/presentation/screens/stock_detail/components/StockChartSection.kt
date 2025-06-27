package com.example.groww_assignment.presentation.screens.stock_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.model.TimeSeriesData
import com.example.groww_assignment.presentation.components.chart.ChartTimeFilter
import com.example.groww_assignment.presentation.components.chart.ChartTimeRange
import com.example.groww_assignment.presentation.components.chart.ChartTimeRangeDefaults
import com.example.groww_assignment.presentation.components.chart.StockChart
import com.example.groww_assignment.presentation.components.common.ErrorMessage
import com.example.groww_assignment.presentation.components.common.LoadingIndicator
import com.example.groww_assignment.utils.Dimensions

@Composable
fun StockChartSection(
    timeSeriesData: TimeSeriesData?,
    selectedTimeRange: ChartTimeRange,
    onTimeRangeSelected: (ChartTimeRange) -> Unit,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
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
                text = "Price Chart",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Dimensions.paddingMedium)
            )

            // Time range filter
            ChartTimeFilter(
                selectedRange = selectedTimeRange,
                onRangeSelected = onTimeRangeSelected,
                availableRanges = ChartTimeRangeDefaults.stockDetail,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Chart content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        LoadingIndicator(message = "Loading chart data...")
                    }
                }

                error != null -> {
                    ErrorMessage(
                        message = error,
                        onRetry = onRetry,
                        modifier = Modifier.height(200.dp)
                    )
                }

                timeSeriesData != null -> {
                    StockChart(
                        timeSeriesData = timeSeriesData,
                        showLabels = false, // Header already shows price info
                        animate = true
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Text(
                            text = "No chart data available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}