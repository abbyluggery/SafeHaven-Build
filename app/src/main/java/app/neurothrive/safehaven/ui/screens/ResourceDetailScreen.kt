package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar

/**
 * Resource Detail Screen
 * Shows detailed information about a legal resource
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailScreen(
    resourceId: String,
    onBack: () -> Unit
) {
    // TODO: Load resource from repository by ID

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Resource Details",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Sample Shelter Name",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Match Score
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "✨ 85% Match for your profile",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { /* TODO: Call */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, "Call")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call")
                }
                FilledTonalButton(
                    onClick = { /* TODO: Directions */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Directions, "Directions")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Directions")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Information
            InfoSection(
                title = "Contact",
                items = listOf(
                    "Phone" to "(555) 123-4567",
                    "Website" to "www.safehaven-shelter.org",
                    "Email" to "help@safehaven.org"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            InfoSection(
                title = "Location",
                items = listOf(
                    "Address" to "123 Safe Street, City, State 12345",
                    "Distance" to "2.5 miles away"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Services
            InfoSection(
                title = "Services",
                items = listOf(
                    "Services" to "Emergency shelter, Legal advocacy, Counseling, Job training"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Identity-Specific Services
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✓ Serves Your Community",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Trans-inclusive\n" +
                                "• BIPOC-led organization\n" +
                                "• Wheelchair accessible\n" +
                                "• No ICE contact guaranteed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hours
            InfoSection(
                title = "Hours",
                items = listOf(
                    "Availability" to "24/7 Hotline",
                    "Walk-in Hours" to "Mon-Fri 9am-5pm"
                )
            )
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    items: List<Pair<String, String>>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "$label: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
