package com.khoihealth.app.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khoihealth.app.auth.domain.repository.AuthRepository
import com.khoihealth.app.core.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.login(email.trim(), password)) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.register(name.trim(), email.trim(), password)) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.forgotPassword(email.trim())) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState(successMessage = "Reset link sent to $email")
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun updateProfile(
        weightKg: Float, heightCm: Int, gender: String,
        dateOfBirth: String, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.updateProfile(weightKg, heightCm, gender, dateOfBirth)) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
