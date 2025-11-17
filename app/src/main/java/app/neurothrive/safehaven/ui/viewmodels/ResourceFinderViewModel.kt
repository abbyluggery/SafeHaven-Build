package app.neurothrive.safehaven.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.entities.LegalResource
import app.neurothrive.safehaven.data.database.entities.SurvivorProfile
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import app.neurothrive.safehaven.domain.usecases.IntersectionalResourceMatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Resource Finder Screen
 *
 * Purpose: Find legal resources using intersectional matching
 * - Shelters, hotlines, legal aid, counseling, etc.
 * - Prioritizes resources for marginalized survivors
 * - Distance-based sorting (optional)
 * - Filtering by identity categories
 *
 * INTERSECTIONAL PRIORITY:
 * - Trans survivors → trans-specific resources first
 * - Male survivors → male-serving organizations
 * - Undocumented → U-Visa support, no ICE contact
 * - BIPOC → BIPOC-led organizations
 * - Disabled → wheelchair accessible facilities
 * - Deaf → ASL interpreter available
 */
@HiltViewModel
class ResourceFinderViewModel @Inject constructor(
    private val repository: SafeHavenRepository,
    private val resourceMatcher: IntersectionalResourceMatcher
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Matched resources with scores
    private val _resources = MutableStateFlow<List<IntersectionalResourceMatcher.ScoredResource>>(emptyList())
    val resources: StateFlow<List<IntersectionalResourceMatcher.ScoredResource>> = _resources.asStateFlow()

    // User's survivor profile
    private val _survivorProfile = MutableStateFlow<SurvivorProfile?>(null)
    val survivorProfile: StateFlow<SurvivorProfile?> = _survivorProfile.asStateFlow()

    // Selected resource type
    private val _selectedType = MutableStateFlow("shelter")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val totalResults: Int = 0,
        val hasProfile: Boolean = false
    )

    /**
     * Load survivor profile
     */
    fun loadSurvivorProfile(userId: String) {
        viewModelScope.launch {
            try {
                val profile = repository.getSurvivorProfile(userId)
                _survivorProfile.value = profile
                _uiState.value = _uiState.value.copy(hasProfile = profile != null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load profile: ${e.message}"
                )
            }
        }
    }

    /**
     * Search resources by type
     */
    fun searchResources(
        resourceType: String,
        currentLatitude: Double? = null,
        currentLongitude: Double? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            _selectedType.value = resourceType

            try {
                val profile = _survivorProfile.value
                if (profile == null) {
                    // No profile - show all resources without scoring
                    val allResources = repository.getResourcesByType(resourceType)
                    _resources.value = allResources.map {
                        IntersectionalResourceMatcher.ScoredResource(
                            resource = it,
                            score = 10.0,
                            distance = Double.MAX_VALUE
                        )
                    }
                } else {
                    // Use intersectional matching
                    val scoredResources = resourceMatcher.findResources(
                        profile = profile,
                        currentLatitude = currentLatitude,
                        currentLongitude = currentLongitude,
                        resourceType = resourceType
                    )
                    _resources.value = scoredResources
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalResults = _resources.value.size
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to search resources: ${e.message}"
                )
            }
        }
    }

    /**
     * Get resource by ID
     */
    suspend fun getResourceById(resourceId: String): LegalResource? {
        return _resources.value.find { it.resource.id == resourceId }?.resource
    }

    /**
     * Get available resource types
     */
    fun getResourceTypes(): List<ResourceType> {
        return listOf(
            ResourceType("shelter", "Emergency Shelters", "Safe housing"),
            ResourceType("hotline", "Crisis Hotlines", "24/7 support"),
            ResourceType("legal_aid", "Legal Aid", "Free legal help"),
            ResourceType("counseling", "Counseling", "Therapy & support groups"),
            ResourceType("financial", "Financial Aid", "Emergency funds"),
            ResourceType("employment", "Employment", "Job assistance"),
            ResourceType("reproductive_healthcare", "Healthcare", "Reproductive health"),
            ResourceType("childcare", "Childcare", "Safe childcare"),
            ResourceType("transportation", "Transportation", "Travel assistance")
        )
    }

    data class ResourceType(
        val id: String,
        val name: String,
        val description: String
    )
}
