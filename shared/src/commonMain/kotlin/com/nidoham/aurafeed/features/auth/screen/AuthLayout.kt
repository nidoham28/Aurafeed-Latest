package com.nidoham.aurafeed.features.auth.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.features.auth.component.AuthAdPanel
import com.nidoham.aurafeed.features.auth.component.AuthBrandHeader
import com.nidoham.aurafeed.features.auth.component.AuthFormCard
import com.nidoham.aurafeed.features.shell.screen.ShellScaffold
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme

/**
 * Aurafeed — Auth Layout (v4)
 *
 *  • Compact (< 840dp): full-width login. No ads. Compact brand header,
 *    form card, then footer. Scroll + IME padding.
 *
 *  • Expanded (≥ 840dp): exact 50/50 split.
 *      Left  — campaign ads ([AuthAdPanel])
 *      Right — heading + form card, centred, max 440dp.
 */
@Composable
fun AuthLayout(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit = {},
    formContent: @Composable ColumnScope.() -> Unit,
) {
    val tokens = AurafeedTheme.tokens
    val appear = remember { MutableTransitionState(false) }

    LaunchedEffect(Unit) { appear.targetState = true }

    ShellScaffold(modifier = modifier) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val wideLayout = maxWidth >= 840.dp

            if (wideLayout) {
                Row(modifier = Modifier.fillMaxSize()) {
                    AuthAdPanel(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(authPaneBrush())
                            .verticalScroll(rememberScrollState())
                            .imePadding(),
                        contentAlignment = Alignment.Center,
                    ) {
                        this@Row.AnimatedVisibility(
                            visibleState = appear,
                            enter = fadeIn(tween(AurafeedMotion.MediumSlow)) +
                                    slideInVertically(tween(AurafeedMotion.MediumSlow)) { it / 8 },
                            exit = fadeOut(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 440.dp)
                                    .padding(
                                        horizontal = tokens.spacing.xxl,
                                        vertical = tokens.spacing.xxxl,
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                AuthFormHeading(title = title, subtitle = subtitle)
                                Spacer(Modifier.height(tokens.spacing.xl))
                                AuthFormCard(content = formContent)
                                Spacer(Modifier.height(tokens.spacing.md))
                                footer()
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(authPaneBrush())
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = tokens.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    AnimatedVisibility(
                        visibleState = appear,
                        enter = fadeIn(tween(AurafeedMotion.MediumSlow)) +
                                slideInVertically(tween(AurafeedMotion.MediumSlow)) { it / 10 },
                        exit = fadeOut(),
                    ) {
                        Column(
                            modifier = Modifier.widthIn(max = 420.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AuthBrandHeader(title = title, subtitle = subtitle)
                            Spacer(Modifier.height(tokens.spacing.lg))
                            AuthFormCard(content = formContent)
                            Spacer(Modifier.height(tokens.spacing.sm))
                            footer()
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun authPaneBrush(): Brush = Brush.verticalGradient(
    colors = listOf(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.background,
    ),
)

@Composable
private fun AuthFormHeading(
    title: String,
    subtitle: String,
) {
    val tokens = AurafeedTheme.tokens
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
