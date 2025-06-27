package com.example.groww_assignment.presentation.screens.watchlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.presentation.components.common.EmptyState
import com.example.groww_assignment.presentation.components.watchlist.WatchlistCard
import com.example.groww_assignment.utils.Dimensions
import com.example.groww_assignment.utils.Dimensions.paddingLarge

@Composable
fun AllWatchlistsView(
    watchlists: List<Watchlist>,
    onWatchlistClick: (Watchlist) -> Unit,
    onCreateWatchlistClick: () -> Unit,
    onEditWatchlist: (Watchlist) -> Unit,
    onDeleteWatchlist: (Watchlist) -> Unit,
    canCreateWatchlist: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header with create button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Watchlists",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${watchlists.size} watchlist${if (watchlists.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            FloatingActionButton(
                onClick = onCreateWatchlistClick,
                modifier = Modifier.size(56.dp),
                containerColor = if (canCreateWatchlist)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new watchlist"
                )
            }
        }

        if (watchlists.isEmpty()) {
            EmptyWatchlistsState(
                onCreateWatchlistClick = onCreateWatchlistClick,
                canCreateWatchlist = canCreateWatchlist,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = Dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                items(
                    items = watchlists,
                    key = { watchlist -> watchlist.id }
                ) { watchlist ->
                    WatchlistCard(
                        watchlist = watchlist,
                        onClick = { onWatchlistClick(watchlist) },
                        onEdit = { onEditWatchlist(watchlist) },
                        onDelete = { onDeleteWatchlist(watchlist) }
                    )
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(paddingLarge))
                }
            }
        }
    }
}

@Composable
private fun EmptyWatchlistsState(
    onCreateWatchlistClick: () -> Unit,
    canCreateWatchlist: Boolean,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "No Watchlists",
        message = "Create your first watchlist to start tracking your favorite stocks",
        icon = Icons.Default.Add,
        actionText = if (canCreateWatchlist) "Create Watchlist" else null,
        onAction = if (canCreateWatchlist) onCreateWatchlistClick else null,
        modifier = modifier
    )
}