package com.matthewparsons.hookline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matthewparsons.hookline.domain.auth.AuthState
import com.matthewparsons.hookline.ui.auth.AuthScreen
import com.matthewparsons.hookline.ui.auth.AuthViewModel
import com.matthewparsons.hookline.ui.home.SignedInPlaceholderScreen

/**
 * Top-level composable: gates the app on authentication state. Unsigned →
 * AuthScreen; signed in → home (currently a placeholder, real home arrives
 * in Phase 4).
 */
@Composable
fun HooklineApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (val state = authState) {
        is AuthState.SignedOut -> AuthScreen(authViewModel)
        is AuthState.SignedIn -> SignedInPlaceholderScreen(
            user = state.user,
            onSignOut = authViewModel::signOut,
        )
    }
}
