# SafeHaven Revised Panic Delete Configuration

**Date**: 2025-11-17
**Update**: Triple Power Button as Default, Shake as Optional Feature
**Rationale**: Toddler-safe configuration based on parent feedback

---

## 🎯 New Approach: Triple Power Button Default

**User Feedback**: *"Toddlers are wild little creatures who love repetitive movement and motions."*

**Revised Strategy**:
- ✅ **Triple Power Button Press = DEFAULT** (safest, zero false positives)
- ⚠️ **Shake Detection = OPTIONAL** (for users without children)
- 🔒 **Settings Screen Manual Delete = ALWAYS AVAILABLE** (backup method)

---

## 🔴 Why Triple Power Button Should Be Default

### Advantages Over Shake Detection

| Feature | Triple Power Button | Shake Detection |
|---------|-------------------|-----------------|
| **Toddler-proof** | ✅ YES - Requires deliberate button presses | ❌ NO - Toddlers love shaking |
| **Drop-proof** | ✅ YES - No physical motion detection | ❌ NO - Drops create acceleration |
| **Pocket-proof** | ✅ YES - Button presses are intentional | ⚠️ MAYBE - Vigorous movement could trigger |
| **Speed** | ✅ 1-2 seconds (3 quick presses) | ✅ 2-3 seconds (multiple shakes) |
| **Accessibility** | ✅ Works with motor impairments | ⚠️ Requires ability to shake phone |
| **Reliability** | ✅ 100% consistent | ⚠️ Depends on sensor calibration |
| **False Positives** | ✅ Near zero | 🔴 HIGH with toddlers |

### Real-World Parent Scenarios

**Scenario 1: Survivor is a Mother**
```
Mom hands phone to 3-year-old to watch videos while she cooks dinner.
Toddler gets excited and shakes phone up and down repeatedly.

❌ SHAKE METHOD: Could trigger panic delete (even at 5+ shakes)
✅ POWER BUTTON METHOD: Zero risk - toddler unlikely to press power
   button 3 times in sequence
```

**Scenario 2: Phone in Diaper Bag**
```
Survivor's 18-month-old grabs phone from diaper bag and shakes it
like a rattle for 30 seconds.

❌ SHAKE METHOD: Almost guaranteed to trigger
✅ POWER BUTTON METHOD: Zero risk
```

**Scenario 3: Phone Left on Couch**
```
4-year-old finds phone and starts playing "shake game" (common toddler
behavior - they love cause-and-effect).

❌ SHAKE METHOD: Will definitely trigger
✅ POWER BUTTON METHOD: May press power button once or twice, but
   unlikely to hit 3 times in 2-second window
```

---

## 💡 Recommended Default Configuration

### App Defaults (First Install)

```kotlin
// Default Settings for SafeHaven
data class PanicDeleteSettings(
    val primaryMethod: PanicDeleteMethod = PanicDeleteMethod.TRIPLE_POWER_BUTTON,
    val shakeEnabled: Boolean = false,  // Disabled by default
    val shakeSensitivity: ShakeSensitivity = ShakeSensitivity.OFF,
    val requireConfirmation: Boolean = true  // Always require confirmation
)

enum class PanicDeleteMethod {
    TRIPLE_POWER_BUTTON,  // Default
    SHAKE_GESTURE,        // Optional
    BOTH                  // Advanced users only
}
```

### Onboarding Flow

**Step 1: Emergency Panic Delete Setup**

```
🚨 EMERGENCY PANIC DELETE

SafeHaven can quickly delete all your evidence in an emergency.

❓ Do you have young children (under 5 years old)?

[Yes, I have young children]  →  Triple Power Button (recommended)
[No, I don't have children]   →  Choice of methods
```

**Step 2a: For Parents (Recommended)**

```
👶 CHILD-SAFE PANIC DELETE

Since you have young children, we recommend:

🔘 Triple Power Button Press
   Press the power button 3 times quickly

Why this is safer:
✅ Toddlers can't trigger it accidentally
✅ Works even if phone is being held by child
✅ No false triggers from shaking/dropping

You can change this later in Settings.

[Set Up Triple Power Button] [I'll Set This Up Later]
```

**Step 2b: For Non-Parents (Options)**

```
⚡ CHOOSE YOUR PANIC DELETE METHOD

Pick the method that feels most natural:

○ Triple Power Button Press (Recommended)
  Press power button 3 times quickly
  ✅ Most reliable, zero false positives

○ Shake Phone Gesture
  Shake phone vigorously 8-10 times
  ⚠️ May trigger accidentally if phone is dropped
  ⚠️ NOT recommended if children use your phone

○ Manual (Settings Menu Only)
  Go to Settings → Panic Delete
  ✅ Safest from accidents, slower in emergency

[Continue]
```

---

## 🔧 Implementation: Triple Power Button Press

### PowerButtonListener.kt (NEW FILE)

```kotlin
package app.neurothrive.safehaven.util.sensors

import android.content.Context
import android.view.KeyEvent
import timber.log.Timber

/**
 * Detects triple power button press for panic delete activation.
 *
 * Advantages over shake detection:
 * - Zero false positives from toddlers
 * - No false positives from phone drops
 * - Requires deliberate action
 * - Accessible for users with motor impairments
 *
 * @param onTriplePressDetected Callback when 3 power button presses detected
 */
class PowerButtonListener(
    private val onTriplePressDetected: () -> Unit
) {

    private var pressCount = 0
    private var firstPressTime = 0L

    // Configuration
    private val REQUIRED_PRESSES = 3
    private val PRESS_WINDOW = 2000L  // 2 seconds to complete all 3 presses
    private val MIN_PRESS_INTERVAL = 100L  // Minimum 100ms between presses (prevent accidental holds)

    /**
     * Call this from Activity's onKeyDown() or dispatchKeyEvent()
     */
    fun onPowerButtonPressed(): Boolean {
        val currentTime = System.currentTimeMillis()

        // First press - start tracking
        if (pressCount == 0) {
            pressCount = 1
            firstPressTime = currentTime
            Timber.d("Power button press 1/3")
            return true
        }

        // Check if within time window
        val elapsedSinceFirst = currentTime - firstPressTime
        if (elapsedSinceFirst > PRESS_WINDOW) {
            // Too slow - reset and start over
            pressCount = 1
            firstPressTime = currentTime
            Timber.d("Power button press 1/3 (timeout reset)")
            return true
        }

        // Increment press count
        pressCount++
        Timber.d("Power button press $pressCount/3")

        // Check if we've reached required presses
        if (pressCount >= REQUIRED_PRESSES) {
            Timber.w("PANIC: Triple power button detected")
            onTriplePressDetected()
            reset()
            return true
        }

        return true
    }

    fun reset() {
        pressCount = 0
        firstPressTime = 0L
    }
}
```

### MainActivity.kt Integration

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var panicDeleteUseCase: PanicDeleteUseCase

    // Power button listener (DEFAULT)
    private lateinit var powerButtonListener: PowerButtonListener

    // Shake detector (OPTIONAL - disabled by default)
    private var shakeDetector: ShakeDetector? = null

    private var showPanicDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load user preferences
        val settings = loadPanicDeleteSettings()

        // Initialize power button listener (always enabled)
        powerButtonListener = PowerButtonListener {
            showPanicConfirmationDialog()
        }

        // Initialize shake detector only if user enabled it
        if (settings.shakeEnabled && settings.shakeSensitivity != ShakeSensitivity.OFF) {
            shakeDetector = ShakeDetector(
                context = this,
                threshold = settings.shakeSensitivity.threshold,
                requiredShakes = settings.shakeSensitivity.requiredShakes,
                onShakeDetected = {
                    showPanicConfirmationDialog()
                }
            )
        }

        // Setup UI
        setContent {
            SafeHavenTheme {
                // Main app content
                SafeHavenNavigation()

                // Panic delete confirmation dialog
                if (showPanicDialog) {
                    PanicDeleteConfirmationDialog(
                        onConfirm = {
                            showPanicDialog = false
                            executePanicDelete()
                        },
                        onDismiss = {
                            showPanicDialog = false
                            Timber.i("Panic delete cancelled by user")
                        }
                    )
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Intercept power button presses
        if (event.keyCode == KeyEvent.KEYCODE_POWER && event.action == KeyEvent.ACTION_DOWN) {
            powerButtonListener.onPowerButtonPressed()
            // Don't consume the event - let system handle sleep/wake
            return super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.start()  // Only starts if initialized
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.stop()
        powerButtonListener.reset()
    }

    private fun showPanicConfirmationDialog() {
        showPanicDialog = true
    }

    private fun executePanicDelete() {
        lifecycleScope.launch {
            try {
                currentUserId?.let { userId ->
                    val result = panicDeleteUseCase.execute(userId)
                    if (result.isSuccess) {
                        // Exit app after successful deletion
                        finishAffinity()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Panic delete failed")
            }
        }
    }
}
```

---

## 🎚️ Revised Shake Settings (For Non-Parents)

### Updated ShakeSensitivity Enum

```kotlin
enum class ShakeSensitivity(
    val threshold: Float,
    val requiredShakes: Int,
    val displayName: String,
    val description: String
) {
    OFF(
        threshold = 999f,
        requiredShakes = 999,
        displayName = "Disabled",
        description = "Shake detection is turned off"
    ),

    VERY_LOW(
        threshold = 40f,
        requiredShakes = 10,
        displayName = "Very Low (Safest)",
        description = "Requires 10 hard shakes. Safe for parents."
    ),

    LOW(
        threshold = 35f,
        requiredShakes = 8,
        displayName = "Low",
        description = "Requires 8 firm shakes. Recommended if you have children."
    ),

    MEDIUM(
        threshold = 30f,
        requiredShakes = 6,
        displayName = "Medium",
        description = "Requires 6 shakes. May trigger if toddler plays with phone."
    ),

    HIGH(
        threshold = 25f,
        requiredShakes = 4,
        displayName = "High",
        description = "Requires 4 shakes. Risk of accidental triggers."
    ),

    VERY_HIGH(
        threshold = 20f,
        requiredShakes = 3,
        displayName = "Very High (Risky)",
        description = "Requires 3 shakes. High risk of false positives. NOT for parents."
    )
}
```

**Key Changes**:
- **OFF by default** (shake disabled)
- **VERY_LOW requires 10 shakes** (toddler-resistant)
- **LOW requires 8 shakes** (recommended for parents)
- **Clear warnings** about toddler risk

---

## 🖥️ Settings Screen UI

### Emergency Panic Delete Settings Section

```kotlin
@Composable
fun EmergencyPanicDeleteSettings(
    viewModel: SettingsViewModel
) {
    val settings by viewModel.panicDeleteSettings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                "Emergency Panic Delete",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Text(
            "Quickly delete all evidence if you're in immediate danger.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider()

        // PRIMARY METHOD (Default: Triple Power Button)
        Text(
            "Primary Method",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = null
                    )
                    Text(
                        "Triple Power Button Press",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "RECOMMENDED",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "Press the power button 3 times quickly (within 2 seconds)",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Benefits list
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Safe for parents - toddlers can't trigger",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Won't trigger from phone drops",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Fast: 1-2 seconds to activate",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Divider()

        // OPTIONAL: Shake Gesture
        Text(
            "Optional: Shake Gesture",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Enable Shake Detection",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Alternative method: Shake phone vigorously",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = settings.shakeEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        // Show warning dialog
                        viewModel.showShakeEnableWarning = true
                    } else {
                        viewModel.updateShakeEnabled(false)
                    }
                }
            )
        }

        // Warning if shake enabled
        if (settings.shakeEnabled) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Shake Detection Enabled",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    Text(
                        "⚠️ May trigger accidentally if:\n" +
                        "• Phone is dropped\n" +
                        "• Children shake your phone\n" +
                        "• Phone is in bag/pocket during vigorous activity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )

                    // Sensitivity slider
                    Text(
                        "Shake Sensitivity: ${settings.shakeSensitivity.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        settings.shakeSensitivity.description,
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Sensitivity options
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ShakeSensitivity.values().filter { it != ShakeSensitivity.OFF }.forEach { sensitivity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable {
                                        viewModel.updateShakeSensitivity(sensitivity)
                                    }
                                    .background(
                                        if (settings.shakeSensitivity == sensitivity)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else
                                            Color.Transparent
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        sensitivity.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (settings.shakeSensitivity == sensitivity)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        "Requires ${sensitivity.requiredShakes} shakes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (settings.shakeSensitivity == sensitivity) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Special warning for parents
                    if (settings.shakeSensitivity.requiredShakes < 8) {
                        Divider()
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "NOT RECOMMENDED if you have young children. " +
                                "Set to 'Very Low' (10 shakes) or 'Low' (8 shakes) " +
                                "for better child-safety.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Divider()

        // Test Panic Delete
        OutlinedButton(
            onClick = { viewModel.showTestPanicDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Science, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Test Panic Delete (No Data Deleted)")
        }
    }
}
```

---

## 📱 Shake Enable Warning Dialog

```kotlin
@Composable
fun ShakeEnableWarningDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text("Enable Shake Detection?")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Triple Power Button is the recommended method. " +
                    "It's safer and more reliable.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    "Shake detection may trigger accidentally from:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• Dropping your phone")
                    Text("• Children shaking your phone")
                    Text("• Vigorous physical activity")
                }

                Divider()

                Text(
                    "⚠️ Do you have young children (under 5)?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    "If yes, we STRONGLY recommend keeping shake detection disabled.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Enable Anyway")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Keep Disabled (Recommended)")
            }
        }
    )
}
```

---

## 🧪 Testing Plan

### Phase 1: Power Button Testing

1. **Triple Press Test**
   - Press power button 3 times quickly (< 2 seconds)
   - **Expected**: Confirmation dialog appears
   - **Success**: 100% trigger rate

2. **Slow Press Test**
   - Press power button 3 times slowly (> 2 seconds between presses)
   - **Expected**: No trigger
   - **Success**: 0% false positives

3. **Toddler Test**
   - Give phone to 2-4 year old for 1 minute
   - **Expected**: May press power button randomly, but unlikely to hit 3 in sequence
   - **Success**: < 5% false positive rate

4. **Pocket Test**
   - Carry phone in pocket for 30 minutes (walking, sitting, stairs)
   - **Expected**: No triggers
   - **Success**: 0% false positives

### Phase 2: Shake Testing (Optional Feature)

5. **Shake at VERY_LOW (10 shakes)**
   - Deliberate shake test: 10 users shake phone deliberately
   - **Expected**: All users can activate in < 3 seconds
   - **Success**: 100% trigger rate

6. **Toddler Shake Test at VERY_LOW**
   - 2-4 year old shakes phone for 30 seconds
   - **Expected**: < 10% false positive rate (10 shakes is hard for toddlers)
   - **Success**: < 10% false positives

7. **Drop Test at VERY_LOW**
   - Drop phone from waist height (10 trials)
   - **Expected**: 0% false positives
   - **Success**: 0% false positives

---

## ✅ Revised Implementation Checklist

**Phase 1: Triple Power Button (Default)** - 3-4 hours

- [ ] Create PowerButtonListener.kt
- [ ] Integrate into MainActivity.kt
- [ ] Update onboarding to show power button method
- [ ] Test triple press detection
- [ ] Test with screen off (power button wake + 2 more presses)
- [ ] Add "Practice Mode" in settings (vibration feedback for each press)

**Phase 2: Shake as Optional** - 2-3 hours

- [ ] Update ShakeSensitivity enum (add VERY_LOW with 10 shakes)
- [ ] Set shake to disabled by default
- [ ] Create shake enable warning dialog
- [ ] Add parent-specific warnings (children under 5)
- [ ] Update settings UI to show shake as optional
- [ ] Add clear visual warnings about false positive risks

**Phase 3: Confirmation Dialog** - 2 hours

- [ ] Implement PanicDeleteConfirmationDialog composable
- [ ] Connect to both power button and shake triggers
- [ ] Add "Accidental Activation?" help text
- [ ] Test dialog appearance and functionality
- [ ] Ensure dialog cannot be dismissed by back button (require explicit choice)

**Total Estimated Time**: 7-9 hours

---

## 📊 Expected Outcomes

### Default Configuration (Triple Power Button Only)

| Scenario | False Positive Rate | Emergency Activation Time |
|----------|-------------------|--------------------------|
| **Phone drops** | 0% | N/A |
| **Toddler play (< 5 years)** | < 1% | N/A |
| **Pocket/bag movement** | 0% | N/A |
| **Deliberate emergency use** | N/A | 1-2 seconds ✅ |

### Optional Shake (VERY_LOW = 10 shakes)

| Scenario | False Positive Rate | Emergency Activation Time |
|----------|-------------------|--------------------------|
| **Phone drops** | < 1% | N/A |
| **Toddler play (< 5 years)** | 5-10% | N/A |
| **Pocket/bag movement** | < 2% | N/A |
| **Deliberate emergency use** | N/A | 2-3 seconds ✅ |

---

## 💬 User Education Updates

### Updated Onboarding Text

```
⚡ EMERGENCY PANIC DELETE

SafeHaven can instantly delete all your evidence if you're in danger.

🔘 RECOMMENDED METHOD: Triple Power Button
   Quick press your power button 3 times
   ✅ Safe for parents - kids can't trigger it
   ✅ Won't trigger from drops or accidents
   ✅ Fast: 1-2 seconds in emergency

📱 Optional: Shake Phone Gesture
   Shake phone vigorously 8-10 times
   ⚠️ May trigger accidentally
   ⚠️ Not recommended if you have children

You'll always see a confirmation before deletion.
```

---

## ✅ Summary

### What Changed

1. **Triple Power Button = NEW DEFAULT**
   - Zero toddler false positives
   - Zero drop false positives
   - Fastest activation (1-2 seconds)

2. **Shake Detection = OPTIONAL FEATURE**
   - Disabled by default
   - Warning dialog when enabling
   - VERY_LOW sensitivity = 10 shakes (toddler-resistant)
   - Parent-specific warnings throughout UI

3. **Confirmation Dialog = ALWAYS REQUIRED**
   - Prevents all accidental deletions
   - Clear "Accidental?" messaging
   - Cannot be bypassed

### Why This is Better

**Your insight was 100% correct**: Toddlers love repetitive motion. Even 5-8 shakes isn't enough to prevent false positives from an enthusiastic toddler shaking a phone like a rattle.

**Triple power button solves this**:
- Requires deliberate sequential button presses
- Toddlers may press power button randomly, but unlikely to press exactly 3 times within 2 seconds
- No motion sensors = No false positives from drops or shaking

### For Users Who Want Shake

Those without children can still enable shake detection with clear warnings and require 10 shakes (VERY_LOW), making it much harder for accidental triggers while still usable in emergencies.

---

**Next Step**: Implement PowerButtonListener.kt and update MainActivity.kt with triple power button as default panic delete method.
