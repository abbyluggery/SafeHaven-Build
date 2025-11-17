package app.neurothrive.safehaven.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.neurothrive.safehaven.data.database.entities.VerifiedDocument
import app.neurothrive.safehaven.data.repository.SafeHavenRepository
import app.neurothrive.safehaven.util.blockchain.DocumentVerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for Document Verification Screen
 *
 * Purpose: Cryptographically verify documents for legal proceedings
 * - Take photo of document (birth certificate, ID, etc.)
 * - Generate SHA-256 hash
 * - Timestamp on Polygon blockchain
 * - Create verified PDF with embedded hash
 * - Store encrypted version
 *
 * USE CASE:
 * Survivors who can't access original documents (abuser has them)
 * can photograph docs when found and create tamper-proof copies
 *
 * SECURITY:
 * - SHA-256 hash proves document authenticity
 * - Blockchain timestamp proves when photo was taken
 * - Both original photo and PDF are AES-256 encrypted
 * - GPS metadata stripped from photos
 */
@HiltViewModel
class DocumentVerificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SafeHavenRepository,
    private val verificationService: DocumentVerificationService
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Verified documents
    private val _documents = MutableStateFlow<List<VerifiedDocument>>(emptyList())
    val documents: StateFlow<List<VerifiedDocument>> = _documents.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val isVerifying: Boolean = false,
        val error: String? = null,
        val verificationSuccess: Boolean = false,
        val verifiedDocumentId: String? = null,
        val progressMessage: String? = null
    )

    /**
     * Load all verified documents for user
     */
    fun loadDocuments(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getAllDocumentsFlow(userId).collect { docs ->
                    _documents.value = docs
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load documents: ${e.message}"
                )
            }
        }
    }

    /**
     * Verify a document from photo file
     */
    fun verifyDocument(
        userId: String,
        photoFile: File,
        documentType: String,
        documentName: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isVerifying = true,
                error = null,
                progressMessage = "Generating cryptographic hash..."
            )

            try {
                // Step 1: Generate SHA-256 hash and create verified PDF
                _uiState.value = _uiState.value.copy(
                    progressMessage = "Creating verified PDF..."
                )
                val verifiedDoc = verificationService.verifyDocument(photoFile)

                // Step 2: Update document details
                val finalDoc = verifiedDoc.copy(
                    userId = userId,
                    documentType = documentType,
                    documentName = documentName
                )

                // Step 3: Save to database
                _uiState.value = _uiState.value.copy(
                    progressMessage = "Saving encrypted document..."
                )
                repository.saveDocument(finalDoc)

                // Step 4: Success
                _uiState.value = _uiState.value.copy(
                    isVerifying = false,
                    verificationSuccess = true,
                    verifiedDocumentId = finalDoc.id,
                    progressMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isVerifying = false,
                    error = "Verification failed: ${e.message}",
                    progressMessage = null
                )
            }
        }
    }

    /**
     * Delete verified document
     */
    fun deleteDocument(document: VerifiedDocument) {
        viewModelScope.launch {
            try {
                repository.deleteDocument(document)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete document: ${e.message}"
                )
            }
        }
    }

    /**
     * Reset verification success state
     */
    fun resetVerificationSuccess() {
        _uiState.value = _uiState.value.copy(
            verificationSuccess = false,
            verifiedDocumentId = null
        )
    }

    /**
     * Get available document types
     */
    fun getDocumentTypes(): List<DocumentType> {
        return listOf(
            DocumentType("birth_certificate", "Birth Certificate"),
            DocumentType("ssn_card", "Social Security Card"),
            DocumentType("drivers_license", "Driver's License"),
            DocumentType("passport", "Passport"),
            DocumentType("marriage_certificate", "Marriage Certificate"),
            DocumentType("divorce_decree", "Divorce Decree"),
            DocumentType("restraining_order", "Restraining Order"),
            DocumentType("lease_agreement", "Lease Agreement"),
            DocumentType("utility_bill", "Utility Bill"),
            DocumentType("bank_statement", "Bank Statement"),
            DocumentType("insurance_card", "Insurance Card"),
            DocumentType("medical_records", "Medical Records"),
            DocumentType("child_custody", "Child Custody Documents"),
            DocumentType("other", "Other Document")
        )
    }

    data class DocumentType(
        val id: String,
        val name: String
    )
}
