package com.khoihealth.app.auth.data

import com.khoihealth.app.auth.data.local.UserPreferences
import com.khoihealth.app.auth.data.remote.AuthApiService
import com.khoihealth.app.auth.data.remote.ForgotPasswordBody
import com.khoihealth.app.auth.data.remote.LoginBody
import com.khoihealth.app.auth.data.remote.RegisterBody
import com.khoihealth.app.auth.data.remote.UpdateProfileBody
import com.khoihealth.app.auth.domain.model.AuthResponse
import com.khoihealth.app.auth.domain.model.User
import com.khoihealth.app.auth.domain.repository.AuthRepository
import com.khoihealth.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedIn

    override suspend fun login(email: String, password: String): NetworkResult<AuthResponse> {
        return try {
            val response = apiService.login(LoginBody(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                userPreferences.saveLoginSession(body.token, body.user.id, body.user.email, body.user.name)
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(name: String, email: String, password: String): NetworkResult<AuthResponse> {
        return try {
            val response = apiService.register(RegisterBody(name, email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                userPreferences.saveLoginSession(body.token, body.user.id, body.user.email, body.user.name)
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun forgotPassword(email: String): NetworkResult<Unit> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordBody(email))
            if (response.isSuccessful) NetworkResult.Success(Unit)
            else NetworkResult.Error(response.message(), response.code())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Reset failed")
        }
    }

    override suspend fun logout() {
        userPreferences.clearSession()
    }

    override suspend fun getCurrentUser(): User? {
        val userId = userPreferences.getUserId() ?: return null
        val email = userPreferences.userEmail
        val name = userPreferences.userName
        return null // Loaded from DataStore flows in ViewModel
    }

    override suspend fun updateProfile(
        weightKg: Float, heightCm: Int, gender: String, dateOfBirth: String
    ): NetworkResult<User> {
        return try {
            val response = apiService.updateProfile(UpdateProfileBody(weightKg, heightCm, gender, dateOfBirth))
            if (response.isSuccessful) {
                userPreferences.updateProfile(weightKg, heightCm)
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Update failed")
        }
    }
}
