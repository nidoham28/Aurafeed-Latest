package com.nidoham.aurafeed.features.auth.screen

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.nidoham.aurafeed.features.auth.state.AuthMode
import com.nidoham.aurafeed.features.auth.state.AuthSession
import com.nidoham.aurafeed.features.auth.state.AuthUiState

/**
 * Aurafeed — Auth Screen (Switcher)
 * ───────────────────────────────────────────────────────────
 *  Parent composable that toggles between [LoginScreen] and [RegisterScreen].
 *
 *  Two things it owns on purpose:
 *   • The typed email. Someone who mistypes their way into "no account
 *     found", taps Create an account, and has to retype their address has
 *     been punished for our error message. Keep it.
 *   • The mode, in savable form, so a rotation doesn't bounce a half-filled
 *     sign-up back to sign-in.
 *
 *  Modes are stored by name because enum saving isn't portable across all
 *  Compose targets (Android / iOS / desktop).
 *
 *  The auth Edge Functions arrive as suspending lambdas so the platform layer
 *  injects the real implementation and the common code stays UI-only.
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

    // ForgotPassword and VerifyEmail are separate routes in NavigationManager;
    // anything that isn't SignUp renders sign-in here.
    val showSignUp: Boolean = modeName == AuthMode.SignUp.name

    Crossfade(targetState = showSignUp, label = "auth-mode") { signUp ->
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