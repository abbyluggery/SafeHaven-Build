package app.neurothrive.safehaven.domain.usecases

import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import app.neurothrive.safehaven.util.crypto.SafeHavenCrypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Healthcare Journey Auto-Delete Use Case
 *
 * CRITICAL PRIVACY FEATURE:
 * - Automatically delete completed journeys after specified time (default: 30 days)
 * - Securely delete all journey data
 * - Run periodically (e.g., daily check)
 * - Minimize data retention for survivor safety
 *
 * SECURITY:
 * - Journeys are only deleted if:
 *   1. autoDeleteAfterCompletion is enabled
 *   2. Journey status is "completed"
 *   3. Current time >= autoDeleteDate
 * - Secure deletion of all related data
 *
 * USAGE:
 * - Should be called periodically via WorkManager or similar
 * - Recommended: Run daily at off-peak hours
 * - Can also be manually triggered by user
 */
class HealthcareJourneyAutoDeleteUseCase @Inject constructor(
    private val repository: SafeHavenRepository
) {

    /**
     * Check and delete expired journeys
     *
     * @return Result with number of journeys deleted
     */
    suspend fun execute(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val currentTimestamp = System.currentTimeMillis()

            // Get all journeys eligible for auto-deletion
            val journeysToDelete = repository.getHealthcareJourneysForAutoDeletion(currentTimestamp)

            Timber.d("Auto-delete check: Found ${journeysToDelete.size} journeys to delete")

            var deletedCount = 0

            journeysToDelete.forEach { journey ->
                try {
                    // Verify journey should be deleted (safety check)
                    if (shouldDeleteJourney(journey.journeyStatus, journey.autoDeleteAfterCompletion,
                        journey.autoDeleteDate, currentTimestamp)) {

                        // Delete the journey
                        repository.deleteHealthcareJourney(journey.id)

                        deletedCount++

                        Timber.d("Auto-deleted journey ${journey.id} (completed at ${journey.completedAt})")
                    } else {
                        Timber.w("Journey ${journey.id} was marked for deletion but doesn't meet criteria")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to auto-delete journey ${journey.id}")
                    // Continue with other journeys even if one fails
                }
            }

            Result.success(deletedCount)

        } catch (e: Exception) {
            Timber.e(e, "Failed to execute auto-delete check")
            Result.failure(e)
        }
    }

    /**
     * Get count of journeys pending auto-deletion
     *
     * Useful for showing user how many journeys will be deleted soon
     */
    suspend fun getPendingDeletionCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val currentTimestamp = System.currentTimeMillis()
            val journeys = repository.getHealthcareJourneysForAutoDeletion(currentTimestamp)
            Result.success(journeys.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get journeys that will be auto-deleted within next N days
     *
     * @param days Number of days to look ahead
     * @return List of journey IDs that will be deleted
     */
    suspend fun getJourneysExpiringWithinDays(days: Int): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val currentTimestamp = System.currentTimeMillis()
            val futureTimestamp = currentTimestamp + (days.toLong() * 24 * 60 * 60 * 1000)

            val journeys = repository.getHealthcareJourneysForAutoDeletion(futureTimestamp)
            val journeyIds = journeys.map { it.id }

            Result.success(journeyIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancel auto-delete for a specific journey
     *
     * Allows user to keep journey beyond auto-delete date
     */
    suspend fun cancelAutoDelete(journeyId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val updated = journey.copy(
                autoDeleteAfterCompletion = false,
                autoDeleteDate = null,
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)

            Timber.d("Cancelled auto-delete for journey $journeyId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel auto-delete for journey $journeyId")
            Result.failure(e)
        }
    }

    /**
     * Enable auto-delete for a journey (if previously disabled)
     *
     * @param journeyId Journey ID
     * @param daysUntilDeletion Number of days from now until deletion
     */
    suspend fun enableAutoDelete(
        journeyId: String,
        daysUntilDeletion: Int = 30
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val journey = repository.getHealthcareJourneyById(journeyId)
                ?: return@withContext Result.failure(Exception("Journey not found"))

            val autoDeleteDate = System.currentTimeMillis() +
                (daysUntilDeletion.toLong() * 24 * 60 * 60 * 1000)

            val updated = journey.copy(
                autoDeleteAfterCompletion = true,
                autoDeleteDate = autoDeleteDate,
                lastUpdated = System.currentTimeMillis()
            )

            repository.updateHealthcareJourney(updated)

            Timber.d("Enabled auto-delete for journey $journeyId (will delete in $daysUntilDeletion days)")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to enable auto-delete for journey $journeyId")
            Result.failure(e)
        }
    }

    /**
     * Verify journey meets criteria for deletion
     */
    private fun shouldDeleteJourney(
        status: String,
        autoDeleteEnabled: Boolean,
        autoDeleteDate: Long?,
        currentTimestamp: Long
    ): Boolean {
        // Journey must be completed
        if (status != "completed") {
            return false
        }

        // Auto-delete must be enabled
        if (!autoDeleteEnabled) {
            return false
        }

        // Auto-delete date must be set and in the past
        if (autoDeleteDate == null || autoDeleteDate > currentTimestamp) {
            return false
        }

        return true
    }
}
