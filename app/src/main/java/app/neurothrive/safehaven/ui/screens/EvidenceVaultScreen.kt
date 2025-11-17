package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.neurothrive.safehaven.data.database.entities.EvidenceItem
import app.neurothrive.safehaven.data.session.UserSession
import app.neurothrive.safehaven.ui.components.EmptyState
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar
import app.neurothrive.safehaven.ui.viewmodels.EvidenceVaultViewModel

/**
 * Evidence Vault Screen
 * Grid view of encrypted photos/videos/audio
 *
 * Files are decrypted on-demand for viewing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceVaultScreen(
    viewModel: EvidenceVaultViewModel = hiltViewModel(),
    userSession: UserSession = hiltViewModel(),
    onBack: () -> Unit,
    onCaptureNew: () -> Unit
) {
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val evidenceItems by viewModel.evidenceItems.collectAsState()
    val currentUserId by userSession.currentUserId.collectAsState(initial = null)

    // Load evidence when screen launches
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            viewModel.loadEvidence(userId)
        }
    }

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Evidence Vault (${evidenceItems.size})",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCaptureNew) {
                Icon(Icons.Default.Add, "Capture new evidence")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                // Loading state
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Error state
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            currentUserId?.let { viewModel.loadEvidence(it) }
                        }) {
                            Text("Retry")
                        }
                    }
                }

                // Empty state
                evidenceItems.isEmpty() -> {
                    EmptyState(
                        message = "No evidence items yet.\nTap + to capture your first photo."
                    )
                }

                // Success state - show evidence grid
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(evidenceItems) { item ->
                            EvidenceThumbnail(
                                item = item,
                                onDelete = { viewModel.deleteEvidence(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EvidenceThumbnail(
    item: EvidenceItem,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Show icon based on type
            Text(
                text = when (item.evidenceType) {
                    "photo" -> "📷"
                    "video" -> "🎥"
                    "audio" -> "🎤"
                    else -> "📄"
                },
                style = MaterialTheme.typography.displayMedium
            )

            // File info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = item.evidenceType.uppercase(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Evidence?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
