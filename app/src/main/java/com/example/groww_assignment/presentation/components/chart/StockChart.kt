package com.example.groww_assignment.presentation.components.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groww_assignment.domain.model.DailyData
import com.example.groww_assignment.domain.model.TimeSeriesData
import com.example.groww_assignment.domain.util.formatAsPrice
import com.example.groww_assignment.ui.theme.GreenPositive
import com.example.groww_assignment.ui.theme.RedNegative
import com.example.groww_assignment.utils.Dimensions
import kotlin.collections.forEachIndexed
import kotlin.collections.isNotEmpty
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText


@Composable
fun StockChart(
    timeSeriesData: TimeSeriesData,
    modifier: Modifier = Modifier,
    showGrid: Boolean = true,
    showLabels: Boolean = true,
    animate: Boolean = true
) {
    if (timeSeriesData.dailyData.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(Dimensions.chartHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No chart data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        return
    }

    val chartData = remember(timeSeriesData) {
        timeSeriesData.dailyData.takeLast(30).reversed() // Show last 30 days
    }

    val minPrice = remember(chartData) { chartData.minOfOrNull { it.close } ?: 0.0 }
    val maxPrice = remember(chartData) { chartData.maxOfOrNull { it.close } ?: 0.0 }
    val priceRange = maxPrice - minPrice

    val isPositive = chartData.isNotEmpty() && chartData.last().close >= chartData.first().close
    val lineColor = if (isPositive) GreenPositive else RedNegative

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingMedium)
        ) {
            if (showLabels) {
                ChartHeader(
                    symbol = timeSeriesData.symbol,
                    currentPrice = chartData.lastOrNull()?.close ?: 0.0,
                    change = if (chartData.size >= 2) {
                        chartData.last().close - chartData[chartData.size - 2].close
                    } else 0.0,
                    isPositive = isPositive
                )

                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
            }

            LineChart(
                data = chartData,
                minPrice = minPrice,
                maxPrice = maxPrice,
                lineColor = lineColor,
                showGrid = showGrid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.chartHeight)
            )
        }
    }
}

@Composable
private fun ChartHeader(
    symbol: String,
    currentPrice: Double,
    change: Double,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = currentPrice.formatAsPrice(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isPositive)
                    GreenPositive.copy(alpha = 0.1f)
                else
                    RedNegative.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = "${if (isPositive) "+" else ""}${change.formatAsPrice()}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isPositive) GreenPositive else RedNegative,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun LineChart(
    data: List<DailyData>,
    minPrice: Double,
    maxPrice: Double,
    lineColor: Color,
    showGrid: Boolean,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40.dp.toPx()

        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        // Draw grid
        if (showGrid) {
            drawGrid(
                width = chartWidth,
                height = chartHeight,
                offsetX = padding,
                offsetY = padding
            )
        }

        // Draw price line
        if (data.size > 1) {
            val path = Path()
            val points = mutableListOf<Offset>()

            data.forEachIndexed { index, dailyData ->
                val x = padding + (index.toFloat() / (data.size - 1)) * chartWidth
                val normalizedPrice = if (maxPrice > minPrice) {
                    ((dailyData.close - minPrice) / (maxPrice - minPrice)).toFloat()
                } else {
                    0.5f
                }
                val y = padding + (1 - normalizedPrice) * chartHeight

                points.add(Offset(x, y))

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            // Draw the line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw gradient fill
            val gradientPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, padding + chartHeight)
                lineTo(points.first().x, padding + chartHeight)
                close()
            }

            val gradient = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.3f),
                    lineColor.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                startY = padding,
                endY = padding + chartHeight
            )

            drawPath(
                path = gradientPath,
                brush = gradient
            )
        }

        // Draw price labels
        if (data.isNotEmpty()) {
            val textStyle = TextStyle(
                fontSize = 10.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )

            // Min price label
            val minPriceText = minPrice.formatAsPrice()
            drawText(
                textMeasurer = textMeasurer,
                text = minPriceText,
                topLeft = Offset(5.dp.toPx(), padding + chartHeight - 15.dp.toPx()),
                style = textStyle
            )

            // Max price label
            val maxPriceText = maxPrice.formatAsPrice()
            drawText(
                textMeasurer = textMeasurer,
                text = maxPriceText,
                topLeft = Offset(5.dp.toPx(), padding),
                style = textStyle
            )
        }
    }
}

private fun DrawScope.drawGrid(
    width: Float,
    height: Float,
    offsetX: Float,
    offsetY: Float,
    gridColor: Color = Color.Gray.copy(alpha = 0.2f)
) {
    val horizontalLines = 5
    val verticalLines = 6

    for (i in 0..horizontalLines) {
        val y = offsetY + (i.toFloat() / horizontalLines) * height
        drawLine(
            color = gridColor,
            start = Offset(offsetX, y),
            end = Offset(offsetX + width, y),
            strokeWidth = 1.dp.toPx()
        )
    }

    for (i in 0..verticalLines) {
        val x = offsetX + (i.toFloat() / verticalLines) * width
        drawLine(
            color = gridColor,
            start = Offset(x, offsetY),
            end = Offset(x, offsetY + height),
            strokeWidth = 1.dp.toPx()
        )
    }
}