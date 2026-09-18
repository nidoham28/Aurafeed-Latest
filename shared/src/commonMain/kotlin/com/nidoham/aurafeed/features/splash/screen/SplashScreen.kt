package com.nidoham.aurafeed.features.splash.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import aurafeed.shared.generated.resources.Res
import aurafeed.shared.generated.resources.app_logo
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import com.nidoham.aurafeed.ui.theme.LocalFormFactor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

// NOTE: adjust this import to match your project's actual generated resources
// package (set via the Gradle `compose.resources { packageOfResClass = ... }`
// block). This is the conventional default for a base package of
// `com.nidoham.aurafeed`; Compose Multiplatform's resource plugin generates
// `Res.drawable.app_logo` for a vector at `composeResources/drawable/app_logo.xml`.

/**
 * Aurafeed — Splash Screen
 * ───────────────────────────────────────────────────────────
 *  Minimal production launch screen: the app logo, centered, and a small
 *  developer credit pinned to the bottom — nothing else. No wordmark, no
 *  tagline, no loading copy, no shimmer bar.
 *
 *  Flow:
 *   1. Logo fades in + scales up from a slightly smaller size.
 *   2. Developer credit fades in shortly after.
 *   3. After a short hold, navigates onward based on [isAuthenticated].
 *
 *  [isAuthenticated] should be sourced from wherever this app's auth state
 *  actually lives (e.g. a ViewModel backed by a repository) — the default
 *  here is a placeholder only.
 */

private const val HOLD_AFTER_ENTRANCE_MS = 1800L
private const val DEVELOPER_CREDIT = "Developed by Nidoham"

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
) {
    val formFactor = LocalFormFactor.current
    val isDesktop = formFactor == AurafeedFormFactor.Desktop
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.85f) }
    val creditAlpha = remember { Animatable(0f) }

    LaunchedEffect(isAuthenticated) {
        coroutineScope {
            launch {
                logoScale.animateTo(1f, tween(AurafeedMotion.Medium, easing = EaseOutCubic))
            }
            launch {
                logoAlpha.animateTo(1f, tween(AurafeedMotion.MediumFast, easing = LinearEasing))
            }
        }

        delay(150)
        creditAlpha.animateTo(1f, tween(AurafeedMotion.ShortSlow, easing = LinearEasing))

        // Brief hold so the brand is actually readable before we leave,
        // then hand off to the navigation graph.
        delay(HOLD_AFTER_ENTRANCE_MS)
        if (isAuthenticated) onNavigateToHome() else onNavigateToLogin()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.app_logo),
            contentDescription = "Aurafeed",
            modifier = Modifier
                .size(if (isDesktop) 200.dp else 160.dp)
                .graphicsLayer {
                    alpha = logoAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                },
        )

        Text(
            text = DEVELOPER_CREDIT,
            style = typography.labelSmall,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    PaddingValues(
                        horizontal = 24.dp,
                        vertical = if (isDesktop) 32.dp else 24.dp,
                    ),
                )
                .graphicsLayer { alpha = creditAlpha.value },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun SplashScreenPreview() {
    AurafeedTheme(formFactor = AurafeedFormFactor.Mobile) {
        SplashScreen(onNavigateToLogin = {}, onNavigateToHome = {})
    }
}