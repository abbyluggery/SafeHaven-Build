package app.neurothrive.safehaven.domain.usecases

import app.neurothrive.safehaven.data.database.entities.HealthcareJourney
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import app.neurothrive.safehaven.util.crypto.SafeHavenCrypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Create Healthcare Journey Use Case
 *
 * CRITICAL RESPONSIBILITY:
 * - Create new healthcare journey with proper encryption
 * - Encrypt all sensitive fields (dates, locations, notes)
 * - Set default privacy settings
 * - Calculate auto-delete date if enabled
 *
 * PRIVACY PROTECTIONS:
 * - All dates, locations, and notes are AES-256 encrypted before storage
 * - Default settings prioritize survivor safety
 * - Auto-delete enabled by default (30 days after completion)
 * - GPS tracking disabled by default
 */
class CreateHealthcareJourneyUseCase @Inject constructor(
    private val repository: SafeHavenRepository
) {

    /**
     * Create a new healthcare journey
     *
     * @param userId User ID
     * @param clinicResourceId ID of the selected clinic
     * @param appointmentDate Appointment date (will be encrypted)
     * @param appointmentTime Appointment time (will be encrypted)
     * @param servicesNeeded List of services (will be encrypted as JSON)
     * @param appointmentNotes Private notes (will be encrypted)
     * @param needsChildcare Whether childcare is needed
     * @param numberOfChildren Number of children needing care
     * @param childAges Ages of children (will be encrypted)
     * @param childcareType Type of childcare needed
     * @param childcareDuration Duration of childcare
     * @param departureLocation Where traveling from (will be encrypted)
     * @param departureDateStr Departure date (will be encrypted)
     * @param outboundTransportType Type of transportation
     * @param needsRecoveryHousing Whether recovery housing is needed
     * @param recoveryDuration Duration of recovery
     * @param returnTransportType Return transportation type
     * @param needsFinancialAssistance Whether financial assistance is needed
     * @param needsAccompaniment Whether accompaniment is needed
     * @param autoDeleteAfterCompletion Enable auto-delete
     * @param useStealthMode Enable stealth mode
     *
     * @return Result with created HealthcareJourney or error
     */
    suspend fun execute(
        userId: String,
        clinicResourceId: String?,
        appointmentDate: String?,
        appointmentTime: String?,
        servicesNeeded: List<String>,
        appointmentNotes: String?,
        needsChildcare: Boolean,
        numberOfChildren: Int,
        childAges: List<Int>,
        childcareType: String?,
        childcareDuration: String?,
        departureLocation: String?,
        departureDateStr: String?,
        outboundTransportType: String?,
        needsRecoveryHousing: Boolean,
        recoveryDuration: String?,
        returnTransportType: String?,
        needsFinancialAssistance: Boolean,
        needsAccompaniment: Boolean,
        autoDeleteAfterCompletion: Boolean = true,
        useStealthMode: Boolean = false
    ): Result<HealthcareJourney> = withContext(Dispatchers.IO) {
        try {
            val currentTime = System.currentTimeMillis()
            val journeyId = "HJ_${UUID.randomUUID()}"

            // Encrypt sensitive fields
            val appointmentDateEncrypted = appointmentDate?.let {
                SafeHavenCrypto.encrypt(it)
            }

            val appointmentTimeEncrypted = appointmentTime?.let {
                SafeHavenCrypto.encrypt(it)
            }

            val servicesNeededEncrypted = if (servicesNeeded.isNotEmpty()) {
                SafeHavenCrypto.encrypt(servicesNeeded.joinToString(","))
            } else {
                null
            }

            val appointmentNotesEncrypted = appointmentNotes?.let {
                if (it.isNotBlank()) SafeHavenCrypto.encrypt(it) else null
            }

            val childAgesEncrypted = if (childAges.isNotEmpty()) {
                SafeHavenCrypto.encrypt(childAges.joinToString(","))
            } else {
                null
            }

            val childcareNotesEncrypted: String? = null // Can be set later

            val departureLocationEncrypted = departureLocation?.let {
                if (it.isNotBlank()) SafeHavenCrypto.encrypt(it) else null
            }

            val departureDateEncrypted = departureDateStr?.let {
                if (it.isNotBlank()) SafeHavenCrypto.encrypt(it) else null
            }

            // Calculate auto-delete date (30 days after current time if enabled)
            val autoDeleteDate = if (autoDeleteAfterCompletion) {
                currentTime + (30L * 24 * 60 * 60 * 1000) // 30 days in milliseconds
            } else {
                null
            }

            // Create journey entity
            val journey = HealthcareJourney(
                id = journeyId,
                userId = userId,
                journeyStatus = "planning",
                createdAt = currentTime,
                lastUpdated = currentTime,

                // Appointment details (encrypted)
                clinicResourceId = clinicResourceId,
                appointmentDateEncrypted = appointmentDateEncrypted,
                appointmentTimeEncrypted = appointmentTimeEncrypted,
                servicesNeededEncrypted = servicesNeededEncrypted,
                appointmentNotesEncrypted = appointmentNotesEncrypted,

                // Childcare
                needsChildcare = needsChildcare,
                numberOfChildren = numberOfChildren,
                childAgesEncrypted = childAgesEncrypted,
                childcareType = childcareType,
                childcareDuration = childcareDuration,
                childcareResourceId = null,
                childcareArranged = false,
                childcareNotesEncrypted = childcareNotesEncrypted,

                // Outbound travel
                departureLocationEncrypted = departureLocationEncrypted,
                departureDateEncrypted = departureDateEncrypted,
                outboundTransportationType = outboundTransportType,
                outboundTransportationResourceId = null,
                outboundTransportationArranged = false,
                outboundTransportationNotesEncrypted = null,

                // Recovery
                needsRecoveryHousing = needsRecoveryHousing,
                recoveryHousingResourceId = null,
                recoveryDuration = recoveryDuration,
                recoveryHousingArranged = false,
                recoveryHousingNotesEncrypted = null,

                // Accompaniment
                needsAccompaniment = needsAccompaniment,
                accompanimentResourceId = null,
                accompanimentArranged = false,
                accompanimentNotesEncrypted = null,

                // Return travel
                returnDateEncrypted = null,
                returnTransportationType = returnTransportType,
                returnTransportationResourceId = null,
                returnTransportationArranged = false,
                returnTransportationNotesEncrypted = null,

                // Financial assistance
                needsFinancialAssistance = needsFinancialAssistance,
                financialNeedsEncrypted = null,
                financialAssistanceResourceIds = null,
                financialAssistanceArranged = false,
                financialAssistanceNotesEncrypted = null,

                // Privacy settings
                autoDeleteAfterCompletion = autoDeleteAfterCompletion,
                autoDeleteDate = autoDeleteDate,
                disableLocationTracking = true, // Always true by default for safety
                useStealthMode = useStealthMode,

                // Completion
                completedAt = null,
                cancellationReason = null,
                journeyNotesEncrypted = null
            )

            // Save to database
            repository.saveHealthcareJourney(journey)

            Result.success(journey)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
