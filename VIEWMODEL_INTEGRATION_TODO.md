# ViewModel Integration - Remaining Tasks

**Status**: ViewModels created ✅ | Integration pending ⚠️ | Navigation complete ✅

---

## Completed Work ✅

### 1. Created 7 New ViewModels
- ✅ `EvidenceVaultViewModel.kt` - Manages encrypted evidence items
- ✅ `IncidentReportViewModel.kt` - Handles incident documentation
- ✅ `ResourceFinderViewModel.kt` - Intersectional resource matching
- ✅ `DocumentVerificationViewModel.kt` - Cryptographic verification
- ✅ `HomeViewModel.kt` - Dashboard statistics
- ✅ `SettingsViewModel.kt` - User preferences & panic delete
- ✅ `LoginViewModel.kt` - Dual password authentication

### 2. Existing Healthcare ViewModels (Already Created)
- ✅ `HealthcareJourneyPlannerViewModel.kt`
- ✅ `HealthcareJourneyDetailViewModel.kt`
- ✅ `HealthcareResourceFinderViewModel.kt`

### 3. Navigation Updates
- ✅ Added healthcare journey routes to `NavGraph.kt`
- ✅ Added `Screen.HealthcareResourceFinder`
- ✅ Added `Screen.HealthcareJourneyPlanner`
- ✅ Added `Screen.HealthcareJourneyDetail`
- ✅ Updated `HomeScreen` with healthcare navigation button
- ✅ Connected all healthcare screens to navigation graph

---

## Remaining Integration Tasks ⚠️

### Step 1: Update UI Screens to Use ViewModels

Each screen needs to be updated to use Hilt for ViewModel injection. Here's the pattern:

#### Example: EvidenceVaultScreen.kt

**Current state:**
```kotlin
@Composable
fun EvidenceVaultScreen(
    onBack: () -> Unit,
    onCaptureNew: () -> Unit
) {
    // Static UI
}
```

**Needs to become:**
```kotlin
@Composable
fun EvidenceVaultScreen(
    viewModel: EvidenceVaultViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onCaptureNew: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val evidenceItems by viewModel.evidenceItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEvidence(userId = "current_user") // TODO: Get real userId
    }

    // Use uiState and evidenceItems in UI
}
```

#### Screens Needing Integration:

1. **EvidenceVaultScreen.kt**
   - Inject `EvidenceVaultViewModel`
   - Display evidence items from ViewModel
   - Handle delete actions
   - Show loading/error states

2. **IncidentReportScreen.kt**
   - Inject `IncidentReportViewModel`
   - Bind form fields to ViewModel draft state
   - Call `saveIncidentReport()` on submit
   - Navigate back on success

3. **DocumentVerificationScreen.kt**
   - Inject `DocumentVerificationViewModel`
   - Display verified documents list
   - Handle new document verification
   - Show verification progress

4. **ResourceFinderScreen.kt**
   - Inject `ResourceFinderViewModel`
   - Display scored resources
   - Handle resource type selection
   - Show intersectional matching results

5. **HomeScreen.kt**
   - Inject `HomeViewModel`
   - Display dashboard statistics
   - Show active journey count
   - Display last incident date

6. **SettingsScreen.kt**
   - Inject `SettingsViewModel`
   - Bind settings to ViewModel
   - Handle panic delete confirmation
   - Update profile settings

7. **LoginScreen.kt**
   - Inject `LoginViewModel`
   - Handle authentication
   - Detect duress password
   - Navigate on success

8. **ProfileSetupScreen.kt**
   - Update to use `LoginViewModel.createProfile()`
   - Collect intersectional identity data
   - Save survivor profile

---

## Step 2: User ID Management

Currently, screens need a `userId` parameter. Options:

### Option A: Pass userId through navigation (Quick)
```kotlin
composable(Screen.Home.route + "/{userId}") { backStackEntry ->
    val userId = backStackEntry.arguments?.getString("userId") ?: ""
    HomeScreen(userId = userId, ...)
}
```

### Option B: Use DataStore for current user (Recommended)
```kotlin
// In SafeHavenApplication or AuthManager
class UserSession @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val currentUserId: Flow<String?> = dataStore.data.map { it[USER_ID_KEY] }

    suspend fun setCurrentUser(userId: String) {
        dataStore.edit { it[USER_ID_KEY] = userId }
    }
}
```

Then inject `UserSession` into ViewModels:
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SafeHavenRepository,
    private val userSession: UserSession
) : ViewModel() {

    init {
        viewModelScope.launch {
            userSession.currentUserId.collect { userId ->
                userId?.let { loadDashboard(it) }
            }
        }
    }
}
```

---

## Step 3: Testing Integration

### Manual Testing Checklist:

1. **Login Flow**
   - [ ] Create new profile with dual passwords
   - [ ] Login with real password → full access
   - [ ] Login with duress password → decoy mode

2. **Evidence Vault**
   - [ ] Take photo with silent camera
   - [ ] Photo appears encrypted in vault
   - [ ] Can view photo (decrypts correctly)
   - [ ] Can delete photo

3. **Incident Reports**
   - [ ] Create new incident report
   - [ ] Description is encrypted
   - [ ] Can view list of reports
   - [ ] Can link evidence to report

4. **Document Verification**
   - [ ] Photograph document
   - [ ] SHA-256 hash generated
   - [ ] Verified PDF created
   - [ ] Both files encrypted

5. **Resource Finder**
   - [ ] Search for shelters
   - [ ] Trans survivor sees trans-specific resources first
   - [ ] Distance calculation works
   - [ ] Can view resource details

6. **Healthcare Journey**
   - [ ] Navigate to healthcare from home
   - [ ] Find reproductive healthcare clinic
   - [ ] Plan journey with all 7 steps
   - [ ] Save journey successfully
   - [ ] View journey details

7. **Settings**
   - [ ] Toggle GPS (should be OFF by default)
   - [ ] Toggle stealth mode
   - [ ] Configure auto-delete
   - [ ] Test panic delete (⚠️ destructive!)

---

## Step 4: Polish & Refinements

### UI Improvements:
- [ ] Add loading spinners for async operations
- [ ] Add error dialogs with retry actions
- [ ] Add empty state screens (no evidence, no reports, etc.)
- [ ] Add confirmation dialogs for destructive actions
- [ ] Improve accessibility (content descriptions, focus order)

### Performance:
- [ ] Lazy load evidence thumbnails
- [ ] Paginate large resource lists
- [ ] Cache decrypted images temporarily
- [ ] Optimize database queries

### Security:
- [ ] Verify GPS is OFF by default
- [ ] Test panic delete completes in <2 seconds
- [ ] Verify all sensitive fields are encrypted
- [ ] Test duress password shows decoy data
- [ ] Verify no data leaks to system logs

---

## Quick Start: Integrate One Screen

**To test the integration pattern, start with EvidenceVaultScreen:**

1. Open `app/src/main/java/app/neurothrive/safehaven/ui/screens/EvidenceVaultScreen.kt`

2. Add import:
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import app.neurothrive.safehaven.ui.viewmodels.EvidenceVaultViewModel
```

3. Update composable signature:
```kotlin
@Composable
fun EvidenceVaultScreen(
    viewModel: EvidenceVaultViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onCaptureNew: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val evidenceItems by viewModel.evidenceItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEvidence("test_user_id") // TODO: Get real userId
    }

    // Rest of UI code...
}
```

4. Test that Hilt injection works and data flows correctly

---

## Architecture Summary

```
UI Screens (Compose)
    ↓ (Hilt inject)
ViewModels
    ↓ (calls)
Repository
    ↓ (queries)
DAOs
    ↓ (Room)
SQLite Database (encrypted)
```

**Key Points:**
- ViewModels hold UI state (MutableStateFlow)
- Repository handles encryption/decryption transparently
- UI screens collect state as Compose State
- Navigation handled by NavGraph

---

## Status: Ready for Integration ✅

All ViewModels are created and ready. The architecture is sound. Integration is straightforward:

1. Add `hiltViewModel()` to each screen
2. Collect StateFlows as Compose State
3. Call ViewModel methods on user actions
4. Handle loading/error states in UI

**Estimated time to complete integration:** 2-3 hours

---

**Last Updated:** November 17, 2025
**Created by:** Claude Code (SafeHaven Build)
