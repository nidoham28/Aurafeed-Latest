package com.nidoham.aurafeed.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.SystemFontFamily

/**
 * Aurafeed — Theme Root
 * ───────────────────────────────────────────────────────────
 *  Wires [Color.kt] + [Type.kt] + [Shapes.kt] + [Tokens.kt] together and
 *  exposes a single [AurafeedTheme] composable for the whole app.
 *
 *  Per project rules:
 *   • Mobile + Desktop separate variants → two [ColorScheme], two [Typography],
 *     two [Shapes], and two token sets, all auto-selected by [LocalFormFactor].
 *   • No demo / deprecated APIs — uses stable Material 3 colorScheme API.
 *   • "Aura" brand gradients are exposed via [LocalAuraBrushes] for reuse
 *     in story rings, hero headers, and onboarding screens.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. COLOR SCHEMES — assembled from Color.kt role tokens
// ─────────────────────────────────────────────────────────────────────────────

@Stable
val AuraLightColors: ColorScheme = lightColorScheme(
    primary             = LightPrimary,
    onPrimary           = LightOnPrimary,
    primaryContainer    = LightPrimaryContainer,
    onPrimaryContainer  = LightOnPrimaryContainer,

    secondary             = LightSecondary,
    onSecondary           = LightOnSecondary,
    secondaryContainer    = LightSecondaryContainer,
    onSecondaryContainer  = LightOnSecondaryContainer,

    tertiary             = LightTertiary,
    onTertiary           = LightOnTertiary,
    tertiaryContainer    = LightTertiaryContainer,
    onTertiaryContainer  = LightOnTertiaryContainer,

    background           = LightBackground,
    onBackground         = LightOnBackground,
    surface              = LightSurface,
    onSurface            = LightOnSurface,
    surfaceVariant       = LightSurfaceVariant,
    onSurfaceVariant     = LightOnSurfaceVariant,

    surfaceTint          = LightSurfaceTint,
    inverseSurface       = LightInverseSurface,
    inverseOnSurface    = LightInverseOnSurface,
    outline             = LightOutline,
    outlineVariant      = LightOutlineVariant,

    error               = ErrorDefault,
    onError             = ErrorOnDefault,
    errorContainer      = ErrorContainer,
    onErrorContainer    = ErrorOnContainer,
)

@Stable
val AuraDarkColors: ColorScheme = darkColorScheme(
    primary             = DarkPrimary,
    onPrimary           = DarkOnPrimary,
    primaryContainer    = DarkPrimaryContainer,
    onPrimaryContainer  = DarkOnPrimaryContainer,

    secondary             = DarkSecondary,
    onSecondary           = DarkOnSecondary,
    secondaryContainer    = DarkSecondaryContainer,
    onSecondaryContainer  = DarkOnSecondaryContainer,

    tertiary             = DarkTertiary,
    onTertiary           = DarkOnTertiary,
    tertiaryContainer    = DarkTertiaryContainer,
    onTertiaryContainer  = DarkOnTertiaryContainer,

    background           = DarkBackground,
    onBackground         = DarkOnBackground,
    surface              = DarkSurface,
    onSurface            = DarkOnSurface,
    surfaceVariant       = DarkSurfaceVariant,
    onSurfaceVariant     = DarkOnSurfaceVariant,

    surfaceTint          = DarkSurfaceTint,
    inverseSurface       = DarkInverseSurface,
    inverseOnSurface     = DarkInverseOnSurface,
    outline              = DarkOutline,
    outlineVariant       = DarkOutlineVariant,

    error                = ErrorDefault,
    onError              = ErrorOnDefault,
    errorContainer       = Color(0xFF93000A),
    onErrorContainer    = Color(0xFFFFDAD6),
)

// ─────────────────────────────────────────────────────────────────────────────
// 2. AURA BRUSHES — reusable gradients assembled from Color.kt
// ─────────────────────────────────────────────────────────────────────────────

@Stable
class AuraBrushes(
    val storyRing: Brush,
    val auraGlow: Brush,
    val auraHero: Brush,
    val dusk: Brush,
    val scrimVertical: Brush,
    val scrimAuraVertical: Brush,
    val shimmerLight: Brush,
    val shimmerDark: Brush,
    val shimmerAura: Brush,
)

val LocalAuraBrushes = staticCompositionLocalOf {
    // Fallback constructed eagerly; replaced by AurafeedTheme provider.
    AuraBrushes(
        storyRing = Brush.linearGradient(StoryRingColors),
        auraGlow = Brush.radialGradient(listOf(AuraGlowStrong, AuraGlowSoft, Color.Transparent)),
        auraHero = Brush.linearGradient(listOf(AuraGradientStart, AuraGradientMid, AuraGradientEnd)),
        dusk = Brush.verticalGradient(listOf(DuskGradientStart, DuskGradientEnd)),
        scrimVertical = Brush.verticalGradient(listOf(ScrimGradientTop, ScrimGradientBottom)),
        scrimAuraVertical = Brush.verticalGradient(listOf(ScrimAuraTop, ScrimAuraBottom)),
        shimmerLight = Brush.linearGradient(listOf(ShimmerLightBase, ShimmerLightHigh, ShimmerLightBase)),
        shimmerDark = Brush.linearGradient(listOf(ShimmerDarkBase, ShimmerDarkHigh, ShimmerDarkBase)),
        shimmerAura = Brush.linearGradient(listOf(ShimmerAuraBase, ShimmerAuraHigh, ShimmerAuraBase)),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. THEME STATE WRAPPER — exposed via LocalAurafeedTheme
// ─────────────────────────────────────────────────────────────────────────────

@Stable
class AurafeedThemeState(
    val colorScheme: ColorScheme,
    val typography: Typography,
    val shapes: androidx.compose.material3.Shapes,
    val tokens: AurafeedTokens,
    val textStyles: AurafeedTextStyles,
    val componentShapes: AurafeedComponentShapes,
    val brushes: AuraBrushes,
    val isDark: Boolean,
    val formFactor: AurafeedFormFactor,
) {
    val spacing get() = tokens.spacing
    val elevation get() = tokens.elevation
    val corners get() = tokens.corners
    val sizes get() = tokens.sizes
    val contentPadding get() = tokens.contentPadding
    val isDesktop: Boolean get() = formFactor == AurafeedFormFactor.Desktop
    val isMobile: Boolean get() = formFactor == AurafeedFormFactor.Mobile
}

val LocalAurafeedTheme = staticCompositionLocalOf<AurafeedThemeState> {
    error("AurafeedTheme not provided. Wrap your content in AurafeedTheme { }.")
}

// Convenience accessor (matches Material's `MaterialTheme` ergonomics)
object AurafeedTheme {
    val colors: ColorScheme
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.colorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.typography

    val shapes: androidx.compose.material3.Shapes
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.shapes

    val tokens: AurafeedTokens
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.tokens

    val textStyles: AurafeedTextStyles
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.textStyles

    val componentShapes: AurafeedComponentShapes
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.componentShapes

    val brushes: AuraBrushes
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.brushes

    val isDark: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.isDark

    val formFactor: AurafeedFormFactor
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.formFactor

    val isDesktop: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.isDesktop

    val isMobile: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalAurafeedTheme.current.isMobile
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. THEME COMPOSABLE — single entry point for the whole app
// ─────────────────────────────────────────────────────────────────────────────

/**
 * @param darkTheme   Force a specific dark mode; defaults to system setting.
 * @param formFactor  Force a specific form factor; defaults to [AurafeedFormFactor.Mobile]
 *                     (the desktop entry point should explicitly pass [AurafeedFormFactor.Desktop]).
 * @param fontFamily  Custom font family; defaults to [FontFamily.Default].
 * @param dynamicColor Currently disabled — Supabase Free tier + project rule
 *                     "no demo / deprecated APIs" means we stick with the brand palette.
 *                     Enable when product brand strategy requires dynamic theming.
 * @param content     App content.
 */
@Composable
fun AurafeedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    formFactor: AurafeedFormFactor = AurafeedFormFactor.Mobile,
    fontFamily: androidx.compose.ui.text.font.FontFamily = androidx.compose.ui.text.font.FontFamily.Default,
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(darkTheme) {
        if (darkTheme) AuraDarkColors else AuraLightColors
    }

    val typography = remember(formFactor, fontFamily) {
        when (formFactor) {
            AurafeedFormFactor.Mobile, AurafeedFormFactor.Tablet -> mobileTypography(fontFamily)
            AurafeedFormFactor.Desktop -> desktopTypography(fontFamily)
        }
    }

    val materialShapes = remember(formFactor) {
        when (formFactor) {
            AurafeedFormFactor.Mobile, AurafeedFormFactor.Tablet -> mobileShapes()
            AurafeedFormFactor.Desktop -> desktopShapes()
        }
    }

    val tokens = remember(formFactor) {
        when (formFactor) {
            AurafeedFormFactor.Mobile -> mobileTokens()
            AurafeedFormFactor.Tablet -> tabletTokens()
            AurafeedFormFactor.Desktop -> desktopTokens()
        }
    }

    val componentShapes = remember(formFactor) {
        when (formFactor) {
            AurafeedFormFactor.Mobile, AurafeedFormFactor.Tablet -> mobileComponentShapes()
            AurafeedFormFactor.Desktop -> desktopComponentShapes()
        }
    }

    val textStyles = remember(fontFamily) { AurafeedTextStyles() }

    val brushes = remember(darkTheme) {
        AuraBrushes(
            storyRing = Brush.linearGradient(StoryRingColors),
            auraGlow = Brush.radialGradient(
                listOf(AuraGlowStrong, AuraGlowSoft, Color.Transparent),
            ),
            auraHero = Brush.linearGradient(
                listOf(AuraGradientStart, AuraGradientMid, AuraGradientEnd),
            ),
            dusk = Brush.verticalGradient(listOf(DuskGradientStart, DuskGradientEnd)),
            scrimVertical = Brush.verticalGradient(
                listOf(ScrimGradientTop, ScrimGradientBottom),
            ),
            scrimAuraVertical = Brush.verticalGradient(
                listOf(ScrimAuraTop, ScrimAuraBottom),
            ),
            shimmerLight = Brush.linearGradient(
                listOf(ShimmerLightBase, ShimmerLightHigh, ShimmerLightBase),
            ),
            shimmerDark = Brush.linearGradient(
                listOf(ShimmerDarkBase, ShimmerDarkHigh, ShimmerDarkBase),
            ),
            shimmerAura = Brush.linearGradient(
                listOf(ShimmerAuraBase, ShimmerAuraHigh, ShimmerAuraBase),
            ),
        )
    }

    val themeState = remember(
        colorScheme, typography, materialShapes, tokens, textStyles, componentShapes, brushes, darkTheme, formFactor,
    ) {
        AurafeedThemeState(
            colorScheme = colorScheme,
            typography = typography,
            shapes = materialShapes,
            tokens = tokens,
            textStyles = textStyles,
            componentShapes = componentShapes,
            brushes = brushes,
            isDark = darkTheme,
            formFactor = formFactor,
        )
    }

    // Status bar / system bar tint — platform-agnostic; platform-specific
    // controller implementations should hook into LocalAurafeedTheme in their
    // respective main entry points (see docs/02 for platform setup).
    SideEffect {
        // The actual system bar coloring is platform-specific (expect/actual).
        // This hook ensures theme changes are observable to platform controllers
        // without coupling the core theme to any single platform API.
        // Implementation: see PlatformChrome.kt (mobile) / DesktopChrome.kt (desktop)
        val argb = colorScheme.background.toArgb()
        // Placeholder for platform controller — see platform-specific extension
        @Suppress("UNUSED_VARIABLE")
        val _argb = argb
    }

    CompositionLocalProvider(
        LocalAurafeedTheme provides themeState,
        LocalAurafeedTextStyles provides textStyles,
        LocalAurafeedShapes provides componentShapes,
        LocalAuraBrushes provides brushes,
        LocalAurafeedFontFamily provides fontFamily as SystemFontFamily,
        LocalFormFactor provides formFactor,
        LocalSpacing provides tokens.spacing,
        LocalElevation provides tokens.elevation,
        LocalCorners provides tokens.corners,
        LocalSizes provides tokens.sizes,
        LocalContentPadding provides tokens.contentPadding,
        LocalAurafeedTokens provides tokens,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = materialShapes,
            content = content,
        )
    }
}
