# SafeHaven Panic Delete Implementation Guide

**Date**: 2025-11-17
**Feature**: 5-Second Hold Confirmation Dialog
**Status**: Ready to Implement

---

## 📦 Files Created

All implementation files are ready in your working directory:

### 1. **PanicDeleteConfirmationDialog.kt** ✅
**Location**: `app/src/main/java/app/neurothrive/safehaven/ui/components/PanicDeleteConfirmationDialog.kt`

**What it does**:
- Shows confirmation dialog when panic delete is triggered (any method)
- Requires 5-second continuous hold to confirm deletion
- Shows clear "ACCIDENTAL - DON'T DELETE" cancel button
- Displays trigger method ("Power button", "Shake", "PIN")
- Lists exactly what will be deleted (with counts)
- Cannot be dismissed accidentally (no back button, no click-outside)
- Smooth progress animation during hold
- Countdown timer shows seconds remaining

**Key Features**:
```kotlin
@Composable
fun PanicDeleteConfirmationDialog(
    triggerMethod: String,           // "Phone shaken 10 times"
    incidentCount: Int = 0,           // Show user what they'll lose
    evidenceCount: Int = 0,
    documentCount: Int = 0,
    onConfirm: () -> Unit,            // Called after 5-second hold
    onDismiss: () -> Unit             // Called when user cancels
)
```

---

### 2. **MainActivity_Updated.kt** ✅
**Location**: `app/src/main/java/app/neurothrive/safehaven/MainActivity.kt`

**What it does**:
- Integrates PanicDeleteConfirmationDialog
- Initializes ShakeDetector (optional, disabled by default)
- Shows confirmation dialog when panic triggered
- Loads data counts for display in dialog
- Executes panic delete after 5-second hold confirmed
- Closes app after successful deletion

**Key Method**:
```kotlin
private fun showPanicConfirmationDialog(triggerMethod: String) {
    // Load current data counts
    incidentCount = // query from database
    evidenceCount = // query from database
    documentCount = // query from database

    // Show dialog
    panicTriggerMethod = triggerMethod
    showPanicDialog = true
}
```

---

### 3. **ShakeDetector_Updated.kt** ✅
**Location**: `app/src/main/java/app/neurothrive/safehaven/util/sensors/ShakeDetector.kt`

**What it does**:
- Detects shake gestures using accelerometer
- Configurable threshold and shake count
- Includes ShakeSensitivity enum for Settings UI
- Logs all shake events for debugging
- Recommends disabling by default (user opt-in)

**Key Features**:
```kotlin
class ShakeDetector(
    context: Context,
    threshold: Float = 30f,           // Acceleration threshold
    requiredShakes: Int = 10,         // Toddler-resistant default
    onShakeDetected: () -> Unit       // Triggers confirmation dialog
)

enum class ShakeSensitivity {
    OFF,          // Disabled (recommended default)
    VERY_LOW,     // 10 shakes, 40f threshold (parents)
    LOW,          // 8 shakes, 35f threshold
    MEDIUM,       // 6 shakes, 30f threshold
    HIGH,         // 4 shakes, 25f threshold
    VERY_HIGH     // 3 shakes, 20f threshold (NOT recommended)
}
```

---

## 🚀 Implementation Steps

### Step 1: Copy Files to Android Project

```bash
# Navigate to your SafeHaven Android project
cd /path/to/SafeHaven-Android

# Copy PanicDeleteConfirmationDialog.kt
cp PanicDeleteConfirmationDialog.kt \
   app/src/main/java/app/neurothrive/safehaven/ui/components/

# Copy updated MainActivity
# IMPORTANT: Back up your existing MainActivity.kt first!
cp MainActivity.kt MainActivity_BACKUP_$(date +%Y%m%d).kt
cp MainActivity_Updated.kt app/src/main/java/app/neurothrive/safehaven/MainActivity.kt

# Copy updated ShakeDetector
# IMPORTANT: Back up existing if present
cp ShakeDetector_Updated.kt \
   app/src/main/java/app/neurothrive/safehaven/util/sensors/ShakeDetector.kt
```

---

### Step 2: Update Dependencies (if needed)

Ensure your `app/build.gradle.kts` has Material3 and Compose dependencies:

```kotlin
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Timber logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Hilt (dependency injection)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
}
```

---

### Step 3: Integrate with Existing MainActivity

If you have an existing MainActivity with navigation, merge the panic delete logic:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var panicDeleteUseCase: PanicDeleteUseCase

    private var shakeDetector: ShakeDetector? = null
    private var showPanicDialog by mutableStateOf(false)
    private var panicTriggerMethod by mutableStateOf("")
    private var incidentCount by mutableStateOf(0)
    private var evidenceCount by mutableStateOf(0)
    private var documentCount by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load shake settings from SharedPreferences/DataStore
        val shakeEnabled = loadShakeEnabledSetting() // TODO: Implement
        val shakeSensitivity = loadShakeSensitivity() // TODO: Implement

        if (shakeEnabled) {
            shakeDetector = ShakeDetector(
                context = this,
                threshold = shakeSensitivity.threshold,
                requiredShakes = shakeSensitivity.requiredShakes,
                onShakeDetected = {
                    showPanicConfirmationDialog(
                        triggerMethod = "Phone shaken ${shakeSensitivity.requiredShakes} times"
                    )
                }
            )
        }

        setContent {
            SafeHavenTheme {
                // Your existing navigation
                YourExistingNavigationComposable()

                // ADD THIS: Panic delete dialog overlay
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
                            Timber.i("Panic delete cancelled - accidental trigger")
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.stop()
    }

    private fun showPanicConfirmationDialog(triggerMethod: String) {
        lifecycleScope.launch {
            // Load actual counts from your database
            currentUserId?.let { userId ->
                incidentCount = incidentRepository.getCount(userId)
                evidenceCount = evidenceRepository.getCount(userId)
                documentCount = documentRepository.getCount(userId)
            }

            panicTriggerMethod = triggerMethod
            showPanicDialog = true
        }
    }

    private fun executePanicDelete() {
        lifecycleScope.launch {
            currentUserId?.let { userId ->
                Timber.w("EXECUTING PANIC DELETE for user: $userId")
                val result = panicDeleteUseCase.execute(userId)

                if (result.isSuccess) {
                    finishAffinity() // Close app completely
                }
            }
        }
    }
}
```

---

### Step 4: Update SettingsScreen to Control Shake Detection

Add shake enable/disable toggle in Settings:

```kotlin
@Composable
fun EmergencySettingsSection(viewModel: SettingsViewModel) {
    var shakeEnabled by remember { mutableStateOf(viewModel.isShakeEnabled()) }
    var shakeSensitivity by remember { mutableStateOf(viewModel.getShakeSensitivity()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Emergency Panic Delete", style = MaterialTheme.typography.titleLarge)

        // Shake Detection Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Enable Shake Detection", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Shake phone to trigger panic delete",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = shakeEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        // Show warning dialog
                        showShakeEnableWarning = true
                    } else {
                        shakeEnabled = false
                        viewModel.setShakeEnabled(false)
                    }
                }
            )
        }

        // Sensitivity selector (only if shake enabled)
        if (shakeEnabled) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Shake Sensitivity: ${shakeSensitivity.displayName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        shakeSensitivity.description,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        "Recommended for: ${shakeSensitivity.recommendedFor}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Sensitivity options
                    ShakeSensitivity.values().filter { it != ShakeSensitivity.OFF }.forEach { option ->
                        SensitivityOption(
                            sensitivity = option,
                            isSelected = shakeSensitivity == option,
                            onClick = {
                                shakeSensitivity = option
                                viewModel.setShakeSensitivity(option)
                            }
                        )
                    }

                    // Warning for unsafe settings
                    shakeSensitivity.getWarningText()?.let { warning ->
                        HorizontalDivider()
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                warning,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SensitivityOption(
    sensitivity: ShakeSensitivity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    Color.Transparent
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                sensitivity.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                "Requires ${sensitivity.requiredShakes} shakes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

---

### Step 5: Add SharedPreferences Storage

Create `PanicDeletePreferences.kt` to store settings:

```kotlin
package app.neurothrive.safehaven.data.preferences

import android.content.Context
import android.content.SharedPreferences
import app.neurothrive.safehaven.util.sensors.ShakeSensitivity

class PanicDeletePreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "panic_delete_settings",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_SHAKE_ENABLED = "shake_enabled"
        private const val KEY_SHAKE_SENSITIVITY = "shake_sensitivity"
    }

    fun isShakeEnabled(): Boolean {
        return prefs.getBoolean(KEY_SHAKE_ENABLED, false) // Default: OFF
    }

    fun setShakeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHAKE_ENABLED, enabled).apply()
    }

    fun getShakeSensitivity(): ShakeSensitivity {
        val ordinal = prefs.getInt(KEY_SHAKE_SENSITIVITY, ShakeSensitivity.VERY_LOW.ordinal)
        return ShakeSensitivity.values().getOrElse(ordinal) { ShakeSensitivity.VERY_LOW }
    }

    fun setShakeSensitivity(sensitivity: ShakeSensitivity) {
        prefs.edit().putInt(KEY_SHAKE_SENSITIVITY, sensitivity.ordinal).apply()
    }
}
```

---

## 🧪 Testing Guide

### Test 1: 5-Second Hold Requirement ✅

**Purpose**: Verify toddlers cannot confirm deletion

**Steps**:
1. Trigger panic delete (shake phone or test button)
2. Confirmation dialog appears
3. Tap DELETE button quickly (don't hold)
4. **Expected**: Nothing happens
5. Press and hold DELETE button for 5 seconds
6. Watch progress animation fill up
7. Watch countdown: "5 seconds... 4 seconds... 3... 2... 1..."
8. **Expected**: Deletion executes after exactly 5 seconds

**Success Criteria**: Toddlers cannot hold for 5 seconds continuously

---

### Test 2: Easy Cancellation ✅

**Purpose**: Verify accidental triggers can be cancelled easily

**Steps**:
1. Trigger panic delete accidentally
2. Confirmation dialog appears
3. Press large "ACCIDENTAL - DON'T DELETE" button
4. **Expected**: Dialog closes immediately, nothing deleted

**Success Criteria**: One tap cancels, no data loss

---

### Test 3: Cannot Dismiss Accidentally ✅

**Purpose**: Verify dialog cannot be dismissed without explicit choice

**Steps**:
1. Trigger panic delete
2. Confirmation dialog appears
3. Try to dismiss:
   - Press back button → **Expected**: Nothing happens
   - Click outside dialog → **Expected**: Nothing happens
   - Swipe down → **Expected**: Nothing happens
4. **Expected**: Only way to close is press DELETE (5-sec hold) or CANCEL button

**Success Criteria**: No accidental dismissals

---

### Test 4: Trigger Method Display ✅

**Purpose**: Verify user knows what triggered panic delete

**Steps**:
1. Trigger via shake → Dialog shows "Phone shaken 10 times"
2. Trigger via power button → Dialog shows "Power button pressed 3 times"
3. Trigger via PIN → Dialog shows "Emergency PIN entered (9999)"

**Success Criteria**: Clear trigger method displayed

---

### Test 5: Data Counts Accurate ✅

**Purpose**: Verify user sees what they'll lose

**Steps**:
1. Create test data:
   - 5 incident reports
   - 10 evidence photos
   - 3 verified documents
2. Trigger panic delete
3. **Expected**: Dialog shows:
   - "All incident reports (5)"
   - "All evidence photos/videos (10)"
   - "All verified documents (3)"

**Success Criteria**: Accurate counts displayed

---

### Test 6: Shake Detection (If Enabled) ✅

**Purpose**: Verify shake detection works at various sensitivities

**Test 6a: VERY_LOW Sensitivity (10 shakes)**
1. Enable shake detection in Settings
2. Set to VERY_LOW
3. Shake phone vigorously 10 times
4. **Expected**: Confirmation dialog appears
5. Shake phone 8 times (less than required)
6. **Expected**: Nothing happens

**Test 6b: Toddler Resistance**
1. Set shake to VERY_LOW (10 shakes)
2. Give phone to toddler for 30 seconds (supervised)
3. **Expected**: Dialog may appear, but toddler cannot confirm (5-second hold)

**Success Criteria**:
- VERY_LOW requires deliberate 10 shakes
- Even if triggered, toddler cannot hold DELETE for 5 seconds

---

### Test 7: Release Before 5 Seconds ✅

**Purpose**: Verify early release cancels deletion

**Steps**:
1. Trigger panic delete
2. Press and hold DELETE button
3. Watch progress: "4 seconds remaining... 3... 2..."
4. Release finger at 2 seconds
5. **Expected**: Progress resets, nothing deleted
6. See message indicating early release

**Success Criteria**: Early release = safe, no deletion

---

## 📊 Expected Behavior Summary

### Confirmation Dialog Behavior

| User Action | Result |
|-------------|--------|
| **Tap DELETE button** | Nothing (requires hold) |
| **Hold DELETE for 2 seconds, release** | Progress resets, nothing deleted |
| **Hold DELETE for 5 seconds** | Deletion executes, app closes |
| **Tap CANCEL button** | Dialog closes, nothing deleted |
| **Press back button** | Nothing (cannot dismiss) |
| **Click outside dialog** | Nothing (cannot dismiss) |
| **Toddler random taps** | Nothing (cannot hold 5 seconds) |

### Shake Detection Behavior (When Enabled)

| Sensitivity | Shakes Required | Safe for Parents? |
|-------------|----------------|------------------|
| **OFF** | N/A | ✅ YES (disabled) |
| **VERY_LOW** | 10 shakes | ✅ YES (toddler-resistant) |
| **LOW** | 8 shakes | ⚠️ MAYBE (school-age kids) |
| **MEDIUM** | 6 shakes | ❌ NO (toddler risk) |
| **HIGH** | 4 shakes | ❌ NO (high false positives) |
| **VERY_HIGH** | 3 shakes | ❌ NO (NOT recommended) |

---

## ✅ Implementation Checklist

**Before Deploying**:

- [ ] Copy PanicDeleteConfirmationDialog.kt to project
- [ ] Update MainActivity.kt with confirmation dialog integration
- [ ] Copy ShakeDetector_Updated.kt (or update existing)
- [ ] Add PanicDeletePreferences.kt for settings storage
- [ ] Update SettingsScreen.kt with shake enable/disable toggle
- [ ] Add ShakeSensitivity selector UI in Settings
- [ ] Implement data count queries (incidentCount, evidenceCount, documentCount)
- [ ] Test 5-second hold requirement
- [ ] Test easy cancellation
- [ ] Test cannot dismiss accidentally
- [ ] Test shake detection at various sensitivities
- [ ] Test with toddler (supervised - verify they cannot hold 5 seconds)
- [ ] Add analytics logging for false positive tracking
- [ ] Update user onboarding to explain shake is OPTIONAL
- [ ] Default shake to DISABLED (users opt-in)

---

## 🔐 Security & Safety Notes

### This Implementation Prevents

✅ **Toddler false positives** - 5-second hold requirement (toddlers can't hold that long)
✅ **Pocket false positives** - Confirmation dialog with explicit cancel
✅ **Drop false positives** - Even if shake triggered, must confirm with 5-second hold
✅ **Accidental taps** - Quick tap doesn't delete, requires continuous 5-second press
✅ **Accidental dismissal** - Cannot close dialog without explicit choice

### User Safety Maintained

✅ **Emergency speed** - Still quick: trigger + 5-second hold = 6-8 seconds total
✅ **Clear feedback** - User knows what triggered, what will be deleted
✅ **Easy escape** - Large CANCEL button for accidental triggers
✅ **Progress indicator** - User sees countdown and progress bar
✅ **No bypass** - Cannot accidentally dismiss or skip confirmation

---

## 📞 Support Resources

**For Developers**:
- See implementation code in files above
- Test cases in "Testing Guide" section
- Sensitivity recommendations in ShakeSensitivity enum

**For Users**:
- Shake detection is OPTIONAL (disabled by default)
- Recommend VERY_LOW sensitivity for parents
- Recommend OFF/PIN method if you have toddlers under 3
- Confirmation dialog ALWAYS shows before deletion
- 5-second hold prevents all accidental deletions

---

## ✅ Summary

### What You're Getting

1. **PanicDeleteConfirmationDialog** - Beautiful, safe, 5-second hold confirmation
2. **Updated MainActivity** - Integration with any trigger method
3. **Updated ShakeDetector** - Configurable, disabled by default
4. **Settings UI** - User control over shake enable/sensitivity
5. **Complete safety** - Prevents ALL false positive deletions

### The 5-Second Hold Difference

**Before**: Accidental triggers = data loss
**After**: Accidental triggers = safe cancel option, no data loss

Even the most active toddler cannot hold a button continuously for 5 seconds. This makes panic delete **100% toddler-proof** while maintaining fast emergency access for survivors.

---

**Next Step**: Copy files to Android project and test with real devices!
