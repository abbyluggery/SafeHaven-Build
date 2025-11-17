package app.neurothrive.safehaven.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.entities.SafeHavenProfile
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home Screen
 *
 * Purpose: Main dashboard showing SafeHaven overview
 * - Quick stats (incident count, evidence count, etc.)
 * - Quick actions (take photo, create report, find resources)
 * - Active healthcare journeys
 * - User profile status
 *
 * SECURITY:
 * - All counts are live from encrypted database
 * - No sensitive data displayed in overview
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SafeHavenRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // User profile
    private val _profile = MutableStateFlow<SafeHavenProfile?>(null)
    val profile: StateFlow<SafeHavenProfile?> = _profile.asStateFlow()

    // Dashboard stats
    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val hasProfile: Boolean = false
    )

    data class DashboardStats(
        val incidentCount: Int = 0,
        val evidenceCount: Int = 0,
        val documentCount: Int = 0,
        val activeHealthcareJourneys: Int = 0,
        val lastIncidentDate: Long? = null
    )

    /**
     * Load dashboard data
     */
    fun loadDashboard(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Load profile
                repository.getProfileFlow(userId).collect { profile ->
                    _profile.value = profile
                    _uiState.value = _uiState.value.copy(
                        hasProfile = profile != null,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load dashboard: ${e.message}"
                )
            }
        }

        // Load stats
        loadStats(userId)
    }

    /**
     * Load dashboard statistics
     */
    private fun loadStats(userId: String) {
        viewModelScope.launch {
            try {
                // Collect incident count
                repository.getAllIncidentsFlow(userId).collect { incidents ->
                    val lastIncident = incidents.maxByOrNull { it.timestamp }

                    _stats.value = _stats.value.copy(
                        incidentCount = incidents.size,
                        lastIncidentDate = lastIncident?.timestamp
                    )
                }
            } catch (e: Exception) {
                // Silent failure for stats
            }
        }

        viewModelScope.launch {
            try {
                // Collect evidence count
                repository.getAllEvidenceFlow(userId).collect { evidence ->
                    _stats.value = _stats.value.copy(evidenceCount = evidence.size)
                }
            } catch (e: Exception) {
                // Silent failure
            }
        }

        viewModelScope.launch {
            try {
                // Collect document count
                repository.getAllDocumentsFlow(userId).collect { documents ->
                    _stats.value = _stats.value.copy(documentCount = documents.size)
                }
            } catch (e: Exception) {
                // Silent failure
            }
        }

        viewModelScope.launch {
            try {
                // Collect active healthcare journey count
                repository.getActiveHealthcareJourneyCountFlow(userId).collect { count ->
                    _stats.value = _stats.value.copy(activeHealthcareJourneys = count)
                }
            } catch (e: Exception) {
                // Silent failure
            }
        }
    }

    /**
     * Refresh dashboard
     */
    fun refresh(userId: String) {
        loadDashboard(userId)
    }
}
