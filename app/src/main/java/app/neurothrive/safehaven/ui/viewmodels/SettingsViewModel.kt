package app.neurothrive.safehaven.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.entities.SafeHavenProfile
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import app.neurothrive.safehaven.domain.usecases.PanicDeleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings Screen
 *
 * Purpose: Manage SafeHaven settings and profile
 * - Privacy settings (GPS, auto-delete, stealth mode)
 * - Security settings (passwords, panic mode)
 * - Identity settings (intersectional profile)
 * - Data management (export, backup, delete all)
 *
 * CRITICAL SETTINGS:
 * - GPS OFF by default for safety
 * - Dual password system (real + duress)
 * - Panic delete configuration
 * - Auto-delete timers
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SafeHavenRepository,
    private val panicDeleteUseCase: PanicDeleteUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // User profile/settings
    private val _profile = MutableStateFlow<SafeHavenProfile?>(null)
    val profile: StateFlow<SafeHavenProfile?> = _profile.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val isPanicDeleting: Boolean = false,
        val error: String? = null,
        val saveSuccess: Boolean = false,
        val panicDeleteComplete: Boolean = false
    )

    /**
     * Load user profile/settings
     */
    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getProfileFlow(userId).collect { profile ->
                    _profile.value = profile
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load settings: ${e.message}"
                )
            }
        }
    }

    /**
     * Update profile settings
     */
    fun updateProfile(updatedProfile: SafeHavenProfile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                repository.updateProfile(updatedProfile)
                _profile.value = updatedProfile
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save settings: ${e.message}"
                )
            }
        }
    }

    /**
     * Toggle GPS setting
     */
    fun toggleGPS(enabled: Boolean) {
        _profile.value?.let { profile ->
            updateProfile(profile.copy(gpsEnabled = enabled))
        }
    }

    /**
     * Toggle stealth mode
     */
    fun toggleStealthMode(enabled: Boolean) {
        _profile.value?.let { profile ->
            updateProfile(profile.copy(stealthModeEnabled = enabled))
        }
    }

    /**
     * Toggle auto-delete
     */
    fun toggleAutoDelete(enabled: Boolean, days: Int = 30) {
        _profile.value?.let { profile ->
            updateProfile(
                profile.copy(
                    autoDeleteEnabled = enabled,
                    autoDeleteDays = days
                )
            )
        }
    }

    /**
     * Execute panic delete
     */
    fun executePanicDelete(userId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPanicDeleting = true, error = null)
            try {
                val result = panicDeleteUseCase.execute(userId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isPanicDeleting = false,
                        panicDeleteComplete = true
                    )
                    onComplete()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isPanicDeleting = false,
                        error = "Panic delete failed: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPanicDeleting = false,
                    error = "Panic delete failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Reset save success state
     */
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    /**
     * Export data (for legal proceedings or backup)
     */
    fun exportData(userId: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // TODO: Implement data export to encrypted ZIP
                // Include: incident reports, evidence items, documents
                // Format: Legal-compliant PDF bundle
                onComplete("Export not yet implemented")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Export failed: ${e.message}"
                )
            }
        }
    }
}
