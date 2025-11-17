package app.neurothrive.safehaven.domain.usecases

import app.neurothrive.safehaven.data.database.entities.HealthcareJourney
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Update Healthcare Journey Use Case
 *
 * RESPONSIBILITIES:
 * - Update journey status (planning → traveling → completed)
 * - Mark arrangements as completed
 * - Update lastUpdated timestamp
 * - Handle journey completion with auto-delete scheduling
 * - Handle journey cancellation
 *
 * PRIVACY NOTE:
 * - Preserves existing encrypted fields
 * - Updates only specified fields
 * - Maintains encryption for sensitive data
 */
class UpdateHealthcareJourneyUseCase @Inject constructor(
    private val repository: SafeHavenRepository
) {

    /**
     * Update journey status
     *
     * Valid statuses:
     * - planning: Initial planning phase
     * - confirmed: All arrangements confirmed
     * - traveling_outbound: En route to appointment
     * - at_appointment: At medical appointment
     * - recovering: In recovery period
     * - traveling_return: Returning home
     * - completed: Journey successfully completed
     * - cancelled: Journey cancelled
     */
    suspend fun updateStatus(
        journeyId: String,
        newStatus: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            repository.updateHealthcareJourneyStatus(
                journeyId = journeyId,
                newStatus = newStatus,
                timestamp = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark childcare as arranged
     */
    suspend fun markChildcareArranged(
        journeyId: String,
        childcareResourceId: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val updated = journey.copy(
                childcareArranged = true,
                childcareResourceId = childcareResourceId,
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark outbound transportation as arranged
     */
    suspend fun markOutboundTransportArranged(
        journeyId: String,
        transportResourceId: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val updated = journey.copy(
                outboundTransportationArranged = true,
                outboundTransportationResourceId = transportResourceId,
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark recovery housing as arranged
     */
    suspend fun markRecoveryHousingArranged(
        journeyId: String,
        housingResourceId: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val updated = journey.copy(
                recoveryHousingArranged = true,
                recoveryHousingResourceId = housingResourceId,
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark return transportation as arranged
     */
    suspend fun markReturnTransportArranged(
        journeyId: String,
        transportResourceId: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val updated = journey.copy(
                returnTransportationArranged = true,
                returnTransportationResourceId = transportResourceId,
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark financial assistance as arranged
     */
    suspend fun markFinancialAssistanceArranged(
        journeyId: String,
        assistanceResourceIds: List<String>?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val updated = journey.copy(
                financialAssistanceArranged = true,
                financialAssistanceResourceIds = assistanceResourceIds?.joinToString(","),
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark accompaniment as arranged
     */
    suspend fun markAccompanimentArranged(
        journeyId: String,
        accompanimentResourceId: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val updated = journey.copy(
                accompanimentArranged = true,
                accompanimentResourceId = accompanimentResourceId,
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Complete journey
     *
     * If auto-delete is enabled, schedules deletion for 30 days from completion
     */
    suspend fun completeJourney(
        journeyId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentTime = System.currentTimeMillis()
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            // Calculate auto-delete date if enabled
            val autoDeleteDate = if (journey.autoDeleteAfterCompletion) {
                currentTime + (30L * 24 * 60 * 60 * 1000) // 30 days
            } else {
                null
            }

            repository.markHealthcareJourneyCompleted(
                journeyId = journeyId,
                completedTimestamp = currentTime,
                autoDeleteDate = autoDeleteDate
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancel journey
     */
    suspend fun cancelJourney(
        journeyId: String,
        reason: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            repository.cancelHealthcareJourney(
                journeyId = journeyId,
                reason = reason,
                timestamp = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
