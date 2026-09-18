package com.nidoham.aurafeed.features.splash.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.shared.component.shimmer.shimmer
import com.nidoham.aurafeed.shared.component.shimmer.ShimmerStyle
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import com.nidoham.aurafeed.ui.theme.LocalFormFactor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Aurafeed — Splash Screen
 * ───────────────────────────────────────────────────────────
 *  Production launch screen. Renders the brand identity with a
 *  properly orchestrated entrance animation, an always-on pulsing
 *  aura halo, and a shimmer loading bar — never a plain spinner.
 *
 *  Flow:
 *   1. Background aura gradient fades in.
 *   2. Aura halo (radial gradient orb) fades in and scales up, then
 *      pulses forever.
 *   3. "Aurafeed" wordmark slides up + fades in.
 *   4. "Your aura. Your feed." tagline slides up + fades in.
 *   5. Shimmer loading bar appears at the bottom.
 *   6. After a short hold, navigates onward based on [isAuthenticated].
 *
 *  [isAuthenticated] should be sourced from wherever this app's auth
 *  state actually lives (e.g. a ViewModel backed by a repository) —
 *  the default here is a placeholder only.
 *
 *  Per project rule: shimmer loading only — no CircularProgressIndicator.
 */

private const val HOLD_AFTER_ENTRANCE_MS = 2500L

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
) {
    val formFactor = LocalFormFactor.current
    val isDesktop = formFactor == AurafeedFormFactor.Desktop
    val brushes = AurafeedTheme.brushes
    val typography = MaterialTheme.typography

    // ── Staggered entrance animation state ─────────────────────────────
    val haloAlpha = remember { Animatable(0f) }
    val haloScale = remember { Animatable(0.85f) }
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffset = remember { Animatable(24f) }
    val taglineAlpha = remember { Animatable(0f) }
    val taglineOffset = remember { Animatable(16f) }
    val barAlpha = remember { Animatable(0f) }

    // ── Continuous halo pulse ──────────────────────────────────────────
    val pulseTransition = rememberInfiniteTransition(label = "auraPulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AurafeedMotion.XLong,
                easing = EaseOutCubic,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AurafeedMotion.XLong,
                easing = EaseOutCubic,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    // ── Drive the staggered entrance once, then hand off navigation ────
    // Each step's offset + alpha animate concurrently (not sequentially)
    // so elements visibly slide *while* fading in, rather than moving
    // invisibly into place and only then fading in.
    LaunchedEffect(isAuthenticated) {
        coroutineScope {
            launch {
                haloScale.animateTo(1.0f, tween(AurafeedMotion.Medium, easing = EaseOutCubic))
            }
            launch {
                haloAlpha.animateTo(1.0f, tween(AurafeedMotion.MediumFast, easing = LinearEasing))
            }
        }

        delay(120)
        coroutineScope {
            launch {
                wordmarkOffset.animateTo(0f, tween(AurafeedMotion.Medium, easing = EaseOutCubic))
            }
            launch {
                wordmarkAlpha.animateTo(1.0f, tween(AurafeedMotion.MediumFast, easing = LinearEasing))
            }
        }

        delay(100)
        coroutineScope {
            launch {
                taglineOffset.animateTo(0f, tween(AurafeedMotion.Medium, easing = EaseOutCubic))
            }
            launch {
                taglineAlpha.animateTo(1.0f, tween(AurafeedMotion.ShortSlow, easing = LinearEasing))
            }
        }

        delay(120)
        barAlpha.animateTo(1.0f, tween(AurafeedMotion.Short, easing = LinearEasing))

        // Brief hold so the brand is actually readable before we leave,
        // then hand off to the navigation graph.
        delay(HOLD_AFTER_ENTRANCE_MS)
        if (isAuthenticated) onNavigateToHome() else onNavigateToLogin()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brushes.auraHero),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            // Aura halo — fades + scales in, then pulses forever
            Box(
                modifier = Modifier
                    .size(if (isDesktop) 112.dp else 88.dp)
                    .graphicsLayer {
                        alpha = haloAlpha.value
                        val combinedScale = haloScale.value * pulseScale
                        scaleX = combinedScale
                        scaleY = combinedScale
                    }
                    .clip(CircleShape)
                    .background(brushes.auraGlow),
                contentAlignment = Alignment.Center,
            ) {
                // Inner core dot for a "spark" feel
                Box(
                    modifier = Modifier
                        .size(if (isDesktop) 24.dp else 18.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = pulseAlpha)),
                )
            }

            Spacer(modifier = Modifier.height(if (isDesktop) 32.dp else 24.dp))

            // Wordmark — slides up while fading in
            Text(
                text = "Aurafeed",
                style = typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = wordmarkAlpha.value
                        translationY = wordmarkOffset.value.dp.toPx()
                    }
                    .padding(bottom = 4.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline — slides up while fading in
            Text(
                text = "Your aura. Your feed.",
                style = typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = taglineAlpha.value
                    translationY = taglineOffset.value.dp.toPx()
                },
            )
        }

        // Loading bar pinned to bottom
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = if (isDesktop) 120.dp else 64.dp,
                    vertical = if (isDesktop) 48.dp else 36.dp,
                )
                .fillMaxWidth()
                .graphicsLayer { alpha = barAlpha.value },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .shimmer(
                        style = ShimmerStyle.Light,
                        shape = RoundedCornerShape(2.dp),
                    )
                    .background(color = Color.White.copy(alpha = 0.25f)),
            )
            Text(
                text = "Preparing your aura…",
                style = typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
        }
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