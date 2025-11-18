package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.neurothrive.safehaven.data.session.UserSession
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar
import app.neurothrive.safehaven.ui.viewmodels.IncidentReportViewModel

/**
 * Incident Report Screen
 * Legal-formatted documentation of abuse incidents
 *
 * All sensitive fields are encrypted before database storage
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportScreen(
    viewModel: IncidentReportViewModel = hiltViewModel(),
    userSession: UserSession = hiltViewModel(),
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val draft by viewModel.currentDraft.collectAsState()
    val currentUserId by userSession.currentUserId.collectAsState(initial = null)

    // Local form state (synced with ViewModel draft)
    var incidentType by remember { mutableStateOf(draft.incidentType) }
    var description by remember { mutableStateOf(draft.description) }
    var location by remember { mutableStateOf(draft.location) }
    var witnesses by remember { mutableStateOf(draft.witnesses) }
    var injuries by remember { mutableStateOf(draft.injuries) }
    var policeInvolved by remember { mutableStateOf(draft.policeInvolved) }
    var policeReportNumber by remember { mutableStateOf(draft.policeReportNumber) }
    var medicalAttention by remember { mutableStateOf(draft.medicalAttention) }

    // Handle save success
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaved()
            viewModel.resetSaveSuccess()
        }
    }

    // Update ViewModel draft when fields change
    LaunchedEffect(
        incidentType, description, location, witnesses, injuries,
        policeInvolved, policeReportNumber, medicalAttention
    ) {
        viewModel.updateDraft(
            draft.copy(
                incidentType = incidentType,
                description = description,
                location = location,
                witnesses = witnesses,
                injuries = injuries,
                policeInvolved = policeInvolved,
                policeReportNumber = policeReportNumber,
                medicalAttention = medicalAttention
            )
        )
    }

    val incidentTypes = listOf(
        "physical" to "Physical Abuse",
        "verbal" to "Verbal/Emotional Abuse",
        "financial" to "Financial Abuse",
        "sexual" to "Sexual Abuse",
        "stalking" to "Stalking/Harassment",
        "other" to "Other"
    )

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "New Incident Report",
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
                text = "Document the incident",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Incident Type
            Text("Incident Type", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            incidentTypes.forEach { (value, label) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label)
                    RadioButton(
                        selected = incidentType == value,
                        onClick = { incidentType = value }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description (encrypted)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (encrypted)") },
                placeholder = { Text("What happened? Include date, time, and details") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("Where did this occur?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Witnesses (encrypted)
            OutlinedTextField(
                value = witnesses,
                onValueChange = { witnesses = it },
                label = { Text("Witnesses (encrypted)") },
                placeholder = { Text("Names, contact info of witnesses") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Injuries (encrypted)
            OutlinedTextField(
                value = injuries,
                onValueChange = { injuries = it },
                label = { Text("Injuries (encrypted)") },
                placeholder = { Text("Describe any physical injuries") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Police involved
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Police involved?")
                Switch(
                    checked = policeInvolved,
                    onCheckedChange = { policeInvolved = it }
                )
            }

            if (policeInvolved) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = policeReportNumber,
                    onValueChange = { policeReportNumber = it },
                    label = { Text("Police report number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Medical attention
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Received medical attention?")
                Switch(
                    checked = medicalAttention,
                    onCheckedChange = { medicalAttention = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error message
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Save Button
            Button(
                onClick = {
                    currentUserId?.let { userId ->
                        viewModel.saveIncidentReport(
                            userId = userId,
                            gpsEnabled = false, // TODO: Get from user settings
                            latitude = null,
                            longitude = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving && description.isNotBlank()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Incident Report (Encrypted)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Description, witnesses, and injuries are encrypted before storage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
