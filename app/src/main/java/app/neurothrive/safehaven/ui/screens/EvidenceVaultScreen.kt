package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.EmptyState
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar

/**
 * Evidence Vault Screen
 * Grid view of encrypted photos/videos/audio
 *
 * Files are decrypted on-demand for viewing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceVaultScreen(
    onBack: () -> Unit,
    onCaptureNew: () -> Unit
) {
    val evidenceItems = remember { emptyList<String>() } // TODO: Load from repository

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Evidence Vault",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCaptureNew) {
                Icon(Icons.Default.Add, "Capture new evidence")
            }
        }
    ) { padding ->
        if (evidenceItems.isEmpty()) {
            EmptyState(
                modifier = Modifier.padding(padding),
                message = "No evidence items yet.\nTap + to capture your first photo."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TODO: Load and display encrypted evidence items
                // items(evidenceItems) { item ->
                //     EvidenceThumbnail(item)
                // }
            }
        }
    }
}

@Composable
private fun EvidenceThumbnail() {
    // TODO: Decrypt and display thumbnail
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📷",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}
