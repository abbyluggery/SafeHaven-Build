package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar

/**
 * Safety Plan Screen
 * Personalized safety planning for survivors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyPlanScreen(
    onBack: () -> Unit
) {
    var safePlace by remember { mutableStateOf("") }
    var emergencyContacts by remember { mutableStateOf("") }
    var importantDocuments by remember { mutableStateOf("") }
    var escapeRoute by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Safety Plan",
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
                text = "Create Your Safety Plan",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "A safety plan helps you prepare for emergencies and plan for leaving safely when you're ready.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Safe Places
            Text(
                text = "Safe Places to Go",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = safePlace,
                onValueChange = { safePlace = it },
                label = { Text("Where can you go if you need to leave?") },
                placeholder = { Text("Friend's house, family member, shelter") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Contacts
            Text(
                text = "Emergency Contacts",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = emergencyContacts,
                onValueChange = { emergencyContacts = it },
                label = { Text("Who can you call for help?") },
                placeholder = { Text("Trusted friends, family, hotlines") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Important Documents
            Text(
                text = "Important Documents",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = importantDocuments,
                onValueChange = { importantDocuments = it },
                label = { Text("What documents do you need?") },
                placeholder = { Text("ID, passport, birth certificates, bank info") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Escape Route
            Text(
                text = "Escape Route",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = escapeRoute,
                onValueChange = { escapeRoute = it },
                label = { Text("How will you leave safely?") },
                placeholder = { Text("Best exits, transportation, timing") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = { /* TODO: Save safety plan */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save Safety Plan")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Crisis Resources
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Crisis Resources",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "National DV Hotline: 1-800-799-7233\n" +
                                "Crisis Text Line: Text START to 88788\n" +
                                "911 for immediate danger",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
