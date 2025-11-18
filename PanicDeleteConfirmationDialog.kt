package app.neurothrive.safehaven.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Panic Delete Confirmation Dialog with 5-Second Hold Requirement
 *
 * CRITICAL SAFETY FEATURE:
 * - Prevents ALL false positive deletions (toddlers, pocket presses, drops)
 * - Requires 5-second continuous hold to confirm deletion
 * - Shows clear "ACCIDENTAL" cancel button
 * - Displays exactly what will be deleted
 * - Cannot be bypassed or dismissed accidentally
 *
 * @param triggerMethod How panic delete was triggered ("Power button", "Shake", "PIN")
 * @param incidentCount Number of incident reports that will be deleted
 * @param evidenceCount Number of evidence items (photos/videos) that will be deleted
 * @param documentCount Number of verified documents that will be deleted
 * @param onConfirm Callback when user completes 5-second hold (actually delete)
 * @param onDismiss Callback when user cancels (accidental trigger)
 */
@Composable
fun PanicDeleteConfirmationDialog(
    triggerMethod: String,
    incidentCount: Int = 0,
    evidenceCount: Int = 0,
    documentCount: Int = 0,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var holdProgress by remember { mutableStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Animate the hold progress
    val animatedProgress by animateFloatAsState(
        targetValue = holdProgress,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
        label = "Hold Progress Animation"
    )

    AlertDialog(
        onDismissRequest = {
            // Intentionally empty - user MUST press cancel button
            // Cannot dismiss by clicking outside or back button
            Timber.i("User attempted to dismiss panic delete dialog - ignoring")
        },
        properties = DialogProperties(
            dismissOnBackPress = false,      // CANNOT dismiss with back button
            dismissOnClickOutside = false,   // CANNOT dismiss by clicking outside
            usePlatformDefaultWidth = false  // Full width for better visibility
        ),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(horizontal = 16.dp),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(40.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "EMERGENCY DELETE?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Was this intentional?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show what triggered the panic delete
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "⚠️ Your phone detected:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            triggerMethod,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Common accidental scenarios
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "💡 Could this be accidental?",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            "Common false triggers:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            AccidentalScenarioItem("Phone in pocket or bag")
                            AccidentalScenarioItem("Dropped phone")
                            AccidentalScenarioItem("Child playing with phone")
                            AccidentalScenarioItem("Sitting on phone")
                            AccidentalScenarioItem("Vigorous activity/exercise")
                        }
                    }
                }

                Divider(thickness = 2.dp)

                // What will be deleted
                Text(
                    "If you continue, this will PERMANENTLY DELETE:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DeletionItem("All incident reports", incidentCount)
                        DeletionItem("All evidence photos/videos", evidenceCount)
                        DeletionItem("All verified documents", documentCount)
                        DeletionItem("All healthcare journeys", null)
                        DeletionItem("Your profile and settings", null)
                    }
                }

                // Cannot be undone warning
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "⚠️ THIS CANNOT BE UNDONE",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onError,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            // DELETE button with 5-second hold requirement
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Handled by hold gesture */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHolding = true
                                    Timber.d("User started holding DELETE button")

                                    val pressJob = coroutineScope.launch {
                                        var elapsed = 0L
                                        val holdDuration = 5000L // 5 seconds

                                        while (elapsed < holdDuration && isHolding) {
                                            delay(50) // Update every 50ms for smooth animation
                                            elapsed += 50
                                            holdProgress = elapsed / holdDuration.toFloat()
                                        }

                                        if (isHolding && elapsed >= holdDuration) {
                                            // User held for full 5 seconds
                                            Timber.w("PANIC DELETE CONFIRMED - User held for 5 seconds")
                                            onConfirm()
                                        }
                                    }

                                    // Wait for release
                                    tryAwaitRelease()

                                    // User released before 5 seconds
                                    isHolding = false
                                    pressJob.cancel()

                                    if (holdProgress < 1f) {
                                        Timber.i("User released DELETE button early (${(holdProgress * 5).toInt()} seconds)")
                                    }

                                    // Reset progress with slight delay for visual feedback
                                    coroutineScope.launch {
                                        delay(200)
                                        holdProgress = 0f
                                    }
                                }
                            )
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background progress bar
                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            trackColor = Color.Transparent
                        )

                        // Button content
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (holdProgress > 0) {
                                // Show progress while holding
                                CircularProgressIndicator(
                                    progress = animatedProgress,
                                    modifier = Modifier.size(28.dp),
                                    color = MaterialTheme.colorScheme.onError,
                                    strokeWidth = 4.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        "HOLD TO DELETE...",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${((1f - animatedProgress) * 5).toInt() + 1} seconds remaining",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            } else {
                                // Initial state
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        "HOLD TO DELETE EVERYTHING",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Press and hold for 5 seconds",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Instruction text
                if (holdProgress == 0f) {
                    Text(
                        "👆 Press and HOLD for 5 seconds to confirm",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        dismissButton = {
            // CANCEL button - large and easy to press
            OutlinedButton(
                onClick = {
                    Timber.i("User confirmed this was accidental - panic delete cancelled")
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        "ACCIDENTAL - DON'T DELETE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Keep my evidence safe",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    )
}

/**
 * Displays a single accidental scenario in the warning card
 */
@Composable
private fun AccidentalScenarioItem(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            "•",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

/**
 * Displays a single item that will be deleted
 */
@Composable
private fun DeletionItem(text: String, count: Int?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(20.dp)
        )
        Text(
            if (count != null && count > 0) "$text ($count)" else text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = if (count != null && count > 0) FontWeight.Bold else FontWeight.Normal
        )
    }
}
