package com.khoihealth.app.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "khoi_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val USER_ID    = stringPreferencesKey("user_id")
    private val USER_EMAIL = stringPreferencesKey("user_email")
    private val USER_NAME  = stringPreferencesKey("user_name")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val PROFILE_COMPLETE = booleanPreferencesKey("profile_complete")
    private val WEIGHT_KG = stringPreferencesKey("weight_kg")
    private val HEIGHT_CM = stringPreferencesKey("height_cm")
    private val DARK_MODE = booleanPreferencesKey("dark_mode")

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val isProfileComplete: Flow<Boolean> = context.dataStore.data.map { it[PROFILE_COMPLETE] ?: false }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }

    suspend fun getAuthToken(): String? = context.dataStore.data.firstOrNull()?.get(AUTH_TOKEN)
    suspend fun getUserId(): String? = context.dataStore.data.firstOrNull()?.get(USER_ID)

    suspend fun saveLoginSession(
        token: String,
        userId: String,
        email: String,
        name: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN]    = token
            prefs[USER_ID]       = userId
            prefs[USER_EMAIL]    = email
            prefs[USER_NAME]     = name
            prefs[IS_LOGGED_IN]  = true
        }
    }

    suspend fun updateProfile(weightKg: Float, heightCm: Int) {
        context.dataStore.edit { prefs ->
            prefs[WEIGHT_KG]       = weightKg.toString()
            prefs[HEIGHT_CM]       = heightCm.toString()
            prefs[PROFILE_COMPLETE] = true
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN)
            prefs.remove(USER_ID)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_NAME)
            prefs[IS_LOGGED_IN] = false
        }
    }
}
