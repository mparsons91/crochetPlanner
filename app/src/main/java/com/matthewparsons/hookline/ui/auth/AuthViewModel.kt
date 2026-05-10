package com.matthewparsons.hookline.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matthewparsons.hookline.domain.auth.AuthRepository
import com.matthewparsons.hookline.domain.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object SigningIn : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onGoogleIdTokenObtained(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.SigningIn
            authRepository.signInWithGoogleIdToken(idToken)
                .onSuccess { _uiState.value = AuthUiState.Idle }
                .onFailure {
                    _uiState.value = AuthUiState.Error(it.localizedMessage ?: "Sign-in failed")
                }
        }
    }

    fun onSignInError(message: String) {
        _uiState.value = AuthUiState.Error(message)
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
