package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.neurothrive.safehaven.data.database.entities.VerifiedDocument
import app.neurothrive.safehaven.data.session.UserSession
import app.neurothrive.safehaven.ui.components.EmptyState
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar
import app.neurothrive.safehaven.ui.viewmodels.DocumentVerificationViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Document Verification Screen
 * Cryptographic verification for IDs, passports, birth certificates
 *
 * Use case: Survivor can't access original documents (abuser controls them)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentVerificationScreen(
    viewModel: DocumentVerificationViewModel = hiltViewModel(),
    userSession: UserSession = hiltViewModel(),
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val currentUserId by userSession.currentUserId.collectAsState(initial = null)

    var showNewDocDialog by remember { mutableStateOf(false) }

    // Load documents when screen launches
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            viewModel.loadDocuments(userId)
        }
    }

    // Handle verification success
    LaunchedEffect(uiState.verificationSuccess) {
        if (uiState.verificationSuccess) {
            onVerified()
            viewModel.resetVerificationSuccess()
        }
    }

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Verified Documents (${documents.size})",
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (!uiState.isVerifying) {
                FloatingActionButton(onClick = { showNewDocDialog = true }) {
                    Icon(Icons.Default.CheckCircle, "Verify New Document")
                }
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

                // Verifying in progress
                uiState.isVerifying -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.progressMessage ?: "Verifying document...",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
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
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            currentUserId?.let { viewModel.loadDocuments(it) }
                        }) {
                            Text("Retry")
                        }
                    }
                }

                // Empty state
                documents.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Info Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "What is Document Verification?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "If your abuser controls your documents, take a photo and we'll create a cryptographically verified copy with SHA-256 hash. This can help prove authenticity later.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        EmptyState(
                            message = "No verified documents yet.\nTap + to verify your first document."
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Benefits Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "🔐 SHA-256 hash + Blockchain timestamp\n" +
                                        "📄 PDF with embedded verification\n" +
                                        "🔒 Encrypted storage",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Success - show document list
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(documents) { document ->
                            DocumentCard(
                                document = document,
                                onDelete = { viewModel.deleteDocument(document) }
                            )
                        }
                    }
                }
            }
        }
    }

    // New Document Dialog (simplified)
    if (showNewDocDialog) {
        var selectedType by remember { mutableStateOf("passport") }

        AlertDialog(
            onDismissRequest = { showNewDocDialog = false },
            title = { Text("Verify Document") },
            text = {
                Column {
                    Text("Select document type:")
                    Spacer(modifier = Modifier.height(16.dp))
                    viewModel.getDocumentTypes().take(5).forEach { type ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(type.name)
                            RadioButton(
                                selected = selectedType == type.id,
                                onClick = { selectedType = type.id }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showNewDocDialog = false
                    // TODO: Launch camera to capture document
                    // For now, show verification not implemented message
                }) {
                    Text("Capture Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewDocDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DocumentCard(
    document: VerifiedDocument,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = document.documentName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = document.documentType.replace("_", " ").uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Verified: ${formatTimestamp(document.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hash: ${document.sha256Hash.take(16)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    Icons.Default.CheckCircle,
                    "Verified",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete")
            }
        }
    }

    // Delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Document?") },
            text = { Text("This will delete the verified document. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
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

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
