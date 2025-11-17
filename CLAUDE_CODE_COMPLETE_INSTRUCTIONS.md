# Claude Code: Complete Build Instructions for SafeHaven Android App

**Repository**: https://github.com/abbyluggery/SafeHaven-Build
**Target**: Build complete SafeHaven Android app in 24-hour sprint
**Platform**: Android (Kotlin + Jetpack Compose)
**Database**: Room (SQLite with AES-256 encryption)

---

## CRITICAL: Read This First

### What You're Building
A **domestic violence safety planning app** with:
1. ✅ Silent camera (no sound/flash/GPS)
2. ✅ Encrypted evidence vault (AES-256)
3. ✅ Document verification (SHA-256 + blockchain)
4. ✅ Panic delete (shake to wipe <2 seconds)
5. ✅ Intersectional resource matching (1,000+ orgs)
6. ✅ Legal-formatted incident reports

### Why This Matters
- **70% of survivors** can't leave due to economic dependence
- **Trans BIPOC women** have highest IPV rates, fewest resources
- **Male survivors** have no shelters, stigma barriers
- **Undocumented survivors** fear deportation, avoid police

### Your Mission
Build a production-ready Android app that centers marginalized survivors and enables safe documentation + economic independence.

---

## 📋 BUILD PRIORITY (24-Hour Sprint)

### ⚠️ FOLLOW THIS ORDER EXACTLY ⚠️

## PHASE 1: CRITICAL (Hours 0-12)

### Hour 0-2: Database Schema + Encryption Setup

**Priority**: 🔴 CRITICAL - Everything depends on this

**Task 1.1: Create Room Database Entities**

Create these 6 entities in `app/src/main/java/app/neurothrive/safehaven/data/database/entities/`:

1. **SafeHavenProfile.kt** - User settings, intersectional identity
2. **IncidentReport.kt** - Abuse documentation with encryption
3. **VerifiedDocument.kt** - SHA-256 hashes, blockchain timestamps
4. **EvidenceItem.kt** - Encrypted photos/videos/audio
5. **LegalResource.kt** - 1,000+ shelters with intersectional filters
6. **SurvivorProfile.kt** - Intersectional identity for resource matching

**Complete code for all entities is in**: [# SafeHaven Database Schema (Room).md](# SafeHaven Database Schema (Room).md)

**IMPORTANT FIELDS TO INCLUDE**:

**SafeHavenProfile**:
```kotlin
- safeHavenPasswordHash: String
- duressPasswordHash: String (CRITICAL for dual-password panic feature)
- gpsEnabled: Boolean = false (DEFAULT OFF for safety)
- isLGBTQIA, isTrans, isNonBinary, isBIPOC, isMaleIdentifying, isUndocumented, isDisabled, isDeaf
```

**IncidentReport**:
```kotlin
- descriptionEncrypted: String (AES-256 encrypted)
- witnessesEncrypted: String? (AES-256 encrypted)
- latitude/longitude: Double? (only if user enabled GPS)
- policeInvolved, policeReportNumber, medicalAttention
```

**VerifiedDocument**:
```kotlin
- cryptographicHash: String (SHA-256, 64 chars)
- blockchainTxHash: String? (Polygon transaction hash)
- verificationMethod: String = "SHA256_Blockchain"
```

**LegalResource** (CRITICAL for intersectional matching):
```kotlin
- servesLGBTQIA, lgbtqSpecialized, transInclusive, nonBinaryInclusive
- servesBIPOC, bipocLed
- servesMaleIdentifying
- servesUndocumented, uVisaSupport, vawaSupport, noICEContact
- servesDisabled, wheelchairAccessible, servesDeaf, aslInterpreter
```

**Task 1.2: Create DAOs**

Create 6 DAOs in `app/src/main/java/app/neurothrive/safehaven/data/database/dao/`:

```kotlin
@Dao
interface IncidentReportDao {
    @Query("SELECT * FROM incident_reports WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllFlow(userId: String): Flow<List<IncidentReport>>

    @Query("SELECT * FROM incident_reports WHERE syncedToSalesforce = 0")
    suspend fun getUnsynced(): List<IncidentReport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: IncidentReport)

    @Update
    suspend fun update(report: IncidentReport)

    @Delete
    suspend fun delete(report: IncidentReport)

    @Query("DELETE FROM incident_reports WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
```

**Repeat for all 6 entities**.

**Task 1.3: Create AppDatabase**

`app/src/main/java/app/neurothrive/safehaven/data/database/AppDatabase.kt`:

```kotlin
@Database(
    entities = [
        SafeHavenProfile::class,
        IncidentReport::class,
        VerifiedDocument::class,
        EvidenceItem::class,
        LegalResource::class,
        SurvivorProfile::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun safeHavenProfileDao(): SafeHavenProfileDao
    abstract fun incidentReportDao(): IncidentReportDao
    abstract fun verifiedDocumentDao(): VerifiedDocumentDao
    abstract fun evidenceItemDao(): EvidenceItemDao
    abstract fun legalResourceDao(): LegalResourceDao
    abstract fun survivorProfileDao(): SurvivorProfileDao

    companion object {
        const val DATABASE_NAME = "safehaven_db"
    }
}
```

**Task 1.4: Setup Encryption**

Create `app/src/main/java/app/neurothrive/safehaven/util/crypto/SafeHavenCrypto.kt`:

**COMPLETE CODE IS IN**: [SafeHaven Technical Specification.MD](SafeHaven Technical Specification.MD) (search for "SafeHavenCrypto.kt")

**Must include**:
- `initializeKey()` - Generate AES-256 key in Android KeyStore
- `encrypt(plaintext: String): String` - AES-256-GCM encryption
- `decrypt(encrypted: String): String` - Decryption
- `encryptFile(inputFile, outputFile)` - For photos/videos
- `decryptFile(inputFile, outputFile)` - For viewing evidence
- `generateSHA256(file): String` - For document verification
- `secureDelete(file)` - Overwrite with random data before deleting

**SUCCESS CRITERIA FOR HOURS 0-2**:
- ✅ 6 entities created
- ✅ 6 DAOs created
- ✅ AppDatabase compiles
- ✅ SafeHavenCrypto.kt compiles
- ✅ Encryption test passes (encrypt → decrypt = original)

---

### Hours 2-4: Dependency Injection + Repository Layer

**Task 2.1: Setup Hilt**

`SafeHavenApplication.kt`:
```kotlin
@HiltAndroidApp
class SafeHavenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SafeHavenCrypto.initializeKey() // CRITICAL
    }
}
```

**Task 2.2: Create DI Modules**

`di/DatabaseModule.kt`:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // MVP only, remove for production
        .build()
    }

    // Provide all 6 DAOs
}
```

**Task 2.3: Create Repository**

`data/repository/SafeHavenRepository.kt`:
```kotlin
@Singleton
class SafeHavenRepository @Inject constructor(
    private val incidentReportDao: IncidentReportDao,
    private val evidenceItemDao: EvidenceItemDao,
    private val verifiedDocumentDao: VerifiedDocumentDao,
    // ... other DAOs
) {
    // Incident Reports
    fun getAllIncidents(userId: String) = incidentReportDao.getAllFlow(userId)

    suspend fun saveIncident(report: IncidentReport) {
        // Encrypt sensitive fields before saving
        val encrypted = report.copy(
            descriptionEncrypted = SafeHavenCrypto.encrypt(report.descriptionEncrypted)
        )
        incidentReportDao.insert(encrypted)
    }

    // ... other methods
}
```

**SUCCESS CRITERIA FOR HOURS 2-4**:
- ✅ Hilt compiles
- ✅ Database can be injected
- ✅ Repository layer works
- ✅ Can insert/retrieve encrypted data

---

### Hours 4-8: Silent Camera System (MOST CRITICAL FEATURE)

**Priority**: 🔴 ULTRA-CRITICAL - This is THE core differentiator

**Task 3.1: Create SilentCameraManager**

`util/camera/SilentCameraManager.kt`:

```kotlin
class SilentCameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private var imageCapture: ImageCapture? = null
    private val audioManager = context.getSystemService(AudioManager::class.java)

    fun initialize(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            // Image capture with NO FLASH DEFAULT
            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF) // CRITICAL
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Bind to back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(context))
    }

    suspend fun capturePhotoSilently(
        userId: String,
        incidentId: String?
    ): Result<EvidenceItem> = withContext(Dispatchers.IO) {
        try {
            // STEP 1: MUTE SYSTEM VOLUME
            val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)

            // STEP 2: Create temp file
            val photoFile = File.createTempFile("evidence_", ".jpg", context.cacheDir)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            // STEP 3: Capture photo
            val result = suspendCoroutine<ImageCapture.OutputFileResults> { continuation ->
                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            continuation.resume(output)
                        }
                        override fun onError(exception: ImageCaptureException) {
                            continuation.resumeWithException(exception)
                        }
                    }
                )
            }

            // STEP 4: RESTORE VOLUME
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, originalVolume, 0)

            // STEP 5: Strip GPS metadata
            MetadataStripper.removeGPS(photoFile)

            // STEP 6: Encrypt file
            val encryptedFile = File(context.filesDir, "evidence_${UUID.randomUUID()}.enc")
            SafeHavenCrypto.encryptFile(photoFile, encryptedFile)

            // STEP 7: Delete temp file
            photoFile.delete()

            // STEP 8: Create EvidenceItem
            val evidenceItem = EvidenceItem(
                userId = userId,
                evidenceType = "photo",
                timestamp = System.currentTimeMillis(),
                encryptedFilePath = encryptedFile.absolutePath,
                originalFileName = "Photo_${System.currentTimeMillis()}.jpg",
                fileSize = encryptedFile.length(),
                mimeType = "image/jpeg",
                incidentReportId = incidentId
            )

            Result.success(evidenceItem)

        } catch (e: Exception) {
            // ALWAYS restore volume on error
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, originalVolume, 0)
            Result.failure(e)
        }
    }
}
```

**Task 3.2: Create MetadataStripper**

`util/camera/MetadataStripper.kt`:
```kotlin
object MetadataStripper {
    fun removeGPS(file: File) {
        try {
            val exif = ExifInterface(file.absolutePath)

            // Remove ALL location data
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, null)
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, null)
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, null)
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, null)
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, null)
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, null)
            exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, null)
            exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, null)

            exif.saveAttributes()
        } catch (e: Exception) {
            Timber.e(e, "Failed to strip GPS metadata")
        }
    }
}
```

**Task 3.3: Create SilentCameraScreen**

`ui/screens/documentation/SilentCameraScreen.kt`:
```kotlin
@Composable
fun SilentCameraScreen(
    userId: String,
    onPhotoSaved: (EvidenceItem) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { SilentCameraManager(context, lifecycleOwner) }
    val coroutineScope = rememberCoroutineScope()

    // Camera preview
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                cameraManager.initialize(this)
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    // Capture button (bottom center)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    val result = cameraManager.capturePhotoSilently(userId, null)
                    result.onSuccess { evidenceItem ->
                        onPhotoSaved(evidenceItem)
                    }
                }
            },
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(Icons.Default.Camera, "Capture photo")
        }
    }
}
```

**SUCCESS CRITERIA FOR HOURS 4-8**:
- ✅ Camera preview displays
- ✅ Photo captured with NO SOUND
- ✅ GPS metadata stripped
- ✅ File encrypted immediately
- ✅ EvidenceItem saved to database
- ✅ Temp file deleted

---

### Hours 8-10: Panic Delete System

**Priority**: 🔴 CRITICAL - Survivor safety

**Task 4.1: Create ShakeDetector**

`util/sensors/ShakeDetector.kt`:
```kotlin
class ShakeDetector(
    context: Context,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTime = 0L
    private val SHAKE_THRESHOLD = 20f // Adjust based on testing
    private val SHAKE_COOLDOWN = 2000L // 2 seconds between shakes

    fun start() {
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x*x + y*y + z*z) - SensorManager.GRAVITY_EARTH

        if (acceleration > SHAKE_THRESHOLD) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > SHAKE_COOLDOWN) {
                lastShakeTime = currentTime
                onShakeDetected()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
```

**Task 4.2: Create PanicDeleteUseCase**

`domain/usecases/PanicDeleteUseCase.kt`:
```kotlin
class PanicDeleteUseCase @Inject constructor(
    private val repository: SafeHavenRepository,
    private val context: Context
) {
    suspend fun execute(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // STEP 1: Get all evidence files
            val evidenceItems = repository.getAllEvidence(userId)

            // STEP 2: Securely delete all files
            evidenceItems.forEach { item ->
                val file = File(item.encryptedFilePath)
                if (file.exists()) {
                    SafeHavenCrypto.secureDelete(file) // Overwrite then delete
                }
            }

            // STEP 3: Delete database records
            repository.deleteAllIncidents(userId)
            repository.deleteAllEvidence(userId)
            repository.deleteAllDocuments(userId)
            repository.deleteProfile(userId)

            // STEP 4: Clear app cache
            context.cacheDir.deleteRecursively()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Task 4.3: Integrate Shake Detection**

In `MainActivity.kt`:
```kotlin
private lateinit var shakeDetector: ShakeDetector

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    shakeDetector = ShakeDetector(this) {
        // Show panic confirmation dialog
        showPanicConfirmationDialog()
    }
    shakeDetector.start()
}

private fun showPanicConfirmationDialog() {
    AlertDialog.Builder(this)
        .setTitle("Emergency Delete")
        .setMessage("Delete all SafeHaven data? This cannot be undone.")
        .setPositiveButton("DELETE ALL") { _, _ ->
            lifecycleScope.launch {
                panicDeleteUseCase.execute(currentUserId)
                // Exit app
                finishAffinity()
            }
        }
        .setNegativeButton("Cancel", null)
        .show()
}
```

**SUCCESS CRITERIA FOR HOURS 8-10**:
- ✅ Shake detector works
- ✅ Panic delete removes all evidence
- ✅ Files overwritten before deletion
- ✅ Database cleared
- ✅ Execution time <2 seconds

---

### Hours 10-12: Document Verification System

**Task 5.1: Create DocumentVerificationService**

`util/blockchain/DocumentVerificationService.kt`:
```kotlin
class DocumentVerificationService @Inject constructor(
    private val context: Context
) {
    suspend fun verifyDocument(photoFile: File): VerifiedDocument = withContext(Dispatchers.IO) {
        // STEP 1: Generate SHA-256 hash
        val hash = SafeHavenCrypto.generateSHA256(photoFile)

        // STEP 2: (Optional) Timestamp on Polygon blockchain
        val txHash = try {
            timestampOnBlockchain(hash)
        } catch (e: Exception) {
            null // Blockchain optional for MVP
        }

        // STEP 3: Create verified PDF
        val pdfFile = generateVerifiedPDF(photoFile, hash, txHash)

        // STEP 4: Encrypt both files
        val encryptedPhotoFile = File(context.filesDir, "doc_photo_${UUID.randomUUID()}.enc")
        val encryptedPDFFile = File(context.filesDir, "doc_pdf_${UUID.randomUUID()}.enc")

        SafeHavenCrypto.encryptFile(photoFile, encryptedPhotoFile)
        SafeHavenCrypto.encryptFile(pdfFile, encryptedPDFFile)

        // STEP 5: Create VerifiedDocument
        VerifiedDocument(
            userId = getCurrentUserId(),
            documentType = "unknown", // User can set later
            cryptographicHash = hash,
            blockchainTxHash = txHash,
            verificationMethod = "SHA256_Blockchain",
            notarizationDate = System.currentTimeMillis(),
            originalPhotoPathEncrypted = encryptedPhotoFile.absolutePath,
            verifiedPDFPathEncrypted = encryptedPDFFile.absolutePath
        )
    }

    private suspend fun timestampOnBlockchain(hash: String): String {
        // Use Web3j to call Polygon smart contract
        // Contract address: [TO BE DEPLOYED]
        // Function: storeHash(bytes32 hash)
        // Return transaction hash

        // MVP: Return mock hash (deploy blockchain in Phase 2)
        return "0x" + hash.take(64)
    }

    private fun generateVerifiedPDF(photoFile: File, hash: String, txHash: String?): File {
        val pdfFile = File(context.cacheDir, "verified_${System.currentTimeMillis()}.pdf")

        // Use iText7 to create PDF
        val writer = PdfWriter(pdfFile)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        // Add title
        document.add(Paragraph("Verified Document").setFontSize(20f).setBold())
        document.add(Paragraph("SafeHaven Document Verification").setFontSize(14f))
        document.add(Paragraph(""))

        // Add verification info
        document.add(Paragraph("Verification Date: ${Date()}"))
        document.add(Paragraph("SHA-256 Hash: $hash").setFontSize(10f))
        if (txHash != null) {
            document.add(Paragraph("Blockchain TX: $txHash").setFontSize(10f))
        }
        document.add(Paragraph(""))

        // Add photo
        val image = Image(ImageDataFactory.create(photoFile.absolutePath))
        image.setAutoScale(true)
        document.add(image)

        // Add QR code for hash verification
        // (Use ZXing library)

        document.close()

        return pdfFile
    }
}
```

**SUCCESS CRITERIA FOR HOURS 10-12**:
- ✅ SHA-256 hash generated correctly
- ✅ PDF created with embedded hash
- ✅ Both files encrypted
- ✅ VerifiedDocument saved to database

---

## PHASE 2: IMPORTANT (Hours 12-18)

### Hours 12-14: Intersectional Resource Matching

**Task 6.1: Create IntersectionalResourceMatcher**

`domain/usecases/IntersectionalResourceMatcher.kt`:
```kotlin
data class ScoredResource(
    val resource: LegalResource,
    val score: Double,
    val distance: Double
)

class IntersectionalResourceMatcher @Inject constructor(
    private val resourceDao: LegalResourceDao
) {
    suspend fun findResources(
        profile: SurvivorProfile,
        currentLatitude: Double?,
        currentLongitude: Double?,
        resourceType: String
    ): List<ScoredResource> = withContext(Dispatchers.IO) {

        // Get all resources of type
        val allResources = resourceDao.getByType(resourceType)

        // Score each resource
        val scored = allResources.map { resource ->
            val score = calculateScore(resource, profile, currentLatitude, currentLongitude)
            val distance = calculateDistance(resource, currentLatitude, currentLongitude)
            ScoredResource(resource, score, distance)
        }

        // Sort by score (highest first), then distance (closest first)
        scored.sortedWith(
            compareByDescending<ScoredResource> { it.score }
            .thenBy { it.distance }
        )
    }

    private fun calculateScore(
        resource: LegalResource,
        profile: SurvivorProfile,
        currentLat: Double?,
        currentLon: Double?
    ): Double {
        var score = 10.0 // Base score

        // INTERSECTIONAL BONUSES (CRITICAL SECTION)

        // Trans survivors get +30 for trans-specific resources
        if (profile.isTrans && resource.transInclusive) score += 30.0

        // Undocumented survivors get +30 for U-Visa support
        if (profile.isUndocumented && resource.servesUndocumented) {
            score += 30.0
            if (resource.uVisaSupport) score += 10.0
            if (resource.noICEContact) score += 10.0
        }

        // Male survivors get +25 (very few resources)
        if (profile.isMaleIdentifying && resource.servesMaleIdentifying) score += 25.0

        // LGBTQIA+ survivors get +20
        if (profile.isLGBTQIA && resource.servesLGBTQIA) {
            score += 20.0
            if (resource.lgbtqSpecialized) score += 10.0
        }

        // BIPOC survivors get +20 for BIPOC-led orgs
        if (profile.isBIPOC && resource.servesBIPOC) {
            score += 20.0
            if (resource.bipocLed) score += 10.0
        }

        // Disabled survivors
        if (profile.isDisabled && resource.servesDisabled) {
            score += 15.0
            if (resource.wheelchairAccessible) score += 5.0
        }

        // Deaf survivors
        if (profile.isDeaf && resource.servesDeaf) {
            score += 15.0
            if (resource.aslInterpreter) score += 10.0
        }

        // Distance bonus (if location provided)
        if (currentLat != null && currentLon != null &&
            resource.latitude != null && resource.longitude != null) {
            val distance = calculateDistance(resource, currentLat, currentLon)
            when {
                distance < 5.0 -> score += 10.0
                distance < 25.0 -> score += 5.0
                distance > 100.0 -> score -= 10.0
            }
        }

        // Free resources get bonus
        if (resource.isFree) score += 5.0

        // 24/7 availability gets bonus
        if (resource.is24_7) score += 5.0

        return score
    }

    private fun calculateDistance(
        resource: LegalResource,
        currentLat: Double?,
        currentLon: Double?
    ): Double {
        if (currentLat == null || currentLon == null ||
            resource.latitude == null || resource.longitude == null) {
            return Double.MAX_VALUE
        }

        // Haversine formula
        val R = 3959.0 // Earth radius in miles
        val dLat = Math.toRadians(resource.latitude - currentLat)
        val dLon = Math.toRadians(resource.longitude!! - currentLon)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(currentLat)) *
                cos(Math.toRadians(resource.latitude)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
```

**Task 6.2: Pre-populate Legal Resources**

Create `data/resources.csv` with 1,000+ legal resources (template in documentation).

Load on first app launch:
```kotlin
suspend fun loadResources() {
    val csvFile = context.assets.open("resources.csv")
    val reader = BufferedReader(InputStreamReader(csvFile))

    reader.forEachLine { line ->
        val fields = line.split(",")
        val resource = LegalResource(
            id = fields[0],
            organizationName = fields[1],
            // ... parse all fields
        )
        resourceDao.insert(resource)
    }
}
```

**SUCCESS CRITERIA FOR HOURS 12-14**:
- ✅ Resource matching algorithm works
- ✅ Trans survivor finds trans-specific shelters first
- ✅ Male survivor finds male-serving resources
- ✅ Distance calculation accurate

---

### Hours 14-16: Incident Report Form

**Task 7: Create IncidentReportScreen**

`ui/screens/documentation/IncidentReportScreen.kt`:
```kotlin
@Composable
fun IncidentReportScreen(
    userId: String,
    onSaved: () -> Unit
) {
    var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var incidentType by remember { mutableStateOf("physical") }
    var description by remember { mutableStateOf("") }
    var witnesses by remember { mutableStateOf("") }
    var policeInvolved by remember { mutableStateOf(false) }
    var policeReportNumber by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Date/Time Picker
        // Incident Type Dropdown (physical, verbal, emotional, financial, sexual, stalking)
        // Description TextField (will be encrypted)
        // Witnesses TextField (will be encrypted)
        // Police Involved Checkbox
        // Police Report Number TextField (if involved)
        // Medical Attention Checkbox

        // Save Button
        Button(onClick = {
            val report = IncidentReport(
                userId = userId,
                timestamp = timestamp,
                incidentType = incidentType,
                descriptionEncrypted = description, // Will encrypt in repository
                witnessesEncrypted = witnesses,
                policeInvolved = policeInvolved,
                policeReportNumber = policeReportNumber.takeIf { policeInvolved }
            )
            // Save via ViewModel/Repository
        }) {
            Text("Save Incident Report")
        }
    }
}
```

**SUCCESS CRITERIA FOR HOURS 14-16**:
- ✅ Form captures all fields
- ✅ Description encrypted before save
- ✅ Report saved to database
- ✅ Can export to PDF

---

### Hours 16-18: Evidence Vault Screen

**Task 8: Create EvidenceVaultScreen**

`ui/screens/documentation/EvidenceVaultScreen.kt`:
```kotlin
@Composable
fun EvidenceVaultScreen(
    userId: String,
    onPhotoClick: (EvidenceItem) -> Unit
) {
    val evidenceItems by viewModel.evidenceFlow.collectAsState(initial = emptyList())

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize()
    ) {
        items(evidenceItems) { item ->
            EvidenceThumbnail(
                item = item,
                onClick = { onPhotoClick(item) }
            )
        }
    }
}

@Composable
fun EvidenceThumbnail(item: EvidenceItem, onClick: () -> Unit) {
    // Decrypt and display thumbnail
    val decryptedFile = remember {
        val tempFile = File.createTempFile("thumb_", ".jpg")
        SafeHavenCrypto.decryptFile(File(item.encryptedFilePath), tempFile)
        tempFile
    }

    AsyncImage(
        model = decryptedFile,
        contentDescription = item.originalFileName,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    )
}
```

**SUCCESS CRITERIA FOR HOURS 16-18**:
- ✅ Evidence grid displays
- ✅ Photos decrypt on-demand
- ✅ Click to view full size
- ✅ Can link to incident reports

---

## PHASE 3: NICE-TO-HAVE (Hours 18-24)

### Hours 18-20: Onboarding Flow
### Hours 20-22: Settings Screen
### Hours 22-24: Salesforce Sync

(See [SafeHaven Quick Start Guide - 24.md](SafeHaven Quick Start Guide - 24.md) for details)

---

## 🔧 BUILD CONFIGURATION

### build.gradle.kts (Module: app)

**COPY THIS EXACTLY**:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "app.neurothrive.safehaven"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.neurothrive.safehaven"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Room (CRITICAL)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Encryption (CRITICAL)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // Camera (CRITICAL)
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // PDF
    implementation("com.itextpdf:itext7-core:7.2.5")

    // Image Processing
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // Blockchain (Polygon) - Optional
    implementation("org.web3j:core:4.9.8")

    // AWS S3 - Optional
    implementation("com.amazonaws:aws-android-sdk-s3:2.75.0")
}
```

---

## 🚨 CRITICAL REMINDERS

### Security
- ✅ GPS OFF by default
- ✅ Encrypt ALL sensitive fields
- ✅ No gallery thumbnails for silent photos
- ✅ Secure file deletion (overwrite before delete)
- ✅ Dual password system (real vs. decoy)

### Intersectionality
- ✅ Trans survivors prioritized for trans resources
- ✅ Male survivors see male-serving orgs
- ✅ Undocumented survivors see U-Visa support
- ✅ No ICE contact guaranteed for undocumented

### Performance
- ✅ Panic delete <2 seconds
- ✅ Silent camera <1 second capture
- ✅ Photos encrypted immediately

---

## 📊 SUCCESS CRITERIA (END OF 24 HOURS)

### Must Have ✅
- [x] Database schema complete (6 entities, 6 DAOs)
- [x] Encryption working (AES-256-GCM)
- [x] Silent camera functional (no sound/flash/GPS)
- [x] Panic delete working (<2 seconds)
- [x] Document verification (SHA-256 hash)
- [x] Intersectional resource matching algorithm

### Should Have ⚠️
- [x] Incident report form
- [x] Evidence vault screen
- [x] Resource finder UI

### Nice to Have 💡
- [ ] Onboarding flow
- [ ] Settings screen
- [ ] Salesforce sync
- [ ] Blockchain timestamping

---

## 📚 REFERENCE DOCUMENTATION

**Detailed specs**: [SafeHaven Technical Specification.MD](SafeHaven Technical Specification.MD)
**Database schema**: [# SafeHaven Database Schema (Room).md](# SafeHaven Database Schema (Room).md)
**24-hour timeline**: [SafeHaven Quick Start Guide - 24.md](SafeHaven Quick Start Guide - 24.md)
**Executive summary**: [SafeHaven Executive Summary.md](SafeHaven Executive Summary.md)

---

## ❓ QUESTIONS? BLOCKERS?

If you encounter issues:
1. Check [SafeHaven Technical Specification.MD](SafeHaven Technical Specification.MD) for complete code examples
2. Verify all dependencies in build.gradle.kts
3. Ensure Android KeyStore initialized before encryption
4. Test on physical device (emulator may not have camera/sensors)

---

**Repository**: https://github.com/abbyluggery/SafeHaven-Build
**Status**: Ready for 24-hour sprint
**Priority**: Follow this document exactly, in order

**Good luck building SafeHaven! You're helping save lives. 💜**