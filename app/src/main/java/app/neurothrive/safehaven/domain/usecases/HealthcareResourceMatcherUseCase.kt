package app.neurothrive.safehaven.domain.usecases

import app.neurothrive.safehaven.data.database.entities.LegalResource
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import javax.inject.Inject

/**
 * Healthcare Resource Matcher Use Case
 *
 * Purpose: Intelligently match survivors with appropriate healthcare journey resources
 * based on their specific needs and circumstances.
 *
 * CRITICAL POST-ROE SAFETY:
 * - Helps survivors find clinics accepting out-of-state patients
 * - Coordinates complete journey (clinic + housing + childcare + funding + support)
 * - Prioritizes resources that meet multiple needs
 * - Provides fallback options when primary matches unavailable
 *
 * PRIVACY:
 * - No location tracking
 * - Queries are local-only (no network calls)
 * - Results can be quickly cleared
 */
class HealthcareResourceMatcherUseCase @Inject constructor(
    private val repository: SafeHavenRepository
) {

    /**
     * Match result containing categorized resources
     */
    data class MatchResult(
        val clinics: List<LegalResource>,
        val recoveryHousing: List<LegalResource>,
        val childcare: List<LegalResource>,
        val financialAssistance: List<LegalResource>,
        val accompaniment: List<LegalResource>,
        val totalMatches: Int
    ) {
        val hasMatches: Boolean get() = totalMatches > 0
        val hasAllRequiredResources: Boolean
            get() = clinics.isNotEmpty() // At minimum, we need a clinic
    }

    /**
     * Journey requirements for resource matching
     */
    data class JourneyRequirements(
        val needsClinic: Boolean = true, // Almost always true
        val targetState: String? = null, // State where survivor wants to travel
        val needsOutOfStateClinic: Boolean = false, // True if traveling from restricted state
        val needsRecoveryHousing: Boolean = false,
        val needsChildcareDuringAppointment: Boolean = false,
        val needsChildcareDuringRecovery: Boolean = false,
        val needsFinancialAssistance: Boolean = false,
        val needsAccompaniment: Boolean = false
    )

    /**
     * Find all matching resources for a healthcare journey
     *
     * @param requirements Journey requirements
     * @return MatchResult containing categorized resources
     */
    suspend fun findMatchingResources(requirements: JourneyRequirements): Result<MatchResult> {
        return try {
            val clinics = if (requirements.needsClinic) {
                findClinics(
                    targetState = requirements.targetState,
                    needsOutOfState = requirements.needsOutOfStateClinic
                )
            } else emptyList()

            val recoveryHousing = if (requirements.needsRecoveryHousing) {
                findRecoveryHousing(requirements.targetState)
            } else emptyList()

            val childcare = if (requirements.needsChildcareDuringAppointment || requirements.needsChildcareDuringRecovery) {
                findChildcare(
                    duringAppointment = requirements.needsChildcareDuringAppointment,
                    duringRecovery = requirements.needsChildcareDuringRecovery
                )
            } else emptyList()

            val financialAssistance = if (requirements.needsFinancialAssistance) {
                findFinancialAssistance()
            } else emptyList()

            val accompaniment = if (requirements.needsAccompaniment) {
                findAccompaniment()
            } else emptyList()

            val totalMatches = clinics.size + recoveryHousing.size + childcare.size +
                    financialAssistance.size + accompaniment.size

            Result.success(
                MatchResult(
                    clinics = clinics,
                    recoveryHousing = recoveryHousing,
                    childcare = childcare,
                    financialAssistance = financialAssistance,
                    accompaniment = accompaniment,
                    totalMatches = totalMatches
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Find clinics based on requirements
     */
    private suspend fun findClinics(
        targetState: String?,
        needsOutOfState: Boolean
    ): List<LegalResource> {
        return if (needsOutOfState) {
            // Survivor needs out-of-state care - show all accepting clinics
            repository.getClinicsAcceptingOutOfState()
        } else if (targetState != null) {
            // Survivor specified target state
            repository.getHealthcareClinicsInState(targetState)
        } else {
            // Show all clinics accepting out-of-state patients as default
            repository.getClinicsAcceptingOutOfState()
        }
    }

    /**
     * Find recovery housing
     */
    private suspend fun findRecoveryHousing(targetState: String?): List<LegalResource> {
        return if (targetState != null) {
            repository.getRecoveryHousingInState(targetState)
        } else {
            // If no state specified, get all recovery housing resources
            repository.getResourcesByType("recovery_housing")
        }
    }

    /**
     * Find childcare providers
     */
    private suspend fun findChildcare(
        duringAppointment: Boolean,
        duringRecovery: Boolean
    ): List<LegalResource> {
        val appointmentChildcare = if (duringAppointment) {
            repository.getChildcareDuringAppointment()
        } else emptyList()

        val recoveryChildcare = if (duringRecovery) {
            repository.getChildcareDuringRecovery()
        } else emptyList()

        // Combine and deduplicate (some providers offer both)
        return (appointmentChildcare + recoveryChildcare).distinctBy { it.id }
    }

    /**
     * Find financial assistance resources
     */
    private suspend fun findFinancialAssistance(): List<LegalResource> {
        return repository.getFinancialAssistanceResources()
    }

    /**
     * Find accompaniment services
     */
    private suspend fun findAccompaniment(): List<LegalResource> {
        return repository.getAccompanimentServices()
    }

    /**
     * Score a clinic based on how well it meets survivor needs
     * Higher score = better match
     *
     * Used for ranking clinics when multiple options available
     */
    fun scoreClinic(
        clinic: LegalResource,
        requirements: JourneyRequirements
    ): Int {
        var score = 0

        // Base score for being a reproductive healthcare clinic
        if (clinic.providesReproductiveHealthcare) score += 10

        // Bonus for accepting out-of-state if needed
        if (requirements.needsOutOfStateClinic && clinic.acceptsOutOfStatePatients) {
            score += 20 // High priority
        }

        // Bonus for providing integrated services (one-stop shop)
        if (requirements.needsRecoveryHousing && clinic.providesRecoveryHousing) {
            score += 15
        }

        if (requirements.needsChildcareDuringAppointment && clinic.childcareDuringAppointment) {
            score += 10
        }

        if (requirements.needsFinancialAssistance && clinic.financialAssistanceAvailable) {
            score += 10
        }

        if (requirements.needsFinancialAssistance && clinic.travelFundingAvailable) {
            score += 10
        }

        if (requirements.needsAccompaniment && clinic.accompanimentServices) {
            score += 5
        }

        // Bonus for being in target state
        if (requirements.targetState != null && clinic.state == requirements.targetState) {
            score += 15
        }

        return score
    }

    /**
     * Rank clinics by how well they meet survivor needs
     *
     * @param clinics List of clinics to rank
     * @param requirements Journey requirements
     * @return Clinics sorted by match score (best first)
     */
    fun rankClinics(
        clinics: List<LegalResource>,
        requirements: JourneyRequirements
    ): List<Pair<LegalResource, Int>> {
        return clinics
            .map { clinic -> clinic to scoreClinic(clinic, requirements) }
            .sortedByDescending { it.second } // Highest score first
    }

    /**
     * Get comprehensive journey resource package
     * Finds the best matching resources for a complete journey
     *
     * @param requirements Journey requirements
     * @return Matched resources with best clinic ranked first
     */
    suspend fun getComprehensiveJourneyPackage(
        requirements: JourneyRequirements
    ): Result<MatchResult> {
        return try {
            val matchResult = findMatchingResources(requirements).getOrThrow()

            // Rank clinics by how well they meet needs
            val rankedClinics = if (matchResult.clinics.isNotEmpty()) {
                rankClinics(matchResult.clinics, requirements).map { it.first }
            } else {
                emptyList()
            }

            Result.success(
                matchResult.copy(clinics = rankedClinics)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if there are sufficient resources for a journey
     *
     * @param requirements Journey requirements
     * @return True if minimum resources available (at least one clinic)
     */
    suspend fun hasSufficientResources(requirements: JourneyRequirements): Boolean {
        return try {
            val matchResult = findMatchingResources(requirements).getOrThrow()
            matchResult.hasAllRequiredResources
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get resource availability summary
     * Useful for showing survivor what's available before planning journey
     */
    suspend fun getResourceAvailability(): Result<ResourceAvailability> {
        return try {
            val totalClinics = repository.getClinicsAcceptingOutOfState().size
            val totalRecoveryHousing = repository.getResourcesByType("recovery_housing").size
            val totalChildcare = repository.getResourcesByType("childcare").size
            val totalFinancialAid = repository.getFinancialAssistanceResources().size
            val totalAccompaniment = repository.getAccompanimentServices().size

            Result.success(
                ResourceAvailability(
                    clinicsAvailable = totalClinics,
                    recoveryHousingAvailable = totalRecoveryHousing,
                    childcareAvailable = totalChildcare,
                    financialAidAvailable = totalFinancialAid,
                    accompanimentAvailable = totalAccompaniment
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Resource availability summary
     */
    data class ResourceAvailability(
        val clinicsAvailable: Int,
        val recoveryHousingAvailable: Int,
        val childcareAvailable: Int,
        val financialAidAvailable: Int,
        val accompanimentAvailable: Int
    ) {
        val totalResources: Int
            get() = clinicsAvailable + recoveryHousingAvailable + childcareAvailable +
                    financialAidAvailable + accompanimentAvailable
    }
}
