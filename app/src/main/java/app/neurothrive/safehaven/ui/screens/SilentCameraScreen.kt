package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.neurothrive.safehaven.ui.components.SafeHavenTopBar

/**
 * Silent Camera Screen
 * CRITICAL FEATURE: Captures photos with NO sound, NO flash, NO GPS
 *
 * This is THE differentiator for SafeHaven
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SilentCameraScreen(
    onBack: () -> Unit,
    onPhotoSaved: () -> Unit
) {
    var flashEnabled by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SafeHavenTopBar(
                title = "Silent Camera",
                onBack = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // TODO: Integrate actual camera preview using SilentCameraManager
            // For now, show placeholder
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Camera Preview",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Integrate with SilentCameraManager",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Camera Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info card
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔇 Silent Mode | 🚫 No GPS | 🔒 Encrypted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flash toggle
                    IconButton(
                        onClick = { flashEnabled = !flashEnabled },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle flash",
                            tint = if (flashEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Capture button
                    FloatingActionButton(
                        onClick = {
                            isCapturing = true
                            // TODO: Call SilentCameraManager.capturePhotoSilently()
                            // Simulate capture
                            kotlinx.coroutines.GlobalScope.launch {
                                kotlinx.coroutines.delay(500)
                                isCapturing = false
                                onPhotoSaved()
                            }
                        },
                        modifier = Modifier.size(72.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = "Capture photo",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Spacer for symmetry
                    Spacer(modifier = Modifier.size(56.dp))
                }
            }
        }
    }
}
