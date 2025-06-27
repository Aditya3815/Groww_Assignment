package com.example.groww_assignment.presentation.components.watchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.utils.Dimensions

@Composable
fun AddToWatchlistDialog(
    stock: Stock,
    watchlists: List<Watchlist>,
    onDismiss: () -> Unit,
    onAddToExisting: (Long) -> Unit,
    onCreateNew: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateNew by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.paddingLarge)
            ) {
                Text(
                    text = "Add ${stock.symbol} to Watchlist",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                if (showCreateNew) {
                    CreateNewWatchlistSection(
                        onCancel = { showCreateNew = false },
                        onCreate = { name ->
                            onCreateNew(name)
                            onDismiss()
                        }
                    )
                } else {
                    ExistingWatchlistsSection(
                        watchlists = watchlists,
                        onSelectWatchlist = { watchlistId ->
                            onAddToExisting(watchlistId)
                            onDismiss()
                        },
                        onCreateNew = { showCreateNew = true }
                    )
                }

                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateNewWatchlistSection(
    onCancel: () -> Unit,
    onCreate: (String) -> Unit
) {
    var watchlistName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        Text(
            text = "Create new watchlist:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

        OutlinedTextField(
            value = watchlistName,
            onValueChange = {
                watchlistName = it
                isError = false
            },
            label = { Text("Watchlist name") },
            placeholder = { Text("Enter watchlist name") },
            isError = isError,
            supportingText = if (isError) {
                { Text("Please enter a valid name (2-50 characters)") }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    if (watchlistName.trim().length in 2..50) {
                        onCreate(watchlistName.trim())
                    } else {
                        isError = true
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (watchlistName.trim().length in 2..50) {
                        onCreate(watchlistName.trim())
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = watchlistName.trim().isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create")
            }
        }
    }
}

@Composable
private fun WatchlistSelectionItem(
    watchlist: Watchlist,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = watchlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${watchlist.stockCount} stocks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to this watchlist",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ExistingWatchlistsSection(
    watchlists: List<Watchlist>,
    onSelectWatchlist: (Long) -> Unit,
    onCreateNew: () -> Unit
) {
    Column {
        if (watchlists.isNotEmpty()) {
            Text(
                text = "Select a watchlist:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                items(watchlists) { watchlist ->
                    WatchlistSelectionItem(
                        watchlist = watchlist,
                        onSelect = { onSelectWatchlist(watchlist.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
        }

        Button(
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create new watchlist",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create new watchlist")
        }
    }
}



