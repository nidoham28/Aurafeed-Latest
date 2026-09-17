package com.nidoham.aurafeed.shared.component.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import com.nidoham.aurafeed.ui.theme.ShimmerAuraBase
import com.nidoham.aurafeed.ui.theme.ShimmerAuraHigh
import com.nidoham.aurafeed.ui.theme.ShimmerDarkBase
import com.nidoham.aurafeed.ui.theme.ShimmerDarkHigh
import com.nidoham.aurafeed.ui.theme.ShimmerLightBase
import com.nidoham.aurafeed.ui.theme.ShimmerLightHigh

/**
 * Aurafeed — Shimmer Primitive
 * ───────────────────────────────────────────────────────────
 *  Per project rule: NO plain spinners / simple loaders anywhere —
 *  every loading state in the app must use shimmer skeletons.
 *
 *  This file exposes:
 *
 *   • [shimmer]      — extension to apply shimmer to any Composable
 *   • [ShimmerBox]            — generic block placeholder
 *   • [ShimmerCircle]         — avatar placeholder
 *   • [ShimmerLine]           — text line placeholder
 *   • [ShimmerCard]           — composed card (avatar + 2 lines + body block)
 *
 *  Tuning:
 *   • Sweep period = [AurafeedMotion.ShimmerPeriod] (1100ms)
 *   • Easing = LinearEasing (constant sweep, no jerk)
 *   • Color picked from theme: Light / Dark / Aura variants in [Color.kt]
 *   • Sweep direction: left-to-right; sweep length = 3× surface width
 *     so highlight is fully off-screen before wrapping
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. STYLE — color choice (auto-resolves from theme)
// ─────────────────────────────────────────────────────────────────────────────

@Stable
enum class ShimmerStyle {
    /** Auto: light base for light theme, dark base for dark theme. */
    Auto,

    /** Always light base (for use on tinted/dark backgrounds regardless of theme). */
    Light,

    /** Always dark base. */
    Dark,

    /** Aura-tinted base — use on brand/gradient backgrounds. */
    Aura,
}

@Composable
private fun resolveShimmerColors(style: ShimmerStyle): Pair<Color, Color> {
    val isDark: Boolean = AurafeedTheme.isDark
    val effective: ShimmerStyle = when (style) {
        ShimmerStyle.Auto -> if (isDark) ShimmerStyle.Dark else ShimmerStyle.Light
        else -> style
    }
    val base: Color = when (effective) {
        ShimmerStyle.Light -> ShimmerLightBase
        ShimmerStyle.Dark -> ShimmerDarkBase
        ShimmerStyle.Aura -> ShimmerAuraBase
        ShimmerStyle.Auto -> ShimmerLightBase  // unreachable (handled above)
    }
    val highlight: Color = when (effective) {
        ShimmerStyle.Light -> ShimmerLightHigh
        ShimmerStyle.Dark -> ShimmerDarkHigh
        ShimmerStyle.Aura -> ShimmerAuraHigh
        ShimmerStyle.Auto -> ShimmerLightHigh
    }
    return base to highlight
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. MODIFIER EXTENSION — apply shimmer to any Composable
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Apply shimmer effect as a Modifier.
 *
 * @param style  color palette (Auto picks based on theme)
 * @param shape  clipping shape; defaults to 8dp rounded
 */
fun Modifier.shimmer(
    style: ShimmerStyle = ShimmerStyle.Auto,
    shape: Shape = RoundedCornerShape(8.dp),
): Modifier = composed {
    val (base: Color, highlight: Color) = resolveShimmerColors(style)

    val transition = rememberInfiniteTransition(label = "aurafeedShimmer")
    val progress: Float by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AurafeedMotion.ShimmerPeriod,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )

    this
        .clip(shape)
        .drawBehind {
            val w: Float = size.width
            val h: Float = size.height
            if (w <= 0f || h <= 0f) return@drawBehind
            // Sweep from -w to 2w so highlight fully exits before wrapping.
            val sweepStart: Float = -w + progress * 3f * w
            val sweepEnd: Float = sweepStart + w
            val brush: Brush = Brush.linearGradient(
                colors = listOf(base, highlight, base),
                start = Offset(sweepStart, 0f),
                end = Offset(sweepEnd, h),
            )
            drawRect(brush = brush)
        }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. PRIMITIVE BUILDING BLOCKS
// ─────────────────────────────────────────────────────────────────────────────

/** Generic rectangular shimmer block. */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    style: ShimmerStyle = ShimmerStyle.Auto,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmer(style, shape),
    )
}

/** Circular shimmer — for avatars, story rings, status dots. */
@Composable
fun ShimmerCircle(
    size: Dp,
    modifier: Modifier = Modifier,
    style: ShimmerStyle = ShimmerStyle.Auto,
) {
    ShimmerBox(
        modifier = modifier.size(size),
        style = style,
        shape = CircleShape,
    )
}

/** Single horizontal text-line shimmer. */
@Composable
fun ShimmerLine(
    width: Dp,
    height: Dp = 12.dp,
    modifier: Modifier = Modifier,
    style: ShimmerStyle = ShimmerStyle.Auto,
) {
    ShimmerBox(
        modifier = modifier
            .width(width)
            .height(height),
        style = style,
        shape = RoundedCornerShape(height / 2),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. COMPOSED SKELETONS — common multi-block patterns
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Card-shaped skeleton mimicking a feed post header (avatar + 2 lines).
 * Pair with [ShimmerBlock] for the post body below.
 */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    style: ShimmerStyle = ShimmerStyle.Auto,
    padding: PaddingValues = PaddingValues(16.dp),
) {
    Box(modifier = modifier.padding(padding)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerCircle(size = 40.dp, style = style)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ShimmerLine(width = 120.dp, height = 12.dp, style = style)
                ShimmerLine(width = 80.dp, height = 10.dp, style = style)
            }
        }
    }
}

/**
 * Square/rectangular block skeleton for media (post image, reel thumb).
 */
@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.0f,
    style: ShimmerStyle = ShimmerStyle.Auto,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio, matchHeightConstraintsFirst = false)
            .shimmer(style, RoundedCornerShape(0.dp)),
    )
}
