package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.neurothrive.safehaven.data.session.UserSession
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar
import app.neurothrive.safehaven.ui.viewmodels.LoginViewModel

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
    viewModel: LoginViewModel = hiltViewModel(),
    userSession: UserSession = hiltViewModel(),
    onComplete: () -> Unit
) {
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    var userId by remember { mutableStateOf("default_user") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var duressPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Intersectional identity
    var isLGBTQIA by remember { mutableStateOf(false) }
    var isTrans by remember { mutableStateOf(false) }
    var isNonBinary by remember { mutableStateOf(false) }
    var isBIPOC by remember { mutableStateOf(false) }
    var isMaleIdentifying by remember { mutableStateOf(false) }
    var isUndocumented by remember { mutableStateOf(false) }
    var isDisabled by remember { mutableStateOf(false) }
    var isDeaf by remember { mutableStateOf(false) }

    // Validation
    val passwordsMatch = password == confirmPassword
    val passwordValid = password.length >= 6
    val canSubmit = passwordValid && passwordsMatch && password.isNotBlank()

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your data is encrypted and stays on your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Hide password"
                            else
                                "Show password"
                        )
                    }
                },
                supportingText = {
                    if (!passwordValid && password.isNotBlank()) {
                        Text(
                            "Password must be at least 6 characters",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = !passwordValid && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    if (!passwordsMatch && confirmPassword.isNotBlank()) {
                        Text(
                            "Passwords do not match",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = !passwordsMatch && confirmPassword.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = duressPassword,
                onValueChange = { duressPassword = it },
                label = { Text("Duress Password (Optional)") },
                visualTransformation = PasswordVisualTransformation(),
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

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "This information helps match you with resources that serve your community. All data stays encrypted on your device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Identity Switches
            IdentitySwitch("LGBTQIA+", isLGBTQIA) { isLGBTQIA = it }
            IdentitySwitch("Transgender", isTrans) { isTrans = it }
            IdentitySwitch("Non-binary", isNonBinary) { isNonBinary = it }
            IdentitySwitch("BIPOC", isBIPOC) { isBIPOC = it }
            IdentitySwitch("Male-identifying", isMaleIdentifying) { isMaleIdentifying = it }
            IdentitySwitch("Undocumented", isUndocumented) { isUndocumented = it }
            IdentitySwitch("Disabled", isDisabled) { isDisabled = it }
            IdentitySwitch("Deaf/Hard of Hearing", isDeaf) { isDeaf = it }

            Spacer(modifier = Modifier.height(24.dp))

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

            Button(
                onClick = {
                    viewModel.createProfile(
                        userId = userId,
                        realPassword = password,
                        duressPassword = duressPassword.ifBlank { "default_duress_${password}" },
                        onSuccess = {
                            // Set user session
                            kotlinx.coroutines.GlobalScope.launch {
                                userSession.setCurrentUser(userId, isDuress = false)
                            }
                            onComplete()
                        }
                    )
                    // TODO: Also create SurvivorProfile with intersectional data
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canSubmit && !uiState.isAuthenticating
            ) {
                if (uiState.isAuthenticating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Setup")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skip button
            TextButton(
                onClick = {
                    // Create profile with default values
                    viewModel.createProfile(
                        userId = userId,
                        realPassword = "demo123",
                        duressPassword = "demo456",
                        onSuccess = {
                            kotlinx.coroutines.GlobalScope.launch {
                                userSession.setCurrentUser(userId, isDuress = false)
                            }
                            onComplete()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip for now (Demo Mode)")
            }
        }
    }
}

@Composable
private fun IdentitySwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
