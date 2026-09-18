package com.nidoham.aurafeed.features.auth.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.nidoham.aurafeed.features.auth.state.AuthMode
import com.nidoham.aurafeed.features.auth.state.AuthSession
import com.nidoham.aurafeed.features.auth.state.AuthUiState
import com.nidoham.aurafeed.ui.theme.AurafeedMotion

/**
 * Aurafeed — Auth Screen (Switcher)
 *
 * Owns the typed email (so Sign in ↔ Sign up does not wipe it) and the
 * mode, saved by name so rotation is portable across Android / iOS / Desktop.
 */
@Composable
fun AuthScreen(
    initialMode: AuthMode,
    onSignInWithEmail: suspend (String, String) -> AuthUiState<AuthSession>,
    onSignInWithGoogle: suspend () -> AuthUiState<AuthSession>,
    onSignUpWithEmail: suspend (String, String) -> AuthUiState<AuthSession>,
    onSignUpWithGoogle: suspend () -> AuthUiState<AuthSession>,
    onForgotPassword: (String) -> Unit,
    onAuthSuccess: (AuthSession) -> Unit,
    initialEmail: String = "",
) {
    var modeName: String by rememberSaveable { mutableStateOf(initialMode.name) }
    var email: String by rememberSaveable { mutableStateOf(initialEmail) }

    val showSignUp: Boolean = modeName == AuthMode.SignUp.name

    Crossfade(
        targetState = showSignUp,
        animationSpec = tween(AurafeedMotion.Medium),
        label = "auth-mode",
    ) { signUp ->
        if (signUp) {
            RegisterScreen(
                email = email,
                onEmailChange = { email = it },
                onSignUpWithEmail = onSignUpWithEmail,
                onSignUpWithGoogle = onSignUpWithGoogle,
                onSwitchToLogin = { modeName = AuthMode.SignIn.name },
                onAuthSuccess = onAuthSuccess,
            )
        } else {
            LoginScreen(
                email = email,
                onEmailChange = { email = it },
                onSignInWithEmail = onSignInWithEmail,
                onSignInWithGoogle = onSignInWithGoogle,
                onForgotPassword = onForgotPassword,
                onSwitchToRegister = { modeName = AuthMode.SignUp.name },
                onAuthSuccess = onAuthSuccess,
            )
        }
    }
}
