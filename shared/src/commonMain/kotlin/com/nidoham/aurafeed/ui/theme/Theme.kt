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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

/**
 * Aurafeed — Theme Root
 * ───────────────────────────────────────────────────────────
 *  Wires [Color.kt] + [Type.kt] + [Shapes.kt] + [Tokens.kt] together and
 *  exposes a single [AurafeedTheme] composable for the whole app.
 *
 *  Platform target: Kotlin Compose Multiplatform — Android, iOS, Desktop
 *  (JVM) only. No web/Wasm-specific code lives here; this file is pure
 *  `commonMain` and has no platform dependencies.
 *
 *  Per project rules:
 *   • Mobile + Desktop separate variants → two [ColorScheme], two [Typography],
 *     two [Shapes], and two token sets, all auto-selected by [LocalFormFactor].
 *   • Only the current, stable Material 3 `ColorScheme` factory functions are
 *     used — including the modern `surfaceContainer*` / `*Fixed*` / `inversePrimary`
 *     roles so components built against the latest Material 3 guidance (cards,
 *     nav bars, bottom sheets, FABs) resolve correct colors instead of falling
 *     back to library defaults. No deprecated or demo APIs.
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
    inversePrimary      = LightInversePrimary,

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
    inverseOnSurface     = LightInverseOnSurface,
    outline              = LightOutline,
    outlineVariant       = LightOutlineVariant,

    // Modern M3 tonal-surface roles — cards, app bars, nav rails, bottom sheets
    surfaceDim              = LightSurfaceDim,
    surfaceBright           = LightSurfaceBright,
    surfaceContainerLowest  = LightSurfaceContainerLowest,
    surfaceContainerLow     = LightSurfaceContainerLow,
    surfaceContainer        = LightSurfaceContainer,
    surfaceContainerHigh    = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,

    // Fixed roles — identical value in light & dark, for brand-locked elements
    primaryFixed            = PrimaryFixed,
    primaryFixedDim         = PrimaryFixedDim,
    onPrimaryFixed          = OnPrimaryFixed,
    onPrimaryFixedVariant   = OnPrimaryFixedVariant,
    secondaryFixed          = SecondaryFixed,
    secondaryFixedDim       = SecondaryFixedDim,
    onSecondaryFixed        = OnSecondaryFixed,
    onSecondaryFixedVariant = OnSecondaryFixedVariant,
    tertiaryFixed           = TertiaryFixed,
    tertiaryFixedDim        = TertiaryFixedDim,
    onTertiaryFixed         = OnTertiaryFixed,
    onTertiaryFixedVariant  = OnTertiaryFixedVariant,

    error               = ErrorDefault,
    onError             = ErrorOnDefault,
    errorContainer      = ErrorContainer,
    onErrorContainer    = ErrorOnContainer,

    scrim               = ScrimBase,
)

@Stable
val AuraDarkColors: ColorScheme = darkColorScheme(
    primary             = DarkPrimary,
    onPrimary           = DarkOnPrimary,
    primaryContainer    = DarkPrimaryContainer,
    onPrimaryContainer  = DarkOnPrimaryContainer,
    inversePrimary      = DarkInversePrimary,

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

    // Modern M3 tonal-surface roles — cards, app bars, nav rails, bottom sheets
    surfaceDim              = DarkSurfaceDim,
    surfaceBright           = DarkSurfaceBright,
    surfaceContainerLowest  = DarkSurfaceContainerLowest,
    surfaceContainerLow     = DarkSurfaceContainerLow,
    surfaceContainer        = DarkSurfaceContainer,
    surfaceContainerHigh    = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,

    // Fixed roles — identical value in light & dark, for brand-locked elements
    primaryFixed            = PrimaryFixed,
    primaryFixedDim         = PrimaryFixedDim,
    onPrimaryFixed          = OnPrimaryFixed,
    onPrimaryFixedVariant   = OnPrimaryFixedVariant,
    secondaryFixed          = SecondaryFixed,
    secondaryFixedDim       = SecondaryFixedDim,
    onSecondaryFixed        = OnSecondaryFixed,
    onSecondaryFixedVariant = OnSecondaryFixedVariant,
    tertiaryFixed           = TertiaryFixed,
    tertiaryFixedDim        = TertiaryFixedDim,
    onTertiaryFixed         = OnTertiaryFixed,
    onTertiaryFixedVariant  = OnTertiaryFixedVariant,

    error                = ErrorDefault,
    onError              = ErrorOnDefault,
    errorContainer       = ErrorContainerDark,
    onErrorContainer     = ErrorOnContainerDark,

    scrim                = ScrimBase,
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
// 4. PLATFORM SYSTEM-BAR HOOK — expect/actual, Android / iOS / Desktop only
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tints the platform's system chrome (Android status/nav bar) to match the
 * current theme background, and switches icon contrast for light/dark content.
 *
 * `@Composable expect` so each `actual` can read the composition locals it
 * needs (e.g. Android's `LocalView`) rather than being fed values from common
 * code that don't exist on every target. Real `actual` implementations live in
 * `PlatformSystemBars.android.kt`, `PlatformSystemBars.ios.kt`, and
 * `PlatformSystemBars.desktop.kt` — not placeholders. iOS and Desktop no-op
 * because neither has an equivalent system status bar to color: iOS status bar
 * style is controlled by the hosting `UIViewController`/Info.plist, and JVM
 * desktop windows have no OS status bar at all.
 */
@Composable
expect fun PlatformSystemBars(background: Color, useDarkIcons: Boolean)

// ─────────────────────────────────────────────────────────────────────────────
// 5. THEME COMPOSABLE — single entry point for the whole app
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Single theming entry point for the whole app on every target (Android,
 * iOS, Desktop). Platform `main()` / root composables should call this once,
 * passing the correct [formFactor] for that target — everything downstream
 * (colors, type, shapes, spacing) is resolved from it automatically.
 *
 * Intentionally does **not** support Android 12+ dynamic (wallpaper-derived)
 * color: Aurafeed is a branded product with a fixed "Aura" palette, and
 * dynamic color would make every install look different and break parity
 * with iOS/Desktop, which have no equivalent system feature.
 *
 * @param darkTheme   Force a specific dark mode; defaults to the system setting.
 * @param formFactor  Force a specific form factor; defaults to [AurafeedFormFactor.Mobile]
 *                     (the desktop entry point should explicitly pass [AurafeedFormFactor.Desktop],
 *                     and large-screen Android/iPad layouts should pass [AurafeedFormFactor.Tablet]).
 * @param fontFamily  Custom font family; defaults to [FontFamily.Default].
 * @param content     App content.
 */
@Composable
fun AurafeedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    formFactor: AurafeedFormFactor = AurafeedFormFactor.Mobile,
    fontFamily: FontFamily = FontFamily.Default,   // now widened to FontFamily,
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

    // System bar (status bar) tinting is inherently platform-specific. Delegate
    // to the real `expect`/`actual` composable rather than faking a hook here;
    // it no-ops on platforms with no system status bar (iOS, Desktop/JVM).
    PlatformSystemBars(background = colorScheme.background, useDarkIcons = !darkTheme)

    CompositionLocalProvider(
        LocalAurafeedTheme provides themeState,
        LocalAurafeedTextStyles provides textStyles,
        LocalAurafeedShapes provides componentShapes,
        LocalAuraBrushes provides brushes,
        LocalAurafeedFontFamily provides fontFamily,
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