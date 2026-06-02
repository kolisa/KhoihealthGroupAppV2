package com.khoihealth.app.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null,
    val weightKg: Float = 0f,
    val heightCm: Int = 0,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val token: String = ""
)

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class ResetPasswordRequest(val email: String)

data class AuthResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)
