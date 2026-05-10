package com.matthewparsons.hookline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.matthewparsons.hookline.domain.auth.AuthState
import com.matthewparsons.hookline.ui.auth.AuthScreen
import com.matthewparsons.hookline.ui.auth.AuthViewModel
import com.matthewparsons.hookline.ui.detail.PatternDetailScreen
import com.matthewparsons.hookline.ui.home.HomeScreen
import com.matthewparsons.hookline.ui.navigation.HooklineDestinations
import com.matthewparsons.hookline.ui.newpattern.NewPatternScreen

@Composable
fun HooklineApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        is AuthState.SignedOut -> AuthScreen(authViewModel)
        is AuthState.SignedIn -> SignedInNavGraph(onSignOut = authViewModel::signOut)
    }
}

@Composable
private fun SignedInNavGraph(onSignOut: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HooklineDestinations.HOME,
    ) {
        composable(HooklineDestinations.HOME) {
            HomeScreen(
                onNewPattern = { navController.navigate(HooklineDestinations.NEW_PATTERN) },
                onPatternClick = { id ->
                    navController.navigate(HooklineDestinations.patternDetail(id))
                },
                onSignOut = onSignOut,
            )
        }
        composable(HooklineDestinations.NEW_PATTERN) {
            NewPatternScreen(
                onPatternSaved = { id ->
                    navController.navigate(HooklineDestinations.patternDetail(id)) {
                        popUpTo(HooklineDestinations.HOME) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = HooklineDestinations.PATTERN_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(HooklineDestinations.PATTERN_DETAIL_ARG_ID) {
                    type = NavType.StringType
                }
            ),
        ) {
            PatternDetailScreen(
                onBack = { navController.popBackStack() },
                onDeleted = {
                    navController.popBackStack(HooklineDestinations.HOME, inclusive = false)
                },
            )
        }
    }
}
