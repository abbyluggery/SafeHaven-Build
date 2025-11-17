package app.neurothrive.safehaven.ui.viewmodels

import android.telephony.SmsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.dao.EmergencyContactDao
import app.neurothrive.safehaven.data.database.entities.EmergencyContact
import app.neurothrive.safehaven.data.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Emergency Contacts ViewModel
 *
 * Manages emergency contact CRUD operations and test alerts
 */
@HiltViewModel
class EmergencyContactsViewModel @Inject constructor(
    private val emergencyContactDao: EmergencyContactDao,
    private val userSession: UserSession
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Live list of contacts
    val contacts: StateFlow<List<EmergencyContact>> = userSession.currentUserId
        .filterNotNull()
        .flatMapLatest { userId ->
            emergencyContactDao.getAllContactsFlow(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Add new emergency contact
     */
    fun addContact(
        name: String,
        phoneNumber: String,
        relationship: String,
        isPrimary: Boolean,
        customMessage: String?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val userId = userSession.currentUserId.value
                if (userId == null) {
                    _errorMessage.value = "User not logged in"
                    return@launch
                }

                val contact = EmergencyContact(
                    userId = userId,
                    name = name,
                    phoneNumber = phoneNumber,
                    relationship = relationship,
                    isPrimary = isPrimary,
                    customMessage = customMessage,
                    sendLocationUpdates = true,
                    isVerified = false
                )

                emergencyContactDao.insertContact(contact)

            } catch (e: Exception) {
                _errorMessage.value = "Failed to add contact: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update existing contact
     */
    fun updateContact(contact: EmergencyContact) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                emergencyContactDao.updateContact(contact)

            } catch (e: Exception) {
                _errorMessage.value = "Failed to update contact: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete contact
     */
    fun deleteContact(contactId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                emergencyContactDao.deleteContactById(contactId)

            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete contact: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggle primary/secondary status
     */
    fun togglePrimary(contactId: Long) {
        viewModelScope.launch {
            try {
                val contact = emergencyContactDao.getContactById(contactId)
                if (contact != null) {
                    emergencyContactDao.updateContact(
                        contact.copy(isPrimary = !contact.isPrimary)
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update contact: ${e.message}"
            }
        }
    }

    /**
     * Send test alert to contact
     * Marks contact as verified if successful
     */
    fun testContact(contactId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val contact = emergencyContactDao.getContactById(contactId)
                if (contact == null) {
                    _errorMessage.value = "Contact not found"
                    return@launch
                }

                // Send test SMS
                val testMessage = buildString {
                    append("SafeHaven Test Alert\n\n")
                    append("This is a test message from ${contact.name}'s SafeHaven app. ")
                    append("If they activate SOS, you will receive an emergency alert with their location.\n\n")
                    append("Reply STOP to unsubscribe from alerts.")
                }

                try {
                    // Note: This requires SMS permissions
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(
                        contact.phoneNumber,
                        null,
                        testMessage,
                        null,
                        null
                    )

                    // Update contact as tested and verified
                    emergencyContactDao.updateContact(
                        contact.copy(
                            lastTestedDate = System.currentTimeMillis(),
                            isVerified = true
                        )
                    )

                    _errorMessage.value = null // Success - clear any previous errors
                    // Note: In production, you might want a success message state

                } catch (e: SecurityException) {
                    _errorMessage.value = "SMS permission denied. Grant permission in settings."
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to send test SMS: ${e.message}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Test failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mark contact as verified
     */
    fun markAsVerified(contactId: Long) {
        viewModelScope.launch {
            try {
                val contact = emergencyContactDao.getContactById(contactId)
                if (contact != null) {
                    emergencyContactDao.updateContact(
                        contact.copy(isVerified = true)
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to verify contact: ${e.message}"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Get contact statistics
     */
    fun getContactStats(): Flow<ContactStats> = contacts.map { contactList ->
        ContactStats(
            totalContacts = contactList.size,
            primaryContacts = contactList.count { it.isPrimary },
            verifiedContacts = contactList.count { it.isVerified },
            testedContacts = contactList.count { it.lastTestedDate != null }
        )
    }
}

/**
 * Contact statistics data class
 */
data class ContactStats(
    val totalContacts: Int,
    val primaryContacts: Int,
    val verifiedContacts: Int,
    val testedContacts: Int
)
