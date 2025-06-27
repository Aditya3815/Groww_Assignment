package com.example.groww_assignment.presentation.components.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.util.formatAsPrice
import com.example.groww_assignment.domain.util.formatAsVolume
import com.example.groww_assignment.domain.util.formatPercentage
import com.example.groww_assignment.ui.theme.GreenPositive
import com.example.groww_assignment.ui.theme.RedNegative
import com.example.groww_assignment.utils.Dimensions

@Composable
fun StockCard(
    stock: Stock,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showVolume: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.stockCardHeight)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.paddingMedium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with symbol and trend icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stock.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (stock.name.isNotBlank()) {
                        Text(
                            text = stock.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = if (stock.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = if (stock.isPositive) "Trending Up" else "Trending Down",
                    tint = if (stock.isPositive) GreenPositive else RedNegative,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Price information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = stock.price.formatAsPrice(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (showVolume && stock.volume > 0) {
                        Text(
                            text = "Vol: ${stock.volume.formatAsVolume()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Change information
                PriceChangeChip(
                    change = stock.change,
                    changePercent = stock.changePercent,
                    isPositive = stock.isPositive
                )
            }
        }
    }
}

@Composable
fun PriceChangeChip(
    change: Double,
    changePercent: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isPositive) GreenPositive.copy(alpha = 0.1f) else RedNegative.copy(alpha = 0.1f)
    val textColor = if (isPositive) GreenPositive else RedNegative

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${if (isPositive) "+" else ""}${change.formatAsPrice()}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = changePercent.formatPercentage(),
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}