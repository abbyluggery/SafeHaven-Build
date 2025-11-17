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
import java.security.MessageDigest
import javax.inject.Inject

/**
 * ViewModel for Login Screen
 *
 * Purpose: Authenticate user with SafeHaven password
 * - Real password → full access
 * - Duress password → show decoy data + trigger SOS
 * - Failed attempts tracked
 *
 * DUAL PASSWORD SYSTEM:
 * Critical safety feature for coerced access
 * - Real password: Full access to all data
 * - Duress password: Shows empty/fake data, optionally triggers silent SOS
 *
 * SECURITY:
 * - Passwords hashed with SHA-256
 * - No password recovery (can't be traced)
 * - Rate limiting on failed attempts
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: SafeHavenRepository,
    private val panicDeleteUseCase: PanicDeleteUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Authentication result
    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult.asStateFlow()

    data class UiState(
        val isAuthenticating: Boolean = false,
        val error: String? = null,
        val failedAttempts: Int = 0,
        val isLocked: Boolean = false
    )

    sealed class AuthResult {
        data class Success(val userId: String, val isDuress: Boolean) : AuthResult()
        object Failure : AuthResult()
    }

    /**
     * Authenticate user with password
     */
    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAuthenticating = true, error = null)

            try {
                // Get user profile
                val profile = repository.getProfile(userId)

                if (profile == null) {
                    // User doesn't exist
                    _authResult.value = AuthResult.Failure
                    _uiState.value = _uiState.value.copy(
                        isAuthenticating = false,
                        error = "Invalid credentials",
                        failedAttempts = _uiState.value.failedAttempts + 1
                    )
                    return@launch
                }

                // Hash the entered password
                val passwordHash = hashPassword(password)

                when {
                    // Real password - grant full access
                    passwordHash == profile.safeHavenPasswordHash -> {
                        _authResult.value = AuthResult.Success(
                            userId = userId,
                            isDuress = false
                        )
                        _uiState.value = _uiState.value.copy(
                            isAuthenticating = false,
                            failedAttempts = 0
                        )
                    }

                    // Duress password - show decoy data
                    passwordHash == profile.duressPasswordHash -> {
                        _authResult.value = AuthResult.Success(
                            userId = userId,
                            isDuress = true
                        )
                        _uiState.value = _uiState.value.copy(
                            isAuthenticating = false,
                            failedAttempts = 0
                        )

                        // TODO: Trigger silent SOS (optional setting)
                        // TODO: Show decoy empty database
                    }

                    // Wrong password
                    else -> {
                        _authResult.value = AuthResult.Failure
                        val newFailedAttempts = _uiState.value.failedAttempts + 1
                        _uiState.value = _uiState.value.copy(
                            isAuthenticating = false,
                            error = "Invalid credentials",
                            failedAttempts = newFailedAttempts,
                            isLocked = newFailedAttempts >= 5
                        )
                    }
                }
            } catch (e: Exception) {
                _authResult.value = AuthResult.Failure
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = false,
                    error = "Authentication failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Hash password using SHA-256
     */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Create new profile with passwords
     */
    fun createProfile(
        userId: String,
        realPassword: String,
        duressPassword: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAuthenticating = true, error = null)

            try {
                val profile = SafeHavenProfile(
                    userId = userId,
                    safeHavenPasswordHash = hashPassword(realPassword),
                    duressPasswordHash = hashPassword(duressPassword),
                    gpsEnabled = false, // GPS OFF by default
                    stealthModeEnabled = false,
                    autoDeleteEnabled = false,
                    autoDeleteDays = 30,
                    createdAt = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )

                repository.saveProfile(profile)

                _uiState.value = _uiState.value.copy(isAuthenticating = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = false,
                    error = "Failed to create profile: ${e.message}"
                )
            }
        }
    }

    /**
     * Reset authentication state
     */
    fun resetAuthResult() {
        _authResult.value = null
    }

    /**
     * Reset failed attempts (for testing)
     */
    fun resetFailedAttempts() {
        _uiState.value = _uiState.value.copy(
            failedAttempts = 0,
            isLocked = false
        )
    }
}
