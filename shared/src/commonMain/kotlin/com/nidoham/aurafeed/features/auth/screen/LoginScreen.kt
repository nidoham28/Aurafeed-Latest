package com.nidoham.aurafeed.features.auth.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.features.auth.component.AuthBrandHeader
import com.nidoham.aurafeed.features.auth.component.AuthEmailField
import com.nidoham.aurafeed.features.auth.component.AuthErrorBanner
import com.nidoham.aurafeed.features.auth.component.AuthFooterPrompt
import com.nidoham.aurafeed.features.auth.component.AuthGoogleButton
import com.nidoham.aurafeed.features.auth.component.AuthOrDivider
import com.nidoham.aurafeed.features.auth.component.AuthPasswordField
import com.nidoham.aurafeed.features.auth.component.AuthSubmitButton
import com.nidoham.aurafeed.features.auth.component.AuthTextLink
import com.nidoham.aurafeed.features.auth.state.AuthAction
import com.nidoham.aurafeed.features.auth.state.AuthFieldError
import com.nidoham.aurafeed.features.auth.state.AuthSession
import com.nidoham.aurafeed.features.auth.state.AuthStrings
import com.nidoham.aurafeed.features.auth.state.AuthUiState
import com.nidoham.aurafeed.features.auth.state.isBusy
import com.nidoham.aurafeed.features.auth.state.isLoading
import com.nidoham.aurafeed.features.auth.state.validateEmail
import com.nidoham.aurafeed.features.auth.state.validateSignInPassword
import com.nidoham.aurafeed.features.shell.screen.ShellScaffold
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import kotlinx.coroutines.launch

/**
 * Aurafeed — Login Screen
 * ───────────────────────────────────────────────────────────
 *  Email + Google sign-in. No username, no confirm password (per spec).
 *
 *  UX behaviour worth knowing before you change anything:
 *   • [email] is hoisted to [AuthScreen] so switching to Sign up doesn't
 *     make the user retype it.
 *   • Password text is intentionally NOT saved across process death.
 *   • Field errors appear on blur, not on every keystroke, and never before
 *     the user has left the field once.
 *   • The submit button stays tappable on an invalid form; tapping reveals
 *     the errors and moves focus to the first bad field.
 *   • Only the button you tapped shimmers — the other stays legible.
 *
 *  Per project rule: never trust the client — all writes go through
 *  Supabase Edge Functions (`supabase/functions/auth/signin`, `.../google`).
 *  Device evidence is attached by the platform layer.
 */

@Composable
fun LoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSignInWithEmail: suspend (String, String) -> AuthUiState<AuthSession>,
    onSignInWithGoogle: suspend () -> AuthUiState<AuthSession>,
    onForgotPassword: (String) -> Unit,
    onSwitchToRegister: () -> Unit,
    onAuthSuccess: (AuthSession) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }

    var password: String by remember { mutableStateOf("") }
    var passwordVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    var emailTouched: Boolean by rememberSaveable { mutableStateOf(false) }
    var passwordTouched: Boolean by rememberSaveable { mutableStateOf(false) }
    var uiState: AuthUiState<AuthSession> by remember { mutableStateOf(AuthUiState.Idle) }

    val emailError: AuthFieldError? = if (emailTouched) validateEmail(email) else null
    val passwordError: AuthFieldError? = if (passwordTouched) validateSignInPassword(password) else null
    val busy: Boolean = uiState.isBusy

    fun clearServerError() {
        if (uiState is AuthUiState.Error) uiState = AuthUiState.Idle
    }

    fun submitEmail() {
        if (busy) return
        focusManager.clearFocus()
        emailTouched = true
        passwordTouched = true

        // Reveal, then point at the first thing to fix.
        when {
            validateEmail(email) != null -> {
                emailFocus.requestFocus()
                return
            }
            validateSignInPassword(password) != null -> {
                passwordFocus.requestFocus()
                return
            }
        }

        scope.launch {
            uiState = AuthUiState.Loading(AuthAction.Email)
            val result = onSignInWithEmail(email.trim(), password)
            uiState = result
            if (result is AuthUiState.Success<AuthSession>) onAuthSuccess(result.data)
        }
    }

    fun submitGoogle() {
        if (busy) return
        focusManager.clearFocus()
        scope.launch {
            uiState = AuthUiState.Loading(AuthAction.Google)
            val result = onSignInWithGoogle()
            uiState = result
            if (result is AuthUiState.Success) onAuthSuccess(result.data)
        }
    }

    ShellScaffold(modifier = modifier) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                // imePadding sits on the scroll container, so the focused field
                // is pushed above the keyboard instead of behind it.
                .imePadding()
                .padding(horizontal = tokens.spacing.lg, vertical = tokens.spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
        ) {
            Column(
                // Caps the form on tablets/desktop; on phones it's a no-op.
                modifier = Modifier.widthIn(max = 420.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
            ) {
                AuthBrandHeader(
                    title = "Welcome back",
                    subtitle = "Sign in to continue to your aura.",
                )

                Spacer(modifier = Modifier.height(tokens.spacing.lg))

                AuthEmailField(
                    value = email,
                    onValueChange = {
                        onEmailChange(it)
                        clearServerError()
                    },
                    error = emailError,
                    enabled = !busy,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
                    focusRequester = emailFocus,
                    onFocusLost = { if (email.isNotEmpty()) emailTouched = true },
                )

                AuthPasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        clearServerError()
                    },
                    visible = passwordVisible,
                    onToggleVisible = { passwordVisible = !passwordVisible },
                    error = passwordError,
                    enabled = !busy,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { submitEmail() }),
                    focusRequester = passwordFocus,
                    onFocusLost = { if (password.isNotEmpty()) passwordTouched = true },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    AuthTextLink(
                        text = "Forgot password?",
                        // Carry the typed email through so the reset screen
                        // starts filled in.
                        onClick = { onForgotPassword(email.trim()) },
                        enabled = !busy,
                    )
                }

                // Error sits directly above the CTA that produced it.
                when (val state = uiState) {
                    is AuthUiState.Error -> {
                        AuthErrorBanner(message = AuthStrings.forKey(state.messageKey))
                        Spacer(modifier = Modifier.height(tokens.spacing.xs))
                    }
                    else -> {}
                }

                AuthSubmitButton(
                    text = "Sign in",
                    loading = uiState.isLoading(AuthAction.Email),
                    enabled = !busy,
                    onClick = { submitEmail() },
                    loadingLabel = "Signing in",
                )

                Spacer(modifier = Modifier.height(tokens.spacing.md))
                AuthOrDivider()
                Spacer(modifier = Modifier.height(tokens.spacing.md))

                AuthGoogleButton(
                    text = "Continue with Google",
                    loading = uiState.isLoading(AuthAction.Google),
                    enabled = !busy,
                    onClick = { submitGoogle() },
                )

                Spacer(modifier = Modifier.height(tokens.spacing.xl))

                AuthFooterPrompt(
                    prompt = "New to Aurafeed?",
                    linkText = "Create an account",
                    onClick = onSwitchToRegister,
                    enabled = !busy,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun LoginScreenPreview() {
    AurafeedTheme(formFactor = AurafeedFormFactor.Mobile) {
        var email by remember { mutableStateOf("") }
        LoginScreen(
            email = email,
            onEmailChange = { email = it },
            onSignInWithEmail = { _, _ -> AuthUiState.Idle },
            onSignInWithGoogle = { AuthUiState.Idle },
            onForgotPassword = {},
            onSwitchToRegister = {},
            onAuthSuccess = {},
        )
    }
}