package app.neurothrive.safehaven.data.repository

import app.neurothrive.safehaven.data.database.dao.*
import app.neurothrive.safehaven.data.database.entities.*
import app.neurothrive.safehaven.util.crypto.SafeHavenCrypto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SafeHaven Repository
 * Central data layer for the app
 *
 * CRITICAL: Handles encryption/decryption transparently
 */
@Singleton
class SafeHavenRepository @Inject constructor(
    private val profileDao: SafeHavenProfileDao,
    private val incidentReportDao: IncidentReportDao,
    private val verifiedDocumentDao: VerifiedDocumentDao,
    private val evidenceItemDao: EvidenceItemDao,
    private val legalResourceDao: LegalResourceDao,
    private val survivorProfileDao: SurvivorProfileDao,
    private val healthcareJourneyDao: HealthcareJourneyDao
) {

    // ==================== SafeHaven Profile ====================

    suspend fun getProfile(userId: String): SafeHavenProfile? {
        return profileDao.getProfile(userId)
    }

    fun getProfileFlow(userId: String): Flow<SafeHavenProfile?> {
        return profileDao.getProfileFlow(userId)
    }

    suspend fun saveProfile(profile: SafeHavenProfile) {
        profileDao.insert(profile)
    }

    suspend fun updateProfile(profile: SafeHavenProfile) {
        profileDao.update(profile)
    }

    suspend fun deleteProfile(userId: String) {
        profileDao.deleteByUserId(userId)
    }

    // ==================== Incident Reports ====================

    suspend fun getAllIncidents(userId: String): List<IncidentReport> {
        return incidentReportDao.getAll(userId)
    }

    fun getAllIncidentsFlow(userId: String): Flow<List<IncidentReport>> {
        return incidentReportDao.getAllFlow(userId)
    }

    suspend fun saveIncident(report: IncidentReport) {
        // Encrypt sensitive fields before saving
        val encrypted = report.copy(
            descriptionEncrypted = if (report.descriptionEncrypted.isNotBlank()) {
                SafeHavenCrypto.encrypt(report.descriptionEncrypted)
            } else {
                ""
            },
            witnessesEncrypted = report.witnessesEncrypted?.let {
                if (it.isNotBlank()) SafeHavenCrypto.encrypt(it) else null
            },
            injuriesEncrypted = report.injuriesEncrypted?.let {
                if (it.isNotBlank()) SafeHavenCrypto.encrypt(it) else null
            }
        )
        incidentReportDao.insert(encrypted)
    }

    suspend fun deleteIncident(report: IncidentReport) {
        incidentReportDao.delete(report)
    }

    suspend fun deleteAllIncidents(userId: String) {
        incidentReportDao.deleteAll(userId)
    }

    // ==================== Evidence Items ====================

    suspend fun getAllEvidence(userId: String): List<EvidenceItem> {
        return evidenceItemDao.getAll(userId)
    }

    fun getAllEvidenceFlow(userId: String): Flow<List<EvidenceItem>> {
        return evidenceItemDao.getAllFlow(userId)
    }

    suspend fun saveEvidence(item: EvidenceItem) {
        evidenceItemDao.insert(item)
    }

    suspend fun deleteEvidence(item: EvidenceItem) {
        evidenceItemDao.delete(item)
    }

    suspend fun deleteAllEvidence(userId: String) {
        evidenceItemDao.deleteAll(userId)
    }

    // ==================== Verified Documents ====================

    suspend fun getAllDocuments(userId: String): List<VerifiedDocument> {
        return verifiedDocumentDao.getAll(userId)
    }

    fun getAllDocumentsFlow(userId: String): Flow<List<VerifiedDocument>> {
        return verifiedDocumentDao.getAllFlow(userId)
    }

    suspend fun saveDocument(document: VerifiedDocument) {
        verifiedDocumentDao.insert(document)
    }

    suspend fun deleteDocument(document: VerifiedDocument) {
        verifiedDocumentDao.delete(document)
    }

    suspend fun deleteAllDocuments(userId: String) {
        verifiedDocumentDao.deleteAll(userId)
    }

    // ==================== Legal Resources ====================

    suspend fun getResourcesByType(type: String): List<LegalResource> {
        return legalResourceDao.getByType(type)
    }

    suspend fun getFilteredResources(
        type: String,
        lgbtq: Boolean,
        trans: Boolean,
        bipoc: Boolean,
        male: Boolean,
        undoc: Boolean,
        disabled: Boolean,
        deaf: Boolean
    ): List<LegalResource> {
        return legalResourceDao.getFiltered(type, lgbtq, trans, bipoc, male, undoc, disabled, deaf)
    }

    suspend fun saveResources(resources: List<LegalResource>) {
        legalResourceDao.insertAll(resources)
    }

    suspend fun getResourceCount(): Int {
        return legalResourceDao.getCount()
    }

    // ==================== Survivor Profile ====================

    suspend fun getSurvivorProfile(userId: String): SurvivorProfile? {
        return survivorProfileDao.getByUserId(userId)
    }

    suspend fun saveSurvivorProfile(profile: SurvivorProfile) {
        survivorProfileDao.insert(profile)
    }

    suspend fun updateSurvivorProfile(profile: SurvivorProfile) {
        survivorProfileDao.update(profile)
    }

    // ==================== Healthcare Journeys ====================

    /**
     * Get all healthcare journeys for a user
     */
    fun getAllHealthcareJourneysFlow(userId: String): Flow<List<HealthcareJourney>> {
        return healthcareJourneyDao.getAllForUserFlow(userId)
    }

    /**
     * Get active healthcare journeys (not completed or cancelled)
     */
    fun getActiveHealthcareJourneysFlow(userId: String): Flow<List<HealthcareJourney>> {
        return healthcareJourneyDao.getActiveJourneysFlow(userId)
    }

    /**
     * Get healthcare journeys by status
     */
    fun getHealthcareJourneysByStatusFlow(userId: String, status: String): Flow<List<HealthcareJourney>> {
        return healthcareJourneyDao.getJourneysByStatusFlow(userId, status)
    }

    /**
     * Get single healthcare journey by ID
     */
    suspend fun getHealthcareJourneyById(journeyId: String): HealthcareJourney? {
        return healthcareJourneyDao.getById(journeyId)
    }

    /**
     * Get single healthcare journey by ID (reactive)
     */
    fun getHealthcareJourneyByIdFlow(journeyId: String): Flow<HealthcareJourney?> {
        return healthcareJourneyDao.getByIdFlow(journeyId)
    }

    /**
     * Get upcoming healthcare journeys
     */
    fun getUpcomingHealthcareJourneysFlow(userId: String): Flow<List<HealthcareJourney>> {
        return healthcareJourneyDao.getUpcomingJourneysFlow(userId)
    }

    /**
     * Get journeys needing arrangements
     */
    fun getJourneysNeedingArrangementsFlow(userId: String): Flow<List<HealthcareJourney>> {
        return healthcareJourneyDao.getJourneysNeedingArrangementsFlow(userId)
    }

    /**
     * Get journeys eligible for auto-deletion
     */
    suspend fun getHealthcareJourneysForAutoDeletion(currentTimestamp: Long): List<HealthcareJourney> {
        return healthcareJourneyDao.getJourneysForAutoDeletion(currentTimestamp)
    }

    /**
     * Get count of active healthcare journeys
     */
    fun getActiveHealthcareJourneyCountFlow(userId: String): Flow<Int> {
        return healthcareJourneyDao.getActiveJourneyCountFlow(userId)
    }

    /**
     * Save new healthcare journey
     * Note: Sensitive fields should already be encrypted before calling this
     */
    suspend fun saveHealthcareJourney(journey: HealthcareJourney) {
        healthcareJourneyDao.insert(journey)
    }

    /**
     * Update existing healthcare journey
     */
    suspend fun updateHealthcareJourney(journey: HealthcareJourney) {
        healthcareJourneyDao.update(journey)
    }

    /**
     * Delete healthcare journey
     */
    suspend fun deleteHealthcareJourney(journeyId: String) {
        healthcareJourneyDao.deleteById(journeyId)
    }

    /**
     * Delete all healthcare journeys for a user (for panic delete)
     */
    suspend fun deleteAllHealthcareJourneys(userId: String) {
        healthcareJourneyDao.deleteAllForUser(userId)
    }

    /**
     * Mark healthcare journey as completed
     */
    suspend fun markHealthcareJourneyCompleted(
        journeyId: String,
        completedTimestamp: Long,
        autoDeleteDate: Long?
    ) {
        healthcareJourneyDao.markAsCompleted(journeyId, completedTimestamp, autoDeleteDate)
    }

    /**
     * Cancel healthcare journey
     */
    suspend fun cancelHealthcareJourney(
        journeyId: String,
        reason: String?,
        timestamp: Long
    ) {
        healthcareJourneyDao.cancelJourney(journeyId, reason, timestamp)
    }

    /**
     * Update healthcare journey status
     */
    suspend fun updateHealthcareJourneyStatus(
        journeyId: String,
        newStatus: String,
        timestamp: Long
    ) {
        healthcareJourneyDao.updateStatus(journeyId, newStatus, timestamp)
    }

    // ==================== Healthcare Resources (Specialized Queries) ====================

    /**
     * Get reproductive healthcare clinics
     */
    suspend fun getReproductiveHealthcareClinics(
        state: String? = null,
        acceptsOutOfState: Boolean = false
    ): List<LegalResource> {
        return if (state != null) {
            legalResourceDao.getHealthcareClinicsInState(state)
        } else if (acceptsOutOfState) {
            legalResourceDao.getClinicsAcceptingOutOfState()
        } else {
            legalResourceDao.getByType("reproductive_healthcare")
        }
    }

    /**
     * Get recovery housing facilities
     */
    suspend fun getRecoveryHousing(state: String? = null): List<LegalResource> {
        return if (state != null) {
            legalResourceDao.getRecoveryHousingInState(state)
        } else {
            legalResourceDao.getByType("recovery_housing")
        }
    }

    /**
     * Get childcare providers
     */
    suspend fun getChildcareProviders(
        duringAppointment: Boolean = false,
        duringRecovery: Boolean = false
    ): List<LegalResource> {
        return when {
            duringAppointment -> legalResourceDao.getChildcareDuringAppointment()
            duringRecovery -> legalResourceDao.getChildcareDuringRecovery()
            else -> legalResourceDao.getByType("childcare")
        }
    }

    /**
     * Get financial assistance resources
     */
    suspend fun getFinancialAssistance(): List<LegalResource> {
        return legalResourceDao.getByType("financial_assistance")
    }

    /**
     * Get accompaniment services
     */
    suspend fun getAccompanimentServices(): List<LegalResource> {
        return legalResourceDao.getByType("accompaniment")
    }
}
