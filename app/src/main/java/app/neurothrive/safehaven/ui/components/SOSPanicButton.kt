package app.neurothrive.safehaven.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SOS Panic Button Component
 *
 * Features:
 * - Long-press activation (3 seconds)
 * - Visual progress indicator
 * - Confirmation dialog (prevents accidental activation)
 * - Deactivation when already active
 *
 * Usage:
 * ```
 * SOSPanicButton(
 *     isActive = isSOSActive,
 *     onActivate = { viewModel.activateSOS() },
 *     onDeactivate = { viewModel.deactivateSOS() }
 * )
 * ```
 */
@Composable
fun SOSPanicButton(
    isActive: Boolean,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmation by remember { mutableStateOf(false) }
    var longPressProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    // Pulsing animation when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .scale(if (isActive) scale else 1f)
        ) {
            // Progress ring during long-press
            if (longPressProgress > 0f && !isActive) {
                CircularProgressIndicator(
                    progress = longPressProgress,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    strokeWidth = 8.dp,
                    color = Color(0xFFFF6B6B)
                )
            }

            // Main button
            Surface(
                onClick = {
                    if (isActive) {
                        onDeactivate()
                    }
                },
                modifier = Modifier
                    .size(180.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (!isActive) {
                                    // Start long-press timer
                                    scope.launch {
                                        for (i in 0..30) {
                                            longPressProgress = i / 30f
                                            delay(100) // 3 seconds total

                                            if (!tryAwaitRelease()) {
                                                // User released early
                                                longPressProgress = 0f
                                                return@launch
                                            }
                                        }
                                        // Long-press completed
                                        longPressProgress = 0f
                                        showConfirmation = true
                                    }
                                }
                            }
                        )
                    },
                shape = CircleShape,
                color = if (isActive) Color(0xFFFF3B30) else Color(0xFFFF6B6B),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS",
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isActive) "SOS\nACTIVE" else "SOS",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Instructions
        Text(
            text = if (isActive) {
                "SOS is active\nTap to deactivate"
            } else {
                "Hold for 3 seconds\nto activate emergency alerts"
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        // Active status indicator
        if (isActive) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF3B30).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF3B30)
                    )
                    Column {
                        Text(
                            text = "Emergency alerts active",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF3B30)
                        )
                        Text(
                            text = "Your emergency contacts have been notified",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("Activate SOS?")
            },
            text = {
                Column {
                    Text("This will:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Send emergency SMS to all your contacts")
                    Text("• Share your current location")
                    Text("• Send location updates every 5 minutes")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap Cancel if this was a mistake.",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmation = false
                        onActivate()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF3B30)
                    )
                ) {
                    Text("Activate SOS")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * False Alarm Button
 * Allows user to mark accidental SOS activation
 */
@Composable
fun FalseAlarmButton(
    onFalseAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmation by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showConfirmation = true },
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Mark as False Alarm")
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Mark as False Alarm?") },
            text = {
                Text("This will send a message to your emergency contacts that this was a false alarm.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onFalseAlarm()
                    }
                ) {
                    Text("Send False Alarm Message")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
