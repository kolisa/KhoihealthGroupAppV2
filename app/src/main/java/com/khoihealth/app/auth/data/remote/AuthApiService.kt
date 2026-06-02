package com.khoihealth.app.auth.data.remote

import com.khoihealth.app.auth.domain.model.AuthResponse
import com.khoihealth.app.auth.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

data class LoginBody(val email: String, val password: String)
data class RegisterBody(val name: String, val email: String, val password: String)
data class ForgotPasswordBody(val email: String)
data class UpdateProfileBody(
    val weightKg: Float,
    val heightCm: Int,
    val gender: String,
    val dateOfBirth: String
)

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body body: RegisterBody): Response<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordBody): Response<Unit>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body body: UpdateProfileBody): Response<User>
}
