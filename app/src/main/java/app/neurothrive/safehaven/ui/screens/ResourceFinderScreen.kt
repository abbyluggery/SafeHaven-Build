package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.EmptyState
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar

/**
 * Resource Finder Screen
 * Search and filter legal resources (shelters, legal aid, hotlines)
 *
 * CRITICAL: Uses intersectional matching algorithm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceFinderScreen(
    onBack: () -> Unit,
    onResourceClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var resourceType by remember { mutableStateOf("shelter") }

    val resources = remember { emptyList<String>() } // TODO: Load from repository

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Find Resources",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(Icons.Default.FilterList, "Filters")
                }
            }

            // Resource Type Tabs
            ScrollableTabRow(
                selectedTabIndex = when (resourceType) {
                    "shelter" -> 0
                    "legal_aid" -> 1
                    "hotline" -> 2
                    "counseling" -> 3
                    else -> 0
                }
            ) {
                Tab(
                    selected = resourceType == "shelter",
                    onClick = { resourceType = "shelter" },
                    text = { Text("Shelters") }
                )
                Tab(
                    selected = resourceType == "legal_aid",
                    onClick = { resourceType = "legal_aid" },
                    text = { Text("Legal Aid") }
                )
                Tab(
                    selected = resourceType == "hotline",
                    onClick = { resourceType = "hotline" },
                    text = { Text("Hotlines") }
                )
                Tab(
                    selected = resourceType == "counseling",
                    onClick = { resourceType = "counseling" },
                    text = { Text("Counseling") }
                )
            }

            // Resources List
            if (resources.isEmpty()) {
                EmptyState(
                    message = "No resources found.\nTry adjusting your filters or search query."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TODO: Display actual resources
                    items(resources) { resource ->
                        ResourceCard(
                            name = "Sample Resource",
                            distance = "2.5 miles",
                            score = 85,
                            onClick = { onResourceClick("resource_id") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceCard(
    name: String,
    distance: String,
    score: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = distance,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.Phone, "Call", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
