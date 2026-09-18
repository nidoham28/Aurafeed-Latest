package com.nidoham.aurafeed.features.auth.component

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.features.auth.state.AuthFieldError
import com.nidoham.aurafeed.features.auth.state.AuthStrings
import com.nidoham.aurafeed.shared.component.shimmer.ShimmerLine
import com.nidoham.aurafeed.shared.component.shimmer.ShimmerStyle
import com.nidoham.aurafeed.ui.theme.AurafeedTheme

/**
 * Aurafeed — Shared Auth Components
 * ───────────────────────────────────────────────────────────
 *  Reusable building blocks for the Login + Register screens.
 *
 *  UX contracts these components enforce so the screens can't break them:
 *   • Supporting text is ALWAYS present (error or helper or blank) so the
 *     field never changes height mid-typing and shoves the button away.
 *   • Errors are resolved through [AuthStrings] — a raw key can never reach
 *     the screen.
 *   • Every tappable thing is at least 48dp; links are TextButtons, not
 *     ClickableText (which is deprecated, has no button role, and gives
 *     screen readers nothing to announce).
 *   • Shimmer for loading — never a plain spinner (project rule).
 *
 *  Note: [ContentType] autofill semantics need Compose UI 1.8+. If you're on
 *  an older BOM, delete the two `.semantics { contentType = ... }` lines and
 *  the matching imports; nothing else depends on them.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 0. INTERNAL — one shared supporting-text slot
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun fieldSupportingText(
    error: AuthFieldError?,
    helper: String?,
): @Composable () -> Unit = {
    val text = error?.let { AuthStrings.forError(it) } ?: helper.orEmpty()
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = if (error != null) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
    )
}

@Composable
private fun authFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
)

// ─────────────────────────────────────────────────────────────────────────────
// 1. BRAND HEADER — wordmark + tagline
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthBrandHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AurafeedTheme.tokens.spacing.xs),
    ) {
        Text(
            text = "Aurafeed",
            style = typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            // The screen title is the page heading for assistive tech — the
            // wordmark is decoration, this is the thing that says where you are.
            text = title,
            style = typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = subtitle,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. EMAIL FIELD
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: AuthFieldError? = null,
    helper: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    onFocusLost: () -> Unit = {},
) {
    var wasFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .onFocusChanged { state ->
                if (wasFocused && !state.isFocused) onFocusLost()
                wasFocused = state.isFocused
            }
            .semantics { contentType = ContentType.EmailAddress },
        enabled = enabled,
        singleLine = true,
        label = { Text("Email") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                modifier = Modifier.size(AurafeedTheme.tokens.sizes.iconMd),
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
            autoCorrectEnabled = false,
        ),
        keyboardActions = keyboardActions,
        isError = error != null,
        supportingText = fieldSupportingText(error, helper),
        shape = RoundedCornerShape(AurafeedTheme.tokens.corners.sm),
        colors = authFieldColors(),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. PASSWORD FIELD
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisible: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    error: AuthFieldError? = null,
    helper: String? = null,
    enabled: Boolean = true,
    isNewPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    onFocusLost: () -> Unit = {},
) {
    var wasFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .onFocusChanged { state ->
                if (wasFocused && !state.isFocused) onFocusLost()
                wasFocused = state.isFocused
            }
            .semantics {
                // NewPassword tells the password manager to offer to generate
                // and save, instead of trying to fill an existing credential.
                contentType = if (isNewPassword) ContentType.NewPassword else ContentType.Password
            },
        enabled = enabled,
        singleLine = true,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(AurafeedTheme.tokens.sizes.iconMd),
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onToggleVisible,
                enabled = enabled,
                modifier = Modifier.sizeIn(
                    minWidth = AurafeedTheme.tokens.sizes.touchTargetMin,
                    minHeight = AurafeedTheme.tokens.sizes.touchTargetMin,
                ),
            ) {
                Icon(
                    imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password",
                    modifier = Modifier.size(AurafeedTheme.tokens.sizes.iconMd),
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
            autoCorrectEnabled = false,
        ),
        keyboardActions = keyboardActions,
        isError = error != null,
        supportingText = fieldSupportingText(error, helper),
        shape = RoundedCornerShape(AurafeedTheme.tokens.corners.sm),
        colors = authFieldColors(),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. SUBMIT BUTTON — primary CTA, shimmer replaces text while loading
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthSubmitButton(
    text: String,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loadingLabel: String = "Working",
) {
    val tokens = AurafeedTheme.tokens
    Button(
        onClick = onClick,
        // Deliberately NOT disabled on an invalid form: a dead button with no
        // explanation is the single worst thing in a sign-in screen. Tapping
        // it reveals the field errors instead.
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(tokens.sizes.touchTargetMin)
            .semantics { if (loading) stateDescription = loadingLabel },
        shape = RoundedCornerShape(tokens.corners.sm),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
        ),
    ) {
        if (loading) {
            ShimmerLine(width = 96.dp, height = 14.dp, style = ShimmerStyle.Light)
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. GOOGLE BUTTON
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthGoogleButton(
    text: String,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val tokens = AurafeedTheme.tokens
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(tokens.sizes.touchTargetMin)
            .semantics { if (loading) stateDescription = "Working" },
        shape = RoundedCornerShape(tokens.corners.sm),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        if (loading) {
            ShimmerLine(width = 120.dp, height = 14.dp, style = ShimmerStyle.Light)
        } else {
            GoogleLogoMark(size = 18.dp)
            Spacer(modifier = Modifier.width(tokens.spacing.sm))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * Placeholder "G" mark. The white disc now carries a hairline border so it
 * doesn't disappear against a light surface. Swap for the official asset
 * before store review — Google's brand terms require the real mark.
 */
@Composable
private fun GoogleLogoMark(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "G",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF4285F4),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. ERROR BANNER — announced, iconified, sits next to the action that failed
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens
    Surface(
        modifier = modifier
            .fillMaxWidth()
            // Assertive: the user just tapped a button and nothing happened;
            // they need to hear why immediately.
            .semantics { liveRegion = LiveRegionMode.Assertive },
        shape = RoundedCornerShape(tokens.corners.sm),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = tokens.spacing.md,
                vertical = tokens.spacing.sm,
            ),
            horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(tokens.sizes.iconMd),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. DIVIDER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthOrDivider(
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md),
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        Text(
            text = "or",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. LINKS — real buttons with real touch targets
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    emphasised: Boolean = false,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.sizeIn(minHeight = AurafeedTheme.tokens.sizes.touchTargetMin),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = AurafeedTheme.tokens.spacing.sm,
            vertical = AurafeedTheme.tokens.spacing.xs,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasised) FontWeight.SemiBold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/** "Don't have an account? Sign up" — prompt + link, centred, one touch target. */
@Composable
fun AuthFooterPrompt(
    prompt: String,
    linkText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = prompt,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AuthTextLink(
            text = linkText,
            onClick = onClick,
            enabled = enabled,
            emphasised = true,
        )
    }
}