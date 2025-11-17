package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar

/**
 * Profile Setup Screen
 * Collects intersectional identity for resource matching
 *
 * CRITICAL: This data enables intersectional resource matching
 * Trans survivors find trans-inclusive shelters
 * Male survivors find male-serving resources
 * Undocumented survivors find U-Visa support
 * etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onComplete: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var duressPassword by remember { mutableStateOf("") }

    // Intersectional identity
    var isLGBTQIA by remember { mutableStateOf(false) }
    var isTrans by remember { mutableStateOf(false) }
    var isNonBinary by remember { mutableStateOf(false) }
    var isBIPOC by remember { mutableStateOf(false) }
    var isMaleIdentifying by remember { mutableStateOf(false) }
    var isUndocumented by remember { mutableStateOf(false) }
    var isDisabled by remember { mutableStateOf(false) }
    var isDeaf by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SafeHavenTopBar(title = "Create Your Profile")
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
                text = "Set up your SafeHaven profile",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Passwords
            Text(
                text = "Security",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("SafeHaven Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = duressPassword,
                onValueChange = { duressPassword = it },
                label = { Text("Duress Password (Optional)") },
                supportingText = { Text("Shows fake data if forced to open app") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Intersectional Identity
            Text(
                text = "Help us find resources for you",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This information helps match you with resources that serve your community. All data stays encrypted on your device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Identity Checkboxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LGBTQIA+")
                Switch(checked = isLGBTQIA, onCheckedChange = { isLGBTQIA = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Transgender")
                Switch(checked = isTrans, onCheckedChange = { isTrans = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Non-binary")
                Switch(checked = isNonBinary, onCheckedChange = { isNonBinary = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("BIPOC")
                Switch(checked = isBIPOC, onCheckedChange = { isBIPOC = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Male-identifying")
                Switch(checked = isMaleIdentifying, onCheckedChange = { isMaleIdentifying = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Undocumented")
                Switch(checked = isUndocumented, onCheckedChange = { isUndocumented = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Disabled")
                Switch(checked = isDisabled, onCheckedChange = { isDisabled = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Deaf/Hard of Hearing")
                Switch(checked = isDeaf, onCheckedChange = { isDeaf = it })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // TODO: Save profile to database
                    onComplete()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Complete Setup")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
