package app.neurothrive.safehaven.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.entities.SOSActivationMethod
import app.neurothrive.safehaven.data.database.entities.SOSDeactivationMethod
import app.neurothrive.safehaven.data.database.entities.SOSSession
import app.neurothrive.safehaven.util.emergency.EmergencyAlertManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SOS Panic Button ViewModel
 *
 * Manages SOS emergency alert activation and deactivation
 *
 * Features:
 * - Activate/deactivate SOS
 * - Track active session
 * - Send location updates
 * - Handle false alarms
 */
@HiltViewModel
class SOSViewModel @Inject constructor(
    private val emergencyAlertManager: EmergencyAlertManager
) : ViewModel() {

    // UI State
    private val _isSOSActive = MutableStateFlow(false)
    val isSOSActive: StateFlow<Boolean> = _isSOSActive.asStateFlow()

    private val _activeSession = MutableStateFlow<SOSSession?>(null)
    val activeSession: StateFlow<SOSSession?> = _activeSession.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Observe active session from manager
        viewModelScope.launch {
            emergencyAlertManager.activeSession.collect { session ->
                _activeSession.value = session
                _isSOSActive.value = session != null
            }
        }
    }

    /**
     * Load active session for user (call when screen loads)
     */
    fun loadActiveSession(userId: String) {
        viewModelScope.launch {
            try {
                emergencyAlertManager.loadActiveSession(userId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load SOS status: ${e.message}"
            }
        }
    }

    /**
     * Activate SOS panic button
     * Sends immediate emergency alerts
     */
    fun activateSOS(userId: String, includeLocation: Boolean = true) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val sessionId = emergencyAlertManager.activateSOS(
                    userId = userId,
                    activationMethod = SOSActivationMethod.LONG_PRESS,
                    includeLocation = includeLocation
                )

                if (sessionId == null) {
                    _errorMessage.value = "Failed to activate SOS. Check emergency contacts and permissions."
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error activating SOS: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Deactivate SOS panic button
     * Sends "all clear" message
     */
    fun deactivateSOS(userId: String, sendAllClear: Boolean = true) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                emergencyAlertManager.deactivateSOS(
                    userId = userId,
                    deactivationMethod = SOSDeactivationMethod.LONG_PRESS,
                    sendAllClear = sendAllClear
                )

            } catch (e: Exception) {
                _errorMessage.value = "Error deactivating SOS: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mark SOS as false alarm
     * Sends false alarm message to contacts
     */
    fun markFalseAlarm(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                emergencyAlertManager.sendFalseAlarmMessage(userId)

                // Deactivate SOS without "all clear" (we sent false alarm instead)
                emergencyAlertManager.deactivateSOS(
                    userId = userId,
                    deactivationMethod = SOSDeactivationMethod.LONG_PRESS,
                    sendAllClear = false
                )

            } catch (e: Exception) {
                _errorMessage.value = "Error sending false alarm: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Send location update during active SOS
     */
    fun sendLocationUpdate(userId: String) {
        viewModelScope.launch {
            try {
                emergencyAlertManager.sendLocationUpdate(userId)
            } catch (e: Exception) {
                _errorMessage.value = "Error sending location update: ${e.message}"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
