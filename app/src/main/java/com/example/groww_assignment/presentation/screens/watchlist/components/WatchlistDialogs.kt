package com.example.groww_assignment.presentation.screens.watchlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.utils.Dimensions

@Composable
fun CreateWatchlistDialog(
    onDismiss: () -> Unit,
    onCreateWatchlist: (String) -> Unit,
    isCreating: Boolean,
    modifier: Modifier = Modifier
) {
    var watchlistName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                    text = "Create New Watchlist",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

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
                                onCreateWatchlist(watchlistName.trim())
                            } else {
                                isError = true
                            }
                        }
                    ),
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimensions.paddingLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isCreating
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (watchlistName.trim().length in 2..50) {
                                onCreateWatchlist(watchlistName.trim())
                            } else {
                                isError = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isCreating && watchlistName.trim().isNotBlank()
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteWatchlistDialog(
    watchlist: Watchlist,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    isDeleting: Boolean,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Watchlist") },
        text = {
            Text("Are you sure you want to delete '${watchlist.name}'? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text("Delete")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text("Cancel")
            }
        }
    )
}