package com.matthewparsons.hookline.domain.auth

sealed interface AuthState {
    data object SignedOut : AuthState
    data class SignedIn(val user: AuthUser) : AuthState
}
