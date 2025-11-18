package app.neurothrive.safehaven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import app.neurothrive.safehaven.domain.usecases.PanicDeleteUseCase
import app.neurothrive.safehaven.ui.components.PanicDeleteConfirmationDialog
import app.neurothrive.safehaven.ui.theme.SafeHavenTheme
import app.neurothrive.safehaven.util.sensors.ShakeDetector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Main Activity for SafeHaven App
 *
 * Integrates panic delete confirmation dialog with:
 * - Shake detection (optional, user-configurable)
 * - Power button detection (optional, user-configurable)
 * - PIN code detection (optional, user-configurable)
 *
 * ALL trigger methods route through the same 5-second hold confirmation dialog
 * for maximum safety against false positives.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var panicDeleteUseCase: PanicDeleteUseCase

    // Shake detector (optional - disabled by default)
    private var shakeDetector: ShakeDetector? = null

    // Dialog state
    private var showPanicDialog by mutableStateOf(false)
    private var panicTriggerMethod by mutableStateOf("")
    private var currentUserId: String? = null

    // Data counts for confirmation dialog
    private var incidentCount by mutableStateOf(0)
    private var evidenceCount by mutableStateOf(0)
    private var documentCount by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Load user preferences to determine which panic methods are enabled
        // For now, shake is disabled by default (user must enable in Settings)
        val shakeEnabled = false // TODO: Load from SharedPreferences/DataStore

        if (shakeEnabled) {
            // Initialize shake detector only if user enabled it
            shakeDetector = ShakeDetector(
                context = this,
                threshold = 30f,        // Default medium sensitivity
                requiredShakes = 10,    // Toddler-resistant
                onShakeDetected = {
                    showPanicConfirmationDialog(triggerMethod = "Phone shaken vigorously 10 times")
                }
            )
        }

        setContent {
            SafeHavenTheme {
                // Main app content
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Your main navigation/content goes here
                    // This is just a placeholder
                    SafeHavenNavigation()
                }

                // Panic delete confirmation dialog overlay
                if (showPanicDialog) {
                    PanicDeleteConfirmationDialog(
                        triggerMethod = panicTriggerMethod,
                        incidentCount = incidentCount,
                        evidenceCount = evidenceCount,
                        documentCount = documentCount,
                        onConfirm = {
                            showPanicDialog = false
                            executePanicDelete()
                        },
                        onDismiss = {
                            showPanicDialog = false
                            Timber.i("Panic delete cancelled by user - accidental trigger")
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Start shake detector if enabled
        shakeDetector?.start()
    }

    override fun onPause() {
        super.onPause()
        // Stop shake detector to save battery
        shakeDetector?.stop()
    }

    /**
     * Shows panic delete confirmation dialog
     *
     * @param triggerMethod Description of what triggered panic delete
     *                      Examples:
     *                      - "Phone shaken vigorously 10 times"
     *                      - "Power button pressed 3 times"
     *                      - "Emergency PIN entered (9999)"
     */
    private fun showPanicConfirmationDialog(triggerMethod: String) {
        lifecycleScope.launch {
            // Load current data counts for display in confirmation dialog
            try {
                currentUserId?.let { userId ->
                    // TODO: Query actual counts from database
                    // For now using placeholder values
                    incidentCount = 0  // repository.getIncidentCount(userId)
                    evidenceCount = 0  // repository.getEvidenceCount(userId)
                    documentCount = 0  // repository.getDocumentCount(userId)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load data counts for confirmation dialog")
            }

            // Set trigger method and show dialog
            panicTriggerMethod = triggerMethod
            showPanicDialog = true

            Timber.w("Panic delete triggered: $triggerMethod - showing confirmation dialog")
        }
    }

    /**
     * Actually executes panic delete after user confirms
     *
     * This is only called after user successfully holds DELETE button for 5 seconds
     */
    private fun executePanicDelete() {
        lifecycleScope.launch {
            try {
                currentUserId?.let { userId ->
                    Timber.w("EXECUTING PANIC DELETE for user: $userId")

                    // Show loading state (optional)
                    // showLoadingDialog()

                    // Execute panic delete use case
                    val result = panicDeleteUseCase.execute(userId)

                    if (result.isSuccess) {
                        Timber.i("Panic delete completed successfully")

                        // Close app completely (user expects this after panic delete)
                        finishAffinity()
                    } else {
                        Timber.e("Panic delete failed: ${result.exceptionOrNull()}")
                        // TODO: Show error dialog?
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Panic delete failed with exception")
                // TODO: Show error dialog?
            }
        }
    }

    /**
     * Placeholder navigation composable
     * Replace with your actual navigation implementation
     */
    @Composable
    private fun SafeHavenNavigation() {
        // TODO: Implement your actual navigation
        Box(modifier = Modifier.fillMaxSize()) {
            Text("SafeHaven Main Screen")
        }
    }
}
