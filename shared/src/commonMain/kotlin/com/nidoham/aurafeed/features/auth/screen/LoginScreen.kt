package com.nidoham.aurafeed.features.auth.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.tooling.preview.Preview
import com.nidoham.aurafeed.features.auth.component.AuthEmailField
import com.nidoham.aurafeed.features.auth.component.AuthErrorBanner
import com.nidoham.aurafeed.features.auth.component.AuthFooterPrompt
import com.nidoham.aurafeed.features.auth.component.AuthGoogleButton
import com.nidoham.aurafeed.features.auth.component.AuthOrDivider
import com.nidoham.aurafeed.features.auth.component.AuthPasswordField
import com.nidoham.aurafeed.features.auth.component.AuthSubmitButton
import com.nidoham.aurafeed.features.auth.component.AuthTextLink
import com.nidoham.aurafeed.features.auth.component.AuthTrustRow
import com.nidoham.aurafeed.features.auth.state.AuthAction
import com.nidoham.aurafeed.features.auth.state.AuthFieldError
import com.nidoham.aurafeed.features.auth.state.AuthSession
import com.nidoham.aurafeed.features.auth.state.AuthStrings
import com.nidoham.aurafeed.features.auth.state.AuthUiState
import com.nidoham.aurafeed.features.auth.state.isBusy
import com.nidoham.aurafeed.features.auth.state.isLoading
import com.nidoham.aurafeed.features.auth.state.suggestEmailFix
import com.nidoham.aurafeed.features.auth.state.validateEmail
import com.nidoham.aurafeed.features.auth.state.validateSignInPassword
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import kotlinx.coroutines.launch

/**
 * Aurafeed — Login Screen (v4)
 *
 * Layout, card, ads and entrance motion live in [AuthLayout].
 * This file is the form only: fields, CTA, Google, trust row.
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

    AuthLayout(
        title = "Welcome back",
        subtitle = "Sign in to continue to your aura.",
        modifier = modifier,
        footer = {
            AuthFooterPrompt(
                prompt = "New to Aurafeed?",
                linkText = "Create an account",
                onClick = onSwitchToRegister,
                enabled = !busy,
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
                onClick = { onForgotPassword(email.trim()) },
                enabled = !busy,
            )
        }

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
            text = "Sign in",
            loading = uiState.isLoading(AuthAction.Email),
            enabled = !busy,
            onClick = { submitEmail() },
            loadingLabel = "Signing in",
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
