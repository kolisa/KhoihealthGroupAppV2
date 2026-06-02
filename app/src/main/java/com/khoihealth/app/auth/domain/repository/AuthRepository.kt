package com.khoihealth.app.auth.domain.repository

import com.khoihealth.app.auth.domain.model.AuthResponse
import com.khoihealth.app.auth.domain.model.User
import com.khoihealth.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    suspend fun login(email: String, password: String): NetworkResult<AuthResponse>
    suspend fun register(name: String, email: String, password: String): NetworkResult<AuthResponse>
    suspend fun forgotPassword(email: String): NetworkResult<Unit>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun updateProfile(weightKg: Float, heightCm: Int, gender: String, dateOfBirth: String): NetworkResult<User>
}
