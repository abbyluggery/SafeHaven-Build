package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.FeatureCard

/**
 * Home Screen - Main Dashboard
 * Central hub for all SafeHaven features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToIncidents: () -> Unit,
    onNavigateToEvidence: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToSafetyPlan: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHealthcare: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SafeHaven") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
            // Welcome Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Welcome to SafeHaven",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your data is encrypted and stays on your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Document Evidence",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            FeatureCard(
                title = "Silent Camera",
                description = "Capture photos silently (no sound, flash, or GPS)",
                icon = {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                onClick = onNavigateToCamera
            )

            Spacer(modifier = Modifier.height(8.dp))

            FeatureCard(
                title = "Evidence Vault",
                description = "View encrypted photos and videos",
                icon = {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                onClick = onNavigateToEvidence
            )

            Spacer(modifier = Modifier.height(8.dp))

            FeatureCard(
                title = "Document Verification",
                description = "Verify IDs, passports, birth certificates",
                icon = {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                onClick = onNavigateToVerification
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Track & Report",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            FeatureCard(
                title = "Incident Reports",
                description = "Legal-formatted documentation of abuse",
                icon = {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                onClick = onNavigateToIncidents
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Find Help",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            FeatureCard(
                title = "Find Resources",
                description = "Shelters, legal aid, hotlines near you",
                icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                onClick = onNavigateToResources
            )

            Spacer(modifier = Modifier.height(8.dp))

            FeatureCard(
                title = "Safety Plan",
                description = "Create your personalized safety plan",
                icon = {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                onClick = onNavigateToSafetyPlan
            )

            Spacer(modifier = Modifier.height(8.dp))

            FeatureCard(
                title = "Healthcare Journey",
                description = "Plan reproductive healthcare appointments",
                icon = {
                    Icon(
                        Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                onClick = onNavigateToHealthcare
            )
        }
    }
}
