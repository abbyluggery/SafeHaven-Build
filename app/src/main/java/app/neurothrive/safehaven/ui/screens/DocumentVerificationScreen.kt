package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar

/**
 * Document Verification Screen
 * Cryptographic verification for IDs, passports, birth certificates
 *
 * Use case: Survivor can't access original documents (abuser controls them)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentVerificationScreen(
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    var documentType by remember { mutableStateOf("passport") }
    var isVerifying by remember { mutableStateOf(false) }

    val documentTypes = listOf(
        "passport" to "Passport",
        "id" to "Driver's License / ID",
        "birth_cert" to "Birth Certificate",
        "ssn" to "Social Security Card",
        "other" to "Other Document"
    )

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Document Verification",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Verify Your Documents",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(32.dp))

            // Document Type Selection
            Text(
                text = "Select Document Type",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            documentTypes.forEach { (value, label) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label)
                    RadioButton(
                        selected = documentType == value,
                        onClick = { documentType = value }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Capture Button
            Button(
                onClick = {
                    isVerifying = true
                    // TODO: Launch camera to capture document photo
                    // Then call DocumentVerificationService.verifyDocument()
                    // Generate SHA-256 hash and optional blockchain timestamp
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isVerifying
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Capture & Verify Document")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Card
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
}
