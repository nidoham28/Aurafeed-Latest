package com.nidoham.aurafeed.features.auth.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
import com.nidoham.aurafeed.features.auth.state.AuthAction
import com.nidoham.aurafeed.features.auth.state.AuthFieldError
import com.nidoham.aurafeed.features.auth.state.AuthSession
import com.nidoham.aurafeed.features.auth.state.AuthStrings
import com.nidoham.aurafeed.features.auth.state.AuthUiState
import com.nidoham.aurafeed.features.auth.state.PASSWORD_MIN_LENGTH
import com.nidoham.aurafeed.features.auth.state.PasswordChecks
import com.nidoham.aurafeed.features.auth.state.isBusy
import com.nidoham.aurafeed.features.auth.state.isLoading
import com.nidoham.aurafeed.features.auth.state.passwordChecks
import com.nidoham.aurafeed.features.auth.state.validateEmail
import com.nidoham.aurafeed.features.auth.state.validatePassword
import com.nidoham.aurafeed.features.shell.screen.ShellScaffold
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import kotlinx.coroutines.launch

/**
 * Aurafeed — Register Screen
 * ───────────────────────────────────────────────────────────
 *  Email + Google sign-up. No username, no confirm password (per spec).
 *
 *  The one thing this screen does that Login doesn't: it shows the password
 *  rules as a live checklist while you type, instead of waiting for blur and
 *  then rejecting you. Rules you can't see are rules you fail.
 *
 *  Per project rule: never trust the client — all writes go through
 *  Supabase Edge Functions (`supabase/functions/auth/signup`, `.../google`).
 *  Device evidence is stored on first signup so a later login from another
 *  device raises a security alert.
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
    // While the checklist is on screen it already says what's missing —
    // don't shout the same thing twice in red.
    val passwordError: AuthFieldError? =
        if (passwordTouched && password.isEmpty()) AuthFieldError.PasswordRequired else null
    val busy: Boolean = uiState.isBusy

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

    ShellScaffold(modifier = modifier) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = tokens.spacing.lg, vertical = tokens.spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
        ) {
            Column(
                modifier = Modifier.widthIn(max = 420.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
            ) {
                AuthBrandHeader(
                    title = "Create your aura",
                    subtitle = "Join Aurafeed in seconds.",
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
                    isNewPassword = true,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { submitEmail() }),
                    focusRequester = passwordFocus,
                    onFocusLost = { if (password.isNotEmpty()) passwordTouched = true },
                )

                PasswordChecklist(
                    checks = checks,
                    visible = password.isNotEmpty() && !checks.allMet,
                )

                Spacer(modifier = Modifier.height(tokens.spacing.xs))

                when (val state = uiState) {
                    is AuthUiState.Error -> {
                        AuthErrorBanner(message = AuthStrings.forKey(state.messageKey))
                        Spacer(modifier = Modifier.height(tokens.spacing.xs))
                    }
                    else -> {}
                }

                AuthSubmitButton(
                    text = "Create account",
                    loading = uiState.isLoading(AuthAction.Email),
                    enabled = !busy,
                    onClick = { submitEmail() },
                    loadingLabel = "Creating your account",
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
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PASSWORD CHECKLIST
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PasswordChecklist(
    checks: PasswordChecks,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens
    AnimatedVisibility(visible = visible) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = tokens.spacing.sm)
                // The field's own supporting text carries this for screen
                // readers; announcing each row on every keystroke is noise.
                .clearAndSetSemantics { },
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
        ) {
            ChecklistRow("At least $PASSWORD_MIN_LENGTH characters", checks.longEnough)
            ChecklistRow("A letter", checks.hasLetter)
            ChecklistRow("A number", checks.hasDigit)
        }
    }
}

@Composable
private fun ChecklistRow(text: String, met: Boolean) {
    val tokens = AurafeedTheme.tokens
    val color = if (met) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = if (met) color else color.copy(alpha = 0.35f),
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────────────────────

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