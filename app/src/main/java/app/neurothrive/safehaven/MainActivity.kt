package app.neurothrive.safehaven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import app.neurothrive.safehaven.domain.usecases.PanicDeleteUseCase
import app.neurothrive.safehaven.ui.navigation.SafeHavenNavGraph
import app.neurothrive.safehaven.ui.navigation.Screen
import app.neurothrive.safehaven.ui.theme.SafeHavenTheme
import app.neurothrive.safehaven.util.sensors.ShakeDetector
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * SafeHaven Main Activity
 *
 * CRITICAL FEATURES:
 * - Shake detection for panic delete
 * - Material Design 3 theme
 * - Jetpack Compose UI with Navigation
 * - 12 feature screens
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var panicDeleteUseCase: PanicDeleteUseCase

    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shake detector for panic delete
        shakeDetector = ShakeDetector(this) {
            showPanicConfirmationDialog()
        }

        setContent {
            SafeHavenTheme {
                val navController = rememberNavController()

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    SafeHavenNavGraph(
                        navController = navController,
                        startDestination = Screen.Onboarding.route
                    )
                }
            }
        }

        Timber.d("MainActivity created with navigation")
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }

    /**
     * Show panic confirmation dialog
     * CRITICAL: Confirms before deleting all data
     */
    private fun showPanicConfirmationDialog() {
        // TODO: Implement Compose AlertDialog
        Timber.w("Panic delete triggered - dialog not yet implemented")
    }
}
