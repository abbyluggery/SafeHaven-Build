package app.neurothrive.safehaven.data.database.dao

import androidx.room.*
import app.neurothrive.safehaven.data.database.entities.LegalResource
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Legal Resources
 * Handles intersectional resource database queries
 *
 * CRITICAL: Supports filtering by 48 intersectional categories:
 * - LGBTQIA+, Trans, Non-binary
 * - BIPOC, culturally specific
 * - Male-identifying survivors
 * - Undocumented survivors
 * - Disabled, Deaf/ASL
 * - Dependent care (children, dependent adults, pets)
 * - Vulnerable populations (pregnant, substance use, teen dating, elder abuse, trafficking, TBI, criminal record)
 * - Medical/mental health support
 * - Transportation support (CRITICAL FOR RURAL - pickup, virtual services, gas vouchers, Greyhound)
 * - Reproductive healthcare (CRITICAL POST-ROE - clinics, recovery housing, childcare, financial aid, accompaniment)
 */
@Dao
interface LegalResourceDao {

    @Query("SELECT * FROM legal_resources WHERE resourceType = :type")
    suspend fun getByType(type: String): List<LegalResource>

    @Query("SELECT * FROM legal_resources WHERE resourceType = :type")
    fun getByTypeFlow(type: String): Flow<List<LegalResource>>

    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = :type
        AND latitude IS NOT NULL
        AND longitude IS NOT NULL
        LIMIT 100
    """)
    suspend fun getByTypeWithLocation(type: String): List<LegalResource>

    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = :type
        AND (:lgbtq = 0 OR servesLGBTQIA = 1)
        AND (:trans = 0 OR transInclusive = 1)
        AND (:bipoc = 0 OR servesBIPOC = 1)
        AND (:male = 0 OR servesMaleIdentifying = 1)
        AND (:undoc = 0 OR servesUndocumented = 1)
        AND (:disabled = 0 OR servesDisabled = 1)
        AND (:deaf = 0 OR servesDeaf = 1)
        AND (:hasChildren = 0 OR acceptsChildren = 1)
        AND (:hasDependentAdults = 0 OR acceptsDependentAdults = 1)
        AND (:hasPets = 0 OR acceptsPets = 1)
        AND (:isPregnant = 0 OR servesPregnant = 1)
        AND (:hasSubstanceUse = 0 OR servesSubstanceUse = 1)
        AND (:isTeen = 0 OR servesTeenDating = 1)
        AND (:isElder = 0 OR servesElderAbuse = 1)
        AND (:isTrafficking = 0 OR servesTrafficking = 1)
        AND (:hasTBI = 0 OR servesTBI = 1)
        AND (:hasCriminalRecord = 0 OR acceptsCriminalRecord = 1)
        AND (:needsTransportation = 0 OR providesTransportation = 1 OR offersVirtualServices = 1 OR gasVoucherProgram = 1 OR greyhoundHomeFreePartner = 1)
        LIMIT 100
    """)
    suspend fun getFiltered(
        type: String,
        lgbtq: Boolean,
        trans: Boolean,
        bipoc: Boolean,
        male: Boolean,
        undoc: Boolean,
        disabled: Boolean,
        deaf: Boolean,
        hasChildren: Boolean,
        hasDependentAdults: Boolean,
        hasPets: Boolean,
        isPregnant: Boolean,
        hasSubstanceUse: Boolean,
        isTeen: Boolean,
        isElder: Boolean,
        isTrafficking: Boolean,
        hasTBI: Boolean,
        hasCriminalRecord: Boolean,
        needsTransportation: Boolean
    ): List<LegalResource>

    @Query("SELECT * FROM legal_resources WHERE state = :state")
    suspend fun getByState(state: String): List<LegalResource>

    @Query("SELECT * FROM legal_resources WHERE state = :state AND city = :city")
    suspend fun getByCity(state: String, city: String): List<LegalResource>

    @Query("SELECT * FROM legal_resources WHERE id = :id")
    suspend fun getById(id: String): LegalResource?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resource: LegalResource)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(resources: List<LegalResource>)

    @Update
    suspend fun update(resource: LegalResource)

    @Delete
    suspend fun delete(resource: LegalResource)

    @Query("DELETE FROM legal_resources")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM legal_resources")
    suspend fun getCount(): Int

    @Query("SELECT DISTINCT resourceType FROM legal_resources")
    suspend fun getAllResourceTypes(): List<String>

    // ==================== REPRODUCTIVE HEALTHCARE QUERIES (POST-ROE SAFETY) ====================

    /**
     * Get reproductive healthcare clinics in a specific state
     * Returns clinics that provide reproductive healthcare services
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = 'reproductive_healthcare'
        AND state = :state
        AND providesReproductiveHealthcare = 1
        ORDER BY city ASC
    """)
    suspend fun getHealthcareClinicsInState(state: String): List<LegalResource>

    /**
     * Get all clinics that accept out-of-state patients
     * CRITICAL for survivors traveling for reproductive healthcare
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = 'reproductive_healthcare'
        AND acceptsOutOfStatePatients = 1
        AND providesReproductiveHealthcare = 1
        ORDER BY state ASC, city ASC
    """)
    suspend fun getClinicsAcceptingOutOfState(): List<LegalResource>

    /**
     * Get recovery housing facilities in a specific state
     * Provides safe housing for survivors recovering from medical procedures
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = 'recovery_housing'
        AND state = :state
        AND providesRecoveryHousing = 1
        ORDER BY city ASC
    """)
    suspend fun getRecoveryHousingInState(state: String): List<LegalResource>

    /**
     * Get childcare providers that offer care during medical appointments
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = 'childcare'
        AND childcareDuringAppointment = 1
        ORDER BY state ASC, city ASC
    """)
    suspend fun getChildcareDuringAppointment(): List<LegalResource>

    /**
     * Get childcare providers that offer care during recovery periods
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = 'childcare'
        AND childcareDuringRecovery = 1
        ORDER BY state ASC, city ASC
    """)
    suspend fun getChildcareDuringRecovery(): List<LegalResource>

    /**
     * Get financial assistance resources for healthcare travel
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = 'financial_assistance'
        AND (financialAssistanceAvailable = 1 OR travelFundingAvailable = 1)
        ORDER BY organizationName ASC
    """)
    suspend fun getFinancialAssistanceResources(): List<LegalResource>

    /**
     * Get accompaniment services (volunteer support for medical appointments)
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE resourceType = 'accompaniment'
        AND accompanimentServices = 1
        ORDER BY state ASC, city ASC
    """)
    suspend fun getAccompanimentServices(): List<LegalResource>

    /**
     * Comprehensive healthcare journey resource search
     * Returns all resources matching survivor's journey needs
     */
    @Query("""
        SELECT * FROM legal_resources
        WHERE (
            (resourceType = 'reproductive_healthcare' AND :needsClinic = 1) OR
            (resourceType = 'recovery_housing' AND :needsHousing = 1) OR
            (resourceType = 'childcare' AND :needsChildcare = 1) OR
            (resourceType = 'financial_assistance' AND :needsFinancialAid = 1) OR
            (resourceType = 'accompaniment' AND :needsAccompaniment = 1)
        )
        AND (:state IS NULL OR state = :state)
        ORDER BY resourceType ASC, state ASC, city ASC
    """)
    suspend fun getHealthcareJourneyResources(
        needsClinic: Boolean,
        needsHousing: Boolean,
        needsChildcare: Boolean,
        needsFinancialAid: Boolean,
        needsAccompaniment: Boolean,
        state: String?
    ): List<LegalResource>
}
