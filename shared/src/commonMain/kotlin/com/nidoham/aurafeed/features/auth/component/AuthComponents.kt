package com.nidoham.aurafeed.features.auth.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as GeometrySize
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.features.auth.state.AuthFieldError
import com.nidoham.aurafeed.features.auth.state.AuthStrings
import com.nidoham.aurafeed.features.auth.state.PASSWORD_MIN_LENGTH
import com.nidoham.aurafeed.features.auth.state.PasswordChecks
import com.nidoham.aurafeed.features.auth.state.metCount
import com.nidoham.aurafeed.shared.component.shimmer.ShimmerLine
import com.nidoham.aurafeed.shared.component.shimmer.ShimmerStyle
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import com.nidoham.aurafeed.ui.theme.ErrorDefault
import com.nidoham.aurafeed.ui.theme.SuccessDefault
import com.nidoham.aurafeed.ui.theme.TextOnBrandMin
import com.nidoham.aurafeed.ui.theme.WarningDefault

/**
 * Aurafeed — Shared Auth Components (v4)
 *
 * Visual language:
 *  • Soft inset fields with a 4dp violet focus halo (not a naked outline).
 *  • Form sits in a bordered, elevated [AuthFormCard] — 24dp corners.
 *  • Primary CTA is the Aura gradient; disabled is a dim solid so it
 *    never looks tappable.
 *  • Password strength is three segments (weak / almost / strong),
 *    not a single bar that hides progress.
 *  • Compact brand header on mobile; desktop brand lives on the ad pane.
 */

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
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.55f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.32f),
    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.18f),
    errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.28f),
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
    errorIndicatorColor = MaterialTheme.colorScheme.error,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

private val AuthControlCorner
    @Composable get() = RoundedCornerShape(AurafeedTheme.tokens.corners.md)

@Composable
fun AuthFormCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tokens = AurafeedTheme.tokens
    val desktop = AurafeedTheme.isDesktop
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(tokens.corners.xl),
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.92f),
        tonalElevation = if (desktop) 2.dp else 1.dp,
        shadowElevation = if (desktop) 16.dp else 6.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = tokens.spacing.xl,
                vertical = tokens.spacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
            content = content,
        )
    }
}

@Composable
fun AuthLogoMark(size: Dp, modifier: Modifier = Modifier) {
    val glyphSize = with(LocalDensity.current) { (size * 0.46f).toSp() }
    val brandTint = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .shadow(
                elevation = size * 0.16f,
                shape = CircleShape,
                ambientColor = brandTint.copy(alpha = 0.45f),
                spotColor = brandTint.copy(alpha = 0.55f),
            )
            .size(size)
            .clip(CircleShape)
            .background(AurafeedTheme.brushes.auraHero)
            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "a",
            color = Color.White,
            fontSize = glyphSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * Mobile header. Compact row (logo + wordmark) then the page title —
 * the previous stacked 56dp mark + gradient wordmark + title + subtitle
 * ate too much vertical space before the form.
 */
@Composable
fun AuthBrandHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val tokens = AurafeedTheme.tokens
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
        ) {
            AuthLogoMark(size = 36.dp)
            Text(
                text = "Aurafeed",
                style = typography.titleLarge.copy(
                    brush = AurafeedTheme.brushes.auraHero,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        Spacer(modifier = Modifier.height(tokens.spacing.sm))
        Text(
            text = title,
            style = typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = subtitle,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: AuthFieldError? = null,
    helper: String? = null,
    suggestion: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    onFocusLost: () -> Unit = {},
) {
    var wasFocused by remember { mutableStateOf(false) }
    val tokens = AurafeedTheme.tokens

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
        placeholder = { Text("you@email.com") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                modifier = Modifier.size(tokens.sizes.iconMd),
            )
        },
        trailingIcon = {
            if (value.isNotEmpty() && enabled) {
                IconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier.sizeIn(
                        minWidth = tokens.sizes.touchTargetMin,
                        minHeight = tokens.sizes.touchTargetMin,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear email",
                        modifier = Modifier.size(tokens.sizes.iconMd),
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
            autoCorrectEnabled = false,
        ),
        keyboardActions = keyboardActions,
        isError = error != null,
        supportingText = {
            when {
                error != null -> Text(
                    text = AuthStrings.forError(error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
                suggestion != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Did you mean ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(
                            role = Role.Button,
                            onClickLabel = "Use suggested email $suggestion",
                        ) { onValueChange(suggestion) },
                    )
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                helper != null -> Text(
                    text = helper,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                else -> Text(text = "", style = MaterialTheme.typography.bodySmall)
            }
        },
        shape = AuthControlCorner,
        colors = authFieldColors(),
    )
}

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
    val tokens = AurafeedTheme.tokens

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
                contentType = if (isNewPassword) ContentType.NewPassword else ContentType.Password
            },
        enabled = enabled,
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(if (isNewPassword) "Create a password" else "Your password") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(tokens.sizes.iconMd),
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onToggleVisible,
                enabled = enabled,
                modifier = Modifier.sizeIn(
                    minWidth = tokens.sizes.touchTargetMin,
                    minHeight = tokens.sizes.touchTargetMin,
                ),
            ) {
                Icon(
                    imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password",
                    modifier = Modifier.size(tokens.sizes.iconMd),
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
        shape = AuthControlCorner,
        colors = authFieldColors(),
    )
}

@Composable
fun AuthPasswordStrength(
    checks: PasswordChecks,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(tween(AurafeedMotion.Short)) +
                fadeIn(tween(AurafeedMotion.Short)),
        exit = shrinkVertically(tween(AurafeedMotion.Short)) +
                fadeOut(tween(AurafeedMotion.Short)),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = tokens.spacing.xs, bottom = tokens.spacing.xs)
                .clearAndSetSemantics { },
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
        ) {
            val met = checks.metCount
            val meterColor by animateColorAsState(
                targetValue = when {
                    met >= 3 -> SuccessDefault
                    met == 2 -> WarningDefault
                    met == 1 -> ErrorDefault
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                animationSpec = tween(AurafeedMotion.ShortSlow),
                label = "password-strength-color",
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Password strength",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = strengthWord(met),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = meterColor,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (i < met) meterColor
                                else MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs)) {
                ChecklistRow("At least $PASSWORD_MIN_LENGTH characters", checks.longEnough)
                ChecklistRow("A letter", checks.hasLetter)
                ChecklistRow("A number", checks.hasDigit)
            }
        }
    }
}

private fun strengthWord(met: Int): String = when {
    met >= 3 -> "Strong"
    met == 2 -> "Almost there"
    met == 1 -> "Weak"
    else -> ""
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
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (met) MaterialTheme.colorScheme.primary else Color.Transparent)
                .border(
                    1.dp,
                    if (met) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = if (met) TextOnBrandMin else color.copy(alpha = 0.35f),
                modifier = Modifier.size(9.dp),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

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
    val shape = AuthControlCorner
    val backgroundBrush: Brush = if (enabled && !loading) {
        AurafeedTheme.brushes.auraHero
    } else {
        SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
    }

    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(tokens.sizes.touchTargetMin)
            .background(brush = backgroundBrush, shape = shape)
            .semantics { if (loading) stateDescription = loadingLabel },
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextOnBrandMin,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextOnBrandMin.copy(alpha = 0.85f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp,
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
            Spacer(modifier = Modifier.width(tokens.spacing.xs))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

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
        shape = AuthControlCorner,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.28f),
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

@Composable
private fun GoogleLogoMark(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size * 0.74f)) {
            val strokeWidth = this.size.minDimension * 0.34f
            val radius = (this.size.minDimension - strokeWidth) / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val arcSize = GeometrySize(radius * 2f, radius * 2f)
            val topLeft = Offset(center.x - radius, center.y - radius)

            drawArc(
                color = Color(0xFF4285F4),
                startAngle = -50f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )
            drawArc(
                color = Color(0xFF34A853),
                startAngle = 50f,
                sweepAngle = 80f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )
            drawArc(
                color = Color(0xFFFBBC05),
                startAngle = 130f,
                sweepAngle = 80f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )
            drawArc(
                color = Color(0xFFEA4335),
                startAngle = 210f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )
            drawRect(
                color = Color(0xFF4285F4),
                topLeft = Offset(center.x - strokeWidth * 0.1f, center.y - strokeWidth / 2f),
                size = GeometrySize(radius + strokeWidth * 0.6f, strokeWidth),
            )
        }
    }
}

@Composable
fun AuthErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Assertive },
        shape = RoundedCornerShape(tokens.corners.sm),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
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

@Composable
fun AuthOrDivider(
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = tokens.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md),
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        )
        Text(
            text = "or",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        )
    }
}

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
        contentPadding = PaddingValues(
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

@Composable
fun AuthTrustRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = AurafeedTheme.tokens.spacing.md),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Protected sign-in  ·  unknown devices flagged",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
