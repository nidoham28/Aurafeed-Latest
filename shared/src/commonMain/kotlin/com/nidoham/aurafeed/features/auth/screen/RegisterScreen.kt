package com.nidoham.aurafeed.features.auth.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nidoham.aurafeed.features.auth.component.AuthEmailField
import com.nidoham.aurafeed.features.auth.component.AuthErrorBanner
import com.nidoham.aurafeed.features.auth.component.AuthFooterPrompt
import com.nidoham.aurafeed.features.auth.component.AuthGoogleButton
import com.nidoham.aurafeed.features.auth.component.AuthOrDivider
import com.nidoham.aurafeed.features.auth.component.AuthPasswordField
import com.nidoham.aurafeed.features.auth.component.AuthPasswordStrength
import com.nidoham.aurafeed.features.auth.component.AuthSubmitButton
import com.nidoham.aurafeed.features.auth.component.AuthTrustRow
import com.nidoham.aurafeed.features.auth.state.AuthAction
import com.nidoham.aurafeed.features.auth.state.AuthFieldError
import com.nidoham.aurafeed.features.auth.state.AuthSession
import com.nidoham.aurafeed.features.auth.state.AuthStrings
import com.nidoham.aurafeed.features.auth.state.AuthUiState
import com.nidoham.aurafeed.features.auth.state.PasswordChecks
import com.nidoham.aurafeed.features.auth.state.isBusy
import com.nidoham.aurafeed.features.auth.state.isLoading
import com.nidoham.aurafeed.features.auth.state.passwordChecks
import com.nidoham.aurafeed.features.auth.state.suggestEmailFix
import com.nidoham.aurafeed.features.auth.state.validateEmail
import com.nidoham.aurafeed.features.auth.state.validatePassword
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import kotlinx.coroutines.launch

/**
 * Aurafeed — Register Screen (v4)
 *
 * Live strength meter + checklist while typing. No username, no confirm
 * password. Layout / ads / card live in [AuthLayout].
 */
@Composable
fun RegisterScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSignUpWithEmail: suspend (String, String) -> AuthUiState<AuthSession>,
    onSignUpWithGoogle: suspend () -> AuthUiState<AuthSession>,
    onSwitchToLogin: () -> Unit,
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

    val checks: PasswordChecks = passwordChecks(password)
    val emailError: AuthFieldError? = if (emailTouched) validateEmail(email) else null
    val passwordError: AuthFieldError? =
        if (passwordTouched && password.isEmpty()) AuthFieldError.PasswordRequired else null
    val busy: Boolean = uiState.isBusy

    val emailSuggestion: String? = remember(email, emailTouched) {
        if (emailTouched && validateEmail(email) == null) suggestEmailFix(email) else null
    }

    fun clearServerError() {
        if (uiState is AuthUiState.Error) uiState = AuthUiState.Idle
    }

    fun submitEmail() {
        if (busy) return
        focusManager.clearFocus()
        emailTouched = true
        passwordTouched = true

        when {
            validateEmail(email) != null -> {
                emailFocus.requestFocus()
                return
            }
            validatePassword(password) != null -> {
                passwordFocus.requestFocus()
                return
            }
        }

        scope.launch {
            uiState = AuthUiState.Loading(AuthAction.Email)
            val result = onSignUpWithEmail(email.trim(), password)
            uiState = result
            if (result is AuthUiState.Success) onAuthSuccess(result.data)
        }
    }

    fun submitGoogle() {
        if (busy) return
        focusManager.clearFocus()
        scope.launch {
            uiState = AuthUiState.Loading(AuthAction.Google)
            val result = onSignUpWithGoogle()
            uiState = result
            if (result is AuthUiState.Success) onAuthSuccess(result.data)
        }
    }

    AuthLayout(
        title = "Create account",
        subtitle = "Join Aurafeed to explore new stories.",
        modifier = modifier,
        footer = {
            AuthFooterPrompt(
                prompt = "Already have an account?",
                linkText = "Sign in",
                onClick = onSwitchToLogin,
                enabled = !busy,
            )
            Text(
                text = "By continuing you agree to Aurafeed's Terms of Service and Privacy Policy.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = tokens.spacing.lg),
            )
        },
    ) {
        AuthEmailField(
            value = email,
            onValueChange = {
                onEmailChange(it)
                clearServerError()
            },
            error = emailError,
            suggestion = emailSuggestion,
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
            isNewPassword = true,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { submitEmail() }),
            focusRequester = passwordFocus,
            onFocusLost = { if (password.isNotEmpty()) passwordTouched = true },
        )

        AuthPasswordStrength(
            checks = checks,
            visible = password.isNotEmpty(),
        )

        AnimatedVisibility(
            visible = uiState is AuthUiState.Error,
            enter = expandVertically(tween(AurafeedMotion.MediumFast)) +
                    fadeIn(tween(AurafeedMotion.MediumFast)),
            exit = shrinkVertically(tween(AurafeedMotion.Short)) +
                    fadeOut(tween(AurafeedMotion.Short)),
        ) {
            AuthErrorBanner(
                message = AuthStrings.forKey(
                    (uiState as? AuthUiState.Error)?.messageKey
                        ?: "auth.error.unknown",
                ),
            )
        }

        AuthSubmitButton(
            text = "Create account",
            loading = uiState.isLoading(AuthAction.Email),
            enabled = !busy,
            onClick = { submitEmail() },
            loadingLabel = "Creating your account",
        )

        AuthOrDivider()

        AuthGoogleButton(
            text = "Continue with Google",
            loading = uiState.isLoading(AuthAction.Google),
            enabled = !busy,
            onClick = { submitGoogle() },
        )

        AuthTrustRow()
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    AurafeedTheme(formFactor = AurafeedFormFactor.Mobile) {
        var email by remember { mutableStateOf("") }
        RegisterScreen(
            email = email,
            onEmailChange = { email = it },
            onSignUpWithEmail = { _, _ -> AuthUiState.Idle },
            onSignUpWithGoogle = { AuthUiState.Idle },
            onSwitchToLogin = {},
            onAuthSuccess = {},
        )
    }
}
