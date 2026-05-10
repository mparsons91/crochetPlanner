package com.matthewparsons.hookline.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.matthewparsons.hookline.domain.auth.AuthRepository
import com.matthewparsons.hookline.domain.auth.AuthState
import com.matthewparsons.hookline.domain.auth.AuthUser
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    private val _authState = MutableStateFlow(toState(firebaseAuth.currentUser))
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _authState.value = toState(auth.currentUser)
        }
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        result.user?.toAuthUser() ?: error("Sign-in succeeded but Firebase returned no user")
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    private fun toState(user: FirebaseUser?): AuthState =
        user?.let { AuthState.SignedIn(it.toAuthUser()) } ?: AuthState.SignedOut
}

private fun FirebaseUser.toAuthUser(): AuthUser = AuthUser(
    id = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl?.toString(),
)
