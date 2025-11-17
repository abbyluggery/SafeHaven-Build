package app.neurothrive.safehaven.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.entities.IncidentReport
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for Incident Report Screen
 *
 * Purpose: Document abuse incidents with legal-formatted reports
 * - Create new incident reports
 * - View/edit existing reports
 * - Link to evidence items
 * - Export to PDF for legal proceedings
 *
 * SECURITY:
 * - Description and witness fields are AES-256 encrypted before storage
 * - GPS disabled by default (respects user setting)
 * - All data eligible for panic delete
 */
@HiltViewModel
class IncidentReportViewModel @Inject constructor(
    private val repository: SafeHavenRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // All incident reports
    private val _incidentReports = MutableStateFlow<List<IncidentReport>>(emptyList())
    val incidentReports: StateFlow<List<IncidentReport>> = _incidentReports.asStateFlow()

    // Current draft
    private val _currentDraft = MutableStateFlow(IncidentDraft())
    val currentDraft: StateFlow<IncidentDraft> = _currentDraft.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val error: String? = null,
        val saveSuccess: Boolean = false,
        val savedReportId: String? = null
    )

    data class IncidentDraft(
        val timestamp: Long = System.currentTimeMillis(),
        val incidentType: String = "physical", // physical, verbal, emotional, financial, sexual, stalking
        val description: String = "",
        val witnesses: String = "",
        val policeInvolved: Boolean = false,
        val policeReportNumber: String = "",
        val medicalAttention: Boolean = false,
        val injuries: String = "",
        val location: String = "",
        val perpetratorName: String = "",
        val perpetratorRelationship: String = ""
    )

    /**
     * Load all incident reports for user
     */
    fun loadIncidentReports(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getAllIncidentsFlow(userId).collect { reports ->
                    _incidentReports.value = reports
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load incident reports: ${e.message}"
                )
            }
        }
    }

    /**
     * Update draft field
     */
    fun updateDraft(draft: IncidentDraft) {
        _currentDraft.value = draft
    }

    /**
     * Save incident report
     */
    fun saveIncidentReport(userId: String, gpsEnabled: Boolean = false, latitude: Double? = null, longitude: Double? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val draft = _currentDraft.value
                val reportId = UUID.randomUUID().toString()

                val report = IncidentReport(
                    id = reportId,
                    userId = userId,
                    timestamp = draft.timestamp,
                    incidentType = draft.incidentType,
                    descriptionEncrypted = draft.description, // Will be encrypted in repository
                    witnessesEncrypted = draft.witnesses.takeIf { it.isNotBlank() },
                    policeInvolved = draft.policeInvolved,
                    policeReportNumber = draft.policeReportNumber.takeIf { draft.policeInvolved && it.isNotBlank() },
                    medicalAttention = draft.medicalAttention,
                    injuriesEncrypted = draft.injuries.takeIf { it.isNotBlank() },
                    location = draft.location.takeIf { it.isNotBlank() },
                    latitude = latitude.takeIf { gpsEnabled },
                    longitude = longitude.takeIf { gpsEnabled },
                    perpetratorName = draft.perpetratorName.takeIf { it.isNotBlank() },
                    perpetratorRelationship = draft.perpetratorRelationship.takeIf { it.isNotBlank() },
                    createdAt = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )

                repository.saveIncident(report)

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    savedReportId = reportId
                )

                // Reset draft
                _currentDraft.value = IncidentDraft()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save incident report: ${e.message}"
                )
            }
        }
    }

    /**
     * Delete incident report
     */
    fun deleteIncidentReport(report: IncidentReport) {
        viewModelScope.launch {
            try {
                repository.deleteIncident(report)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete incident report: ${e.message}"
                )
            }
        }
    }

    /**
     * Reset save success state
     */
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, savedReportId = null)
    }

    /**
     * Reset draft
     */
    fun resetDraft() {
        _currentDraft.value = IncidentDraft()
    }
}
