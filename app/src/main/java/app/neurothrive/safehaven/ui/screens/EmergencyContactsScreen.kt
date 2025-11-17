package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.neurothrive.safehaven.data.database.entities.EmergencyContact
import app.neurothrive.safehaven.ui.viewmodels.EmergencyContactsViewModel

/**
 * Emergency Contacts Settings Screen
 *
 * Manage trusted contacts for SOS emergency alerts
 *
 * Features:
 * - Add/edit/delete contacts
 * - Test alert functionality
 * - Mark as primary/secondary
 * - Verification tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(
    viewModel: EmergencyContactsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }
    var contactToDelete by remember { mutableStateOf<EmergencyContact?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Contacts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Contact")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Info Banner
                    InfoBanner()

                    // Error Message
                    errorMessage?.let { error ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                IconButton(onClick = { viewModel.clearError() }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // Contacts List
                    if (contacts.isEmpty()) {
                        EmptyState(onAddContact = { showAddDialog = true })
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Primary Contacts
                            val primaryContacts = contacts.filter { it.isPrimary }
                            if (primaryContacts.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Primary Contacts",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Text(
                                        text = "Always notified during SOS activations",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                items(primaryContacts) { contact ->
                                    EmergencyContactCard(
                                        contact = contact,
                                        onEdit = { editingContact = contact },
                                        onDelete = { contactToDelete = contact },
                                        onTest = { viewModel.testContact(contact.id) },
                                        onTogglePrimary = { viewModel.togglePrimary(contact.id) }
                                    )
                                }
                            }

                            // Secondary Contacts
                            val secondaryContacts = contacts.filter { !it.isPrimary }
                            if (secondaryContacts.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Secondary Contacts",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Text(
                                        text = "Notified as backup if needed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                items(secondaryContacts) { contact ->
                                    EmergencyContactCard(
                                        contact = contact,
                                        onEdit = { editingContact = contact },
                                        onDelete = { contactToDelete = contact },
                                        onTest = { viewModel.testContact(contact.id) },
                                        onTogglePrimary = { viewModel.togglePrimary(contact.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Contact Dialog
    if (showAddDialog || editingContact != null) {
        AddEditContactDialog(
            contact = editingContact,
            onDismiss = {
                showAddDialog = false
                editingContact = null
            },
            onSave = { name, phone, relationship, isPrimary, customMessage ->
                if (editingContact != null) {
                    viewModel.updateContact(
                        editingContact!!.copy(
                            name = name,
                            phoneNumber = phone,
                            relationship = relationship,
                            isPrimary = isPrimary,
                            customMessage = customMessage.ifBlank { null }
                        )
                    )
                } else {
                    viewModel.addContact(
                        name = name,
                        phoneNumber = phone,
                        relationship = relationship,
                        isPrimary = isPrimary,
                        customMessage = customMessage.ifBlank { null }
                    )
                }
                showAddDialog = false
                editingContact = null
            }
        )
    }

    // Delete Confirmation Dialog
    contactToDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Contact?") },
            text = {
                Text("Are you sure you want to remove ${contact.name} from your emergency contacts?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteContact(contact.id)
                        contactToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { contactToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InfoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = "Emergency contacts receive SMS alerts when you activate SOS",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Primary contacts are always alerted. Secondary contacts are backups.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onAddContact: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Emergency Contacts",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add trusted contacts who will receive alerts when you activate SOS",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddContact) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add First Contact")
        }
    }
}

@Composable
private fun EmergencyContactCard(
    contact: EmergencyContact,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit,
    onTogglePrimary: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (contact.isVerified) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = "Verified",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (contact.relationship.isNotBlank()) {
                        Text(
                            text = contact.relationship.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (contact.customMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Custom message: \"${contact.customMessage}\"",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (contact.lastTestedDate != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Last tested: ${formatTimestamp(contact.lastTestedDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Actions Menu
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (contact.isPrimary) "Make Secondary" else "Make Primary") },
                            onClick = {
                                showMenu = false
                                onTogglePrimary()
                            },
                            leadingIcon = { Icon(Icons.Default.SwapVert, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Test Alert") },
                            onClick = {
                                showMenu = false
                                onTest()
                            },
                            leadingIcon = { Icon(Icons.Default.Send, null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEditContactDialog(
    contact: EmergencyContact?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, relationship: String, isPrimary: Boolean, customMessage: String) -> Unit
) {
    var name by remember { mutableStateOf(contact?.name ?: "") }
    var phone by remember { mutableStateOf(contact?.phoneNumber ?: "") }
    var relationship by remember { mutableStateOf(contact?.relationship ?: "friend") }
    var isPrimary by remember { mutableStateOf(contact?.isPrimary ?: true) }
    var customMessage by remember { mutableStateOf(contact?.customMessage ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (contact == null) "Add Emergency Contact" else "Edit Contact") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("(555) 123-4567") }
                )

                // Relationship Dropdown
                var expanded by remember { mutableStateOf(false) }
                val relationships = listOf("friend", "family", "advocate", "attorney", "therapist", "other")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = relationship.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Relationship") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        relationships.forEach { rel ->
                            DropdownMenuItem(
                                text = { Text(rel.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    relationship = rel
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Primary Contact")
                    Switch(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it }
                    )
                }

                OutlinedTextField(
                    value = customMessage,
                    onValueChange = { customMessage = it },
                    label = { Text("Custom Alert Message (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    placeholder = { Text("This message will be sent with SOS alerts") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name.trim(), phone.trim(), relationship, isPrimary, customMessage.trim())
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Format timestamp for display
 */
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val hours = diff / (1000 * 60 * 60)
    val days = hours / 24

    return when {
        hours < 1 -> "Just now"
        hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
        days < 7 -> "$days day${if (days == 1L) "" else "s"} ago"
        else -> {
            val date = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US)
            date.format(java.util.Date(timestamp))
        }
    }
}
