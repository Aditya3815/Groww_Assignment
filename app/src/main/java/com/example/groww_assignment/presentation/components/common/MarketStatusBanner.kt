package com.example.groww_assignment.presentation.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.presentation.util.getMarketStatusMessage
import com.example.groww_assignment.presentation.util.isMarketOpen
import com.example.groww_assignment.ui.theme.GreenPositive
import com.example.groww_assignment.ui.theme.RedNegative
import com.example.groww_assignment.utils.Dimensions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MarketStatusBanner(
    modifier: Modifier = Modifier
) {
    val isOpen = isMarketOpen()
    val statusMessage = getMarketStatusMessage()
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = if (isOpen)
                GreenPositive.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = if (isOpen) GreenPositive else RedNegative,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = currentTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}