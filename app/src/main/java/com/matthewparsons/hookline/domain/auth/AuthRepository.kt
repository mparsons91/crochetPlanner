package com.matthewparsons.hookline.domain.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over Firebase Auth. UI/ViewModels depend on this; the data
 * layer provides the implementation.
 */
interface AuthRepository {
    val authState: StateFlow<AuthState>

    /**
     * Exchanges a Google ID token (obtained via Credential Manager) for a
     * Firebase session. Updates [authState] on success.
     */
    suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser>

    suspend fun signOut()
}
