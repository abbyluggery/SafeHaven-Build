package app.neurothrive.safehaven.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserSession Manager
 *
 * Purpose: Track currently logged-in user across the app
 * - Stores userId in DataStore (persisted)
 * - Provides reactive Flow for current user
 * - Handles login/logout
 *
 * SECURITY:
 * - Only stores userId (not passwords)
 * - Cleared on panic delete
 * - Cleared on logout
 */

// DataStore extension
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

@Singleton
class UserSession @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.userDataStore

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("current_user_id")
        private val IS_DURESS_MODE_KEY = stringPreferencesKey("is_duress_mode")
    }

    /**
     * Current user ID as reactive Flow
     */
    val currentUserId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    /**
     * Check if in duress mode (decoy data)
     */
    val isDuressMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DURESS_MODE_KEY] == "true"
    }

    /**
     * Set current user (login)
     */
    suspend fun setCurrentUser(userId: String, isDuress: Boolean = false) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[IS_DURESS_MODE_KEY] = if (isDuress) "true" else "false"
        }
    }

    /**
     * Get current user ID (non-reactive)
     */
    suspend fun getCurrentUserId(): String? {
        var userId: String? = null
        dataStore.data.collect { preferences ->
            userId = preferences[USER_ID_KEY]
        }
        return userId
    }

    /**
     * Clear current user (logout)
     */
    suspend fun clearCurrentUser() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(IS_DURESS_MODE_KEY)
        }
    }

    /**
     * Check if user is logged in
     */
    suspend fun isLoggedIn(): Boolean {
        return getCurrentUserId() != null
    }
}
