package app.neurothrive.safehaven.util.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import timber.log.Timber
import kotlin.math.sqrt

/**
 * Detects shake gestures using device accelerometer
 *
 * IMPORTANT SAFETY NOTES:
 * - This detector can trigger from drops, toddler play, or pocket movement
 * - ALWAYS require confirmation dialog before executing panic delete
 * - Recommend DISABLING by default (users opt-in via Settings)
 * - For parents with toddlers: Recommend 10+ shakes or use PIN method instead
 *
 * @param context Application context
 * @param threshold Acceleration threshold in m/s² (default 30f for medium sensitivity)
 *                  - 40f = Very low sensitivity (10 shakes recommended)
 *                  - 30f = Medium sensitivity (8-10 shakes recommended)
 *                  - 25f = High sensitivity (6-8 shakes recommended)
 *                  - 20f = Very high sensitivity (NOT recommended - too many false positives)
 * @param requiredShakes Number of shakes required to trigger (default 10 for toddler safety)
 *                       - 10 shakes = Toddler-resistant (recommended for parents)
 *                       - 8 shakes = Moderate safety
 *                       - 6 shakes = Higher false positive risk
 *                       - 3-4 shakes = NOT recommended (too easy to trigger accidentally)
 * @param onShakeDetected Callback when shake sequence completed
 */
class ShakeDetector(
    context: Context,
    private val threshold: Float = 30f,
    private val requiredShakes: Int = 10,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTime = 0L
    private var shakeCount = 0

    // Time window for shake sequence (2 seconds)
    private val SHAKE_COOLDOWN = 2000L

    init {
        Timber.d("ShakeDetector initialized: threshold=$threshold, requiredShakes=$requiredShakes")

        if (accelerometer == null) {
            Timber.w("No accelerometer detected on device - shake detection unavailable")
        }
    }

    /**
     * Start listening for shake events
     */
    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Timber.d("ShakeDetector started listening")
        } ?: Timber.w("Cannot start ShakeDetector - no accelerometer")
    }

    /**
     * Stop listening for shake events
     */
    fun stop() {
        sensorManager.unregisterListener(this)
        shakeCount = 0
        lastShakeTime = 0L
        Timber.d("ShakeDetector stopped listening")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate acceleration magnitude (minus gravity)
        // This gives us the "extra" acceleration beyond gravity (9.8 m/s²)
        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

        if (acceleration > threshold) {
            val currentTime = System.currentTimeMillis()

            if (shakeCount == 0) {
                // First shake in sequence
                shakeCount = 1
                lastShakeTime = currentTime
                Timber.d("Shake detected 1/$requiredShakes (acceleration: %.2f m/s²)", acceleration)
            } else {
                // Check if within time window
                val elapsedSinceFirst = currentTime - lastShakeTime

                if (elapsedSinceFirst < SHAKE_COOLDOWN) {
                    // Additional shake within window
                    shakeCount++
                    Timber.d("Shake detected $shakeCount/$requiredShakes (acceleration: %.2f m/s²)", acceleration)

                    if (shakeCount >= requiredShakes) {
                        // Shake threshold reached!
                        Timber.w("SHAKE SEQUENCE COMPLETE: $shakeCount shakes in ${elapsedSinceFirst}ms")
                        onShakeDetected()
                        reset()
                    }
                } else {
                    // Too much time passed - reset and start new sequence
                    shakeCount = 1
                    lastShakeTime = currentTime
                    Timber.d("Shake sequence timeout - restarting (1/$requiredShakes)")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for shake detection
    }

    /**
     * Reset shake counter (called after successful detection)
     */
    private fun reset() {
        shakeCount = 0
        lastShakeTime = 0L
    }
}

/**
 * Shake sensitivity presets for Settings UI
 */
enum class ShakeSensitivity(
    val threshold: Float,
    val requiredShakes: Int,
    val displayName: String,
    val description: String,
    val recommendedFor: String
) {
    OFF(
        threshold = 999f,
        requiredShakes = 999,
        displayName = "Disabled",
        description = "Shake detection is turned off",
        recommendedFor = "All users (use power button or PIN instead)"
    ),

    VERY_LOW(
        threshold = 40f,
        requiredShakes = 10,
        displayName = "Very Low (Safest)",
        description = "Requires 10 hard shakes",
        recommendedFor = "Parents with children under 5"
    ),

    LOW(
        threshold = 35f,
        requiredShakes = 8,
        displayName = "Low",
        description = "Requires 8 firm shakes",
        recommendedFor = "Parents with children 5-12"
    ),

    MEDIUM(
        threshold = 30f,
        requiredShakes = 6,
        displayName = "Medium",
        description = "Requires 6 moderate shakes",
        recommendedFor = "No children, phone not in pocket often"
    ),

    HIGH(
        threshold = 25f,
        requiredShakes = 4,
        displayName = "High",
        description = "Requires 4 shakes",
        recommendedFor = "No children, need fastest activation"
    ),

    VERY_HIGH(
        threshold = 20f,
        requiredShakes = 3,
        displayName = "Very High (Risky)",
        description = "Requires 3 shakes - HIGH false positive risk",
        recommendedFor = "NOT RECOMMENDED - Use power button or PIN instead"
    );

    /**
     * Get warning text for this sensitivity level
     */
    fun getWarningText(): String? {
        return when (this) {
            VERY_HIGH -> "⚠️ DANGER: Very high risk of accidental deletion from drops, toddlers, or pocket movement"
            HIGH -> "⚠️ WARNING: May trigger accidentally from phone drops or children"
            MEDIUM -> "⚠️ CAUTION: May trigger if toddlers shake your phone"
            else -> null
        }
    }

    /**
     * Check if this sensitivity is safe for parents
     */
    fun isSafeForParents(): Boolean {
        return this in listOf(OFF, VERY_LOW, LOW)
    }
}
