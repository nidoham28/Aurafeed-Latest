package com.nidoham.aurafeed

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nidoham.aurafeed.core.navigation.Auth
import com.nidoham.aurafeed.core.navigation.Shell
import com.nidoham.aurafeed.core.navigation.Splash
import com.nidoham.aurafeed.features.auth.screen.AuthScreen
import com.nidoham.aurafeed.features.auth.state.AuthMode
import com.nidoham.aurafeed.features.auth.state.AuthUiState
import com.nidoham.aurafeed.features.shell.screen.ShellScreen
import com.nidoham.aurafeed.features.splash.screen.SplashScreen

@Composable
fun AurafeedApp(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigateClearingBackStack(Auth)
                },
                onNavigateToHome = {
                    navController.navigateClearingBackStack(Shell)
                },
            )
        }

        composable<Auth> {
            AuthScreen(
                initialMode = AuthMode.SignIn,
                onSignInWithEmail = { email, password ->
                    AuthUiState.Idle
                },
                onSignInWithGoogle = {
                    AuthUiState.Idle
                },
                onSignUpWithEmail = { email, password ->
                    AuthUiState.Idle
                },
                onSignUpWithGoogle = {
                    AuthUiState.Idle
                },
                onForgotPassword = {
                    navController.navigateClearingBackStack(Auth)
                },
                onAuthSuccess = {
                    navController.navigateClearingBackStack(Shell)
                },
            )
        }

        composable<Shell> {
            ShellScreen()
        }
    }
}

/**
 * Navigates to [route] and clears the entire back stack (up to and
 * including the graph's start destination), so the user can't hit the
 * system back button and land back on splash or auth after they've
 * moved past it. `launchSingleTop` guards against duplicate entries
 * from a double-tap.
 */
private fun NavHostController.navigateClearingBackStack(route: Any) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}