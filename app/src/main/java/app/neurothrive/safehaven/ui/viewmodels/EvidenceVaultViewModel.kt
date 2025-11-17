package app.neurothrive.safehaven.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.entities.EvidenceItem
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Evidence Vault Screen
 *
 * Purpose: Manage encrypted evidence items (photos, videos, audio)
 * - Display all evidence for current user
 * - Filter by type or linked incident
 * - Delete evidence items
 * - Decrypt for viewing
 *
 * SECURITY:
 * - All files are AES-256 encrypted at rest
 * - Decryption happens on-demand in memory
 * - Supports panic delete
 */
@HiltViewModel
class EvidenceVaultViewModel @Inject constructor(
    private val repository: SafeHavenRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Evidence items
    private val _evidenceItems = MutableStateFlow<List<EvidenceItem>>(emptyList())
    val evidenceItems: StateFlow<List<EvidenceItem>> = _evidenceItems.asStateFlow()

    // Selected filter
    private val _selectedFilter = MutableStateFlow(FilterType.ALL)
    val selectedFilter: StateFlow<FilterType> = _selectedFilter.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val totalItems: Int = 0,
        val totalSizeMB: Double = 0.0,
        val isDeleting: Boolean = false
    )

    enum class FilterType {
        ALL, PHOTOS, VIDEOS, AUDIO
    }

    /**
     * Load all evidence for user
     */
    fun loadEvidence(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getAllEvidenceFlow(userId).collect { items ->
                    _evidenceItems.value = applyFilter(items)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        totalItems = items.size,
                        totalSizeMB = items.sumOf { it.fileSize }.toDouble() / (1024 * 1024)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load evidence: ${e.message}"
                )
            }
        }
    }

    /**
     * Set filter type
     */
    fun setFilter(filter: FilterType) {
        _selectedFilter.value = filter
        _evidenceItems.value = applyFilter(_evidenceItems.value)
    }

    /**
     * Apply current filter to items
     */
    private fun applyFilter(items: List<EvidenceItem>): List<EvidenceItem> {
        return when (_selectedFilter.value) {
            FilterType.ALL -> items
            FilterType.PHOTOS -> items.filter { it.evidenceType == "photo" }
            FilterType.VIDEOS -> items.filter { it.evidenceType == "video" }
            FilterType.AUDIO -> items.filter { it.evidenceType == "audio" }
        }
    }

    /**
     * Delete evidence item
     */
    fun deleteEvidence(item: EvidenceItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, error = null)
            try {
                repository.deleteEvidence(item)
                _uiState.value = _uiState.value.copy(isDeleting = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    error = "Failed to delete evidence: ${e.message}"
                )
            }
        }
    }

    /**
     * Get evidence items linked to specific incident
     */
    fun getEvidenceForIncident(incidentId: String): List<EvidenceItem> {
        return _evidenceItems.value.filter { it.incidentReportId == incidentId }
    }
}
