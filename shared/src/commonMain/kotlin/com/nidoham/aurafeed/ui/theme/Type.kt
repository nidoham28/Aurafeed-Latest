package com.nidoham.aurafeed.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

/**
 * Aurafeed — Typography Scale
 * ───────────────────────────────────────────────────────────
 *  Material 3 typography roles with tuned sizes, weights, line heights,
 *  and letter spacing.
 *
 *  Per project rule: Mobile and Desktop are separate variants — two
 *  [Typography] instances are exposed ([AuraMobileTypography] / [AuraDesktopTypography]),
 *  and [AurafeedTheme] injects the correct one based on [LocalFormFactor].
 *
 *  Font family is wired in [Theme.kt] via [LocalAurafeedFontFamily], so that
 *  platform-specific asset loading (Inter / NotoSans) stays out of this file.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. FONT FAMILY PROVIDER — implemented in Theme.kt (or Font.kt)
// ─────────────────────────────────────────────────────────────────────────────

val LocalAurafeedFontFamily = staticCompositionLocalOf { FontFamily.Default }

// ─────────────────────────────────────────────────────────────────────────────
// 2. LINE HEIGHT STYLE — Material 3 "tight" / "comfortable" defaults
// ─────────────────────────────────────────────────────────────────────────────

private val TightLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Top,
    trim = LineHeightStyle.Trim.None,
)

private val ComfortableLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both,
)

// ─────────────────────────────────────────────────────────────────────────────
// 3. MOBILE TYPOGRAPHY — Android / iOS, 4.5–6" screens
//    Tuned for single-column dense feeds; slightly tighter than desktop
// ─────────────────────────────────────────────────────────────────────────────

@Stable
fun mobileTypography(font: FontFamily = FontFamily.Default): Typography {
    val display = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        lineHeightStyle = TightLineHeight,
    )
    val headline = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        lineHeightStyle = TightLineHeight,
    )
    val title = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Medium,
        lineHeightStyle = TightLineHeight,
    )
    val body = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        lineHeightStyle = ComfortableLineHeight,
    )
    val label = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Medium,
        lineHeightStyle = TightLineHeight,
    )

    return Typography(
        displayLarge = display.copy(fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
        displayMedium = display.copy(fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp),
        displaySmall = display.copy(fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp),

        headlineLarge = headline.copy(fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp),
        headlineMedium = headline.copy(fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp),
        headlineSmall = headline.copy(fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp),

        titleLarge = title.copy(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
        titleMedium = title.copy(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp, fontWeight = FontWeight.Medium),
        titleSmall = title.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),

        bodyLarge = body.copy(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
        bodyMedium = body.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
        bodySmall = body.copy(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),

        labelLarge = label.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp, fontWeight = FontWeight.Medium),
        labelMedium = label.copy(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
        labelSmall = label.copy(fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. DESKTOP TYPOGRAPHY — Windows / macOS / Linux JVM
//    Slightly larger body sizes (users sit further from screen),
//    relaxed letter spacing, less aggressive weight on titles
// ─────────────────────────────────────────────────────────────────────────────

@Stable
fun desktopTypography(font: FontFamily = FontFamily.Default): Typography {
    val display = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        lineHeightStyle = TightLineHeight,
    )
    val headline = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        lineHeightStyle = ComfortableLineHeight,
    )
    val title = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Medium,
        lineHeightStyle = TightLineHeight,
    )
    val body = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        lineHeightStyle = ComfortableLineHeight,
    )
    val label = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Medium,
        lineHeightStyle = TightLineHeight,
    )

    return Typography(
        displayLarge = display.copy(fontSize = 64.sp, lineHeight = 72.sp, letterSpacing = (-0.5).sp),
        displayMedium = display.copy(fontSize = 52.sp, lineHeight = 60.sp, letterSpacing = (-0.25).sp),
        displaySmall = display.copy(fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = 0.sp),

        headlineLarge = headline.copy(fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp),
        headlineMedium = headline.copy(fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp),
        headlineSmall = headline.copy(fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp),

        titleLarge = title.copy(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold),
        titleMedium = title.copy(fontSize = 18.sp, lineHeight = 26.sp, letterSpacing = 0.15.sp),
        titleSmall = title.copy(fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp),

        bodyLarge = body.copy(fontSize = 17.sp, lineHeight = 26.sp, letterSpacing = 0.5.sp),
        bodyMedium = body.copy(fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.25.sp),
        bodySmall = body.copy(fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.4.sp),

        labelLarge = label.copy(fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp),
        labelMedium = label.copy(fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.5.sp),
        labelSmall = label.copy(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. CUSTOM TEXT STYLES — beyond Material 3 roles, used by Aurafeed UI
//    (kept here so screens don't reinvent their own)
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class AurafeedTextStyles(
    /** Profile @handle — mono-numeral, slightly tracked. */
    val handle: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp,
    ),

    /** Caption beneath post — body weight + larger leading. */
    val postCaption: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.15.sp,
    ),

    /** Comment body — tighter than post caption for vertical density. */
    val commentBody: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
    ),

    /** Story timestamp — pill label inside story. */
    val storyTimestamp: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
    ),

    /** Big engagement counter (e.g. profile followers). */
    val statNumber: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
    ),

    val statLabel: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp,
    ),

    /** Hashtag and mention inline link. */
    val tagLink: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        textDecoration = TextDecoration.None,
    ),

    /** Reels overlay caption — sits on top of video, larger for visibility. */
    val reelCaption: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Medium,
    ),

    /** Desktop sidebar nav label. */
    val sidebarNavLabel: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp,
    ),

    /** Empty-state hint message. */
    val emptyHint: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp,
    ),

    /** Security alert banner — used by device-tracking alerts. */
    val securityAlert: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
    ),
)

val LocalAurafeedTextStyles = staticCompositionLocalOf { AurafeedTextStyles() }