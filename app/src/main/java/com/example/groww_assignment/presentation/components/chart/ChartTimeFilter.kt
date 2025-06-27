package com.example.groww_assignment.presentation.components.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ChartTimeRange(val label: String, val days: Int, val description: String) {
    ONE_DAY("1D", 1, "1 Day"),
    ONE_WEEK("1W", 7, "1 Week"),
    ONE_MONTH("1M", 30, "1 Month"),
    THREE_MONTHS("3M", 90, "3 Months"),
    SIX_MONTHS("6M", 180, "6 Months"),
    ONE_YEAR("1Y", 365, "1 Year"),
    ALL_TIME("ALL", -1, "All Time")
}

@Composable
fun ChartTimeFilter(
    selectedRange: ChartTimeRange,
    onRangeSelected: (ChartTimeRange) -> Unit,
    modifier: Modifier = Modifier,
    availableRanges: List<ChartTimeRange> = ChartTimeRange.values().toList(),
    isLoading: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        availableRanges.forEach { range ->
            val isSelected = selectedRange == range

            FilterChip(
                selected = isSelected,
                onClick = {
                    if (!isLoading) {
                        onRangeSelected(range)
                    }
                },
                label = {
                    Text(
                        text = range.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 2.dp
                )
            )
        }
    }
}

@Composable
fun CompactChartTimeFilter(
    selectedRange: ChartTimeRange,
    onRangeSelected: (ChartTimeRange) -> Unit,
    modifier: Modifier = Modifier,
    availableRanges: List<ChartTimeRange> = listOf(
        ChartTimeRange.ONE_DAY,
        ChartTimeRange.ONE_WEEK,
        ChartTimeRange.ONE_MONTH,
        ChartTimeRange.THREE_MONTHS,
        ChartTimeRange.ONE_YEAR
    )
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        availableRanges.forEach { range ->
            val isSelected = selectedRange == range

            TextButton(
                onClick = { onRangeSelected(range) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    }
                )
            ) {
                Text(
                    text = range.label,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SegmentedChartTimeFilter(
    selectedRange: ChartTimeRange,
    onRangeSelected: (ChartTimeRange) -> Unit,
    modifier: Modifier = Modifier,
    availableRanges: List<ChartTimeRange> = ChartTimeRange.values().toList()
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            availableRanges.forEach { range ->
                val isSelected = selectedRange == range

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    onClick = { onRangeSelected(range) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = range.label,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Helper function to get default ranges for different contexts
object ChartTimeRangeDefaults {
    val stockDetail = listOf(
        ChartTimeRange.ONE_DAY,
        ChartTimeRange.ONE_WEEK,
        ChartTimeRange.ONE_MONTH,
        ChartTimeRange.THREE_MONTHS,
        ChartTimeRange.SIX_MONTHS,
        ChartTimeRange.ONE_YEAR
    )

    val miniChart = listOf(
        ChartTimeRange.ONE_WEEK,
        ChartTimeRange.ONE_MONTH,
        ChartTimeRange.THREE_MONTHS
    )

    val fullChart = ChartTimeRange.values().toList()
}