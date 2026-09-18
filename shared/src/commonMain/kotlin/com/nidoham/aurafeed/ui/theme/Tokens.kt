package com.nidoham.aurafeed.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import kotlin.math.roundToInt

/**
 * Aurafeed — Design Tokens
 * ───────────────────────────────────────────────────────────
 *  Single source of truth for spacing, elevation, motion, sizing, and
 *  platform-specific density. Used by every screen + component.
 *
 *  Per project rule: Mobile and Desktop are separate variants — expose
 *  both, and let [AurafeedTheme] inject the correct one via [LocalAurafeedTokens].
 *
 *  All values are deliberately tuned — no arbitrary magic numbers.
 *
 *  Platform target: Kotlin Compose Multiplatform — Android, iOS, Desktop (JVM)
 *  only. Pure `commonMain`; no deprecated or platform-specific APIs.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. SPACING — 4pt baseline grid
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class AurafeedSpacing(
    val none: Dp = 0.dp,
    val hairline: Dp = 0.5.dp,
    val xs: Dp = 4.dp,        // icon-to-text gaps, dense chips
    val sm: Dp = 8.dp,        // inner padding of buttons, list item leading
    val md: Dp = 12.dp,       // card inner padding
    val lg: Dp = 16.dp,       // standard screen padding (mobile)
    val xl: Dp = 20.dp,       // section gap
    val xxl: Dp = 24.dp,      // section header to body
    val xxxl: Dp = 32.dp,     // hero spacing
    val huge: Dp = 48.dp,     // end of scroll padding
    val mega: Dp = 64.dp,     // empty-state spacing
)

val LocalSpacing = staticCompositionLocalOf { AurafeedSpacing() }

// ─────────────────────────────────────────────────────────────────────────────
// 2. ELEVATION — Material 3 tonal elevation scale
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class AurafeedElevation(
    val level0: Dp = 0.dp,    // flat surface
    val level1: Dp = 1.dp,     // raised card / chip resting
    val level2: Dp = 3.dp,     // card hover / dropdown
    val level3: Dp = 6.dp,     // dialog / bottom sheet
    val level4: Dp = 8.dp,     // FAB
    val level5: Dp = 12.dp,    // nav drawer / modal
    val level6: Dp = 16.dp,    // deep modal
    val level7: Dp = 24.dp,    // peek elevation
)

val LocalElevation = staticCompositionLocalOf { AurafeedElevation() }

// ─────────────────────────────────────────────────────────────────────────────
// 3. MOTION — durations and easings (no slow features per project rule)
// ─────────────────────────────────────────────────────────────────────────────

object AurafeedMotion {
    // Durations (ms)
    const val InstantXs    = 50
    const val ShortFast    = 100
    const val Short        = 150
    const val ShortSlow   = 200
    const val MediumFast  = 250
    const val Medium      = 300
    const val MediumSlow  = 400
    const val LongFast    = 450
    const val Long        = 500
    const val LongSlow    = 600
    const val XLong        = 800

    // Stagger between shimmer skeleton blocks
    const val ShimmerBaseDelay   = 30
    const val ShimmerStaggerStep = 60
    const val ShimmerPeriod      = 1100
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. CORNER RADII — Material 3 shape scale (mobile)
//    Desktop uses slightly larger radii (see [desktopCornerOverrides])
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class AurafeedCorners(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,       // chips, small buttons
    val sm: Dp = 8.dp,       // buttons, text fields
    val md: Dp = 12.dp,      // cards, list items
    val lg: Dp = 16.dp,      // sheets, large cards
    val xl: Dp = 24.dp,      // bottom sheets, modals
    val xxl: Dp = 28.dp,     // story rings, FAB groups
    val pill: Dp = 9999.dp,  // pills, story rings
)

val LocalCorners = staticCompositionLocalOf { AurafeedCorners() }

// ─────────────────────────────────────────────────────────────────────────────
// 5. COMPONENT SIZES — touch targets, avatars, icons, app bars
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class AurafeedSizes(
    // Touch targets (Material guideline: 48dp mobile, 40dp desktop acceptable)
    val touchTargetMin: Dp = 48.dp,
    val touchTargetDesktopMin: Dp = 40.dp,

    // App bar
    val topBarHeight: Dp = 56.dp,
    val topBarHeightLarge: Dp = 152.dp,
    val bottomBarHeight: Dp = 60.dp,
    val desktopSidebarWidth: Dp = 280.dp,
    val desktopSidebarCollapsedWidth: Dp = 72.dp,

    // Avatars
    val avatarXs: Dp = 24.dp,    // inline mention
    val avatarSm: Dp = 32.dp,    // comment author
    val avatarMd: Dp = 40.dp,    // list item
    val avatarLg: Dp = 48.dp,    // profile header
    val avatarXl: Dp = 72.dp,    // profile hero
    val avatarStory: Dp = 56.dp, // story tray

    // Icons
    val iconXs: Dp = 12.dp,
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 20.dp,
    val iconLg: Dp = 24.dp,
    val iconXl: Dp = 32.dp,

    // Story ring stroke
    val storyRingStroke: Dp = 2.5.dp,
    val storyRingGap: Dp = 3.dp,
    val storyRingSeenStroke: Dp = 1.5.dp,

    // Feed post aspect ratio
    val postAspectRatio: Float = 1.0f,
    val postMaxWidth: Dp = 600.dp,    // desktop center column

    // Bottom nav item
    val bottomNavItemSize: Dp = 56.dp,

    // FAB
    val fabSize: Dp = 56.dp,
    val fabExtendedHeight: Dp = 48.dp,
    val fabSpacing: Dp = 16.dp,
)

val LocalSizes = staticCompositionLocalOf { AurafeedSizes() }

// ─────────────────────────────────────────────────────────────────────────────
// 6. CONTENT PADDING / MAX WIDTH — responsive layout helpers
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class AurafeedContentPadding(
    val screenHorizontalMobile: Dp = 16.dp,
    val screenHorizontalTablet: Dp = 32.dp,
    val screenHorizontalDesktop: Dp = 48.dp,

    val feedMaxWidthPhone: Dp = 480.dp,
    val feedMaxWidthTablet: Dp = 600.dp,
    val feedMaxWidthDesktop: Dp = 640.dp,

    val detailMaxWidthTablet: Dp = 720.dp,
    val detailMaxWidthDesktop: Dp = 960.dp,

    val twoPaneSplitRatio: Float = 0.45f,   // master / detail split
)

val LocalContentPadding = staticCompositionLocalOf { AurafeedContentPadding() }

// ─────────────────────────────────────────────────────────────────────────────
// 7. PLATFORM / FORM FACTOR
// ─────────────────────────────────────────────────────────────────────────────

enum class AurafeedFormFactor { Mobile, Tablet, Desktop }

val LocalFormFactor = staticCompositionLocalOf { AurafeedFormFactor.Mobile }

// ─────────────────────────────────────────────────────────────────────────────
// 8. AGGREGATE TOKENS — convenience accessor used by [AurafeedTheme]
// ─────────────────────────────────────────────────────────────────────────────

@Stable
data class AurafeedTokens(
    val spacing: AurafeedSpacing,
    val elevation: AurafeedElevation,
    val corners: AurafeedCorners,
    val sizes: AurafeedSizes,
    val contentPadding: AurafeedContentPadding,
    val formFactor: AurafeedFormFactor,
) {
    val isDesktop: Boolean get() = formFactor == AurafeedFormFactor.Desktop
    val isMobile: Boolean get() = formFactor == AurafeedFormFactor.Mobile
}

val LocalAurafeedTokens = staticCompositionLocalOf {
    AurafeedTokens(
        spacing = AurafeedSpacing(),
        elevation = AurafeedElevation(),
        corners = AurafeedCorners(),
        sizes = AurafeedSizes(),
        contentPadding = AurafeedContentPadding(),
        formFactor = AurafeedFormFactor.Mobile,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 9. TOKEN FACTORIES — per form factor
// ─────────────────────────────────────────────────────────────────────────────

/** Mobile-first default tokens. Used on Android + iOS. */
fun mobileTokens(): AurafeedTokens = AurafeedTokens(
    spacing = AurafeedSpacing(),
    elevation = AurafeedElevation(),
    corners = AurafeedCorners(),
    sizes = AurafeedSizes(),
    contentPadding = AurafeedContentPadding(),
    formFactor = AurafeedFormFactor.Mobile,
)

/** Tablet-optimized tokens — wider content area, larger touch targets. */
fun tabletTokens(): AurafeedTokens = AurafeedTokens(
    spacing = AurafeedSpacing().let {
        it.copy(
            lg = 20.dp,
            xl = 24.dp,
            xxl = 32.dp,
        )
    },
    elevation = AurafeedElevation(),
    corners = AurafeedCorners().let {
        it.copy(
            md = 14.dp,
            lg = 18.dp,
            xl = 28.dp,
        )
    },
    sizes = AurafeedSizes().let {
        it.copy(
            topBarHeight = 64.dp,
            bottomBarHeight = 64.dp,
        )
    },
    contentPadding = AurafeedContentPadding().let {
        it.copy(
            screenHorizontalMobile = 24.dp,
            screenHorizontalTablet = 36.dp,
        )
    },
    formFactor = AurafeedFormFactor.Tablet,
)

/** Desktop tokens — paper-like surfaces, smaller touch targets, larger radii. */
fun desktopTokens(): AurafeedTokens = AurafeedTokens(
    spacing = AurafeedSpacing().let {
        it.copy(
            lg = 20.dp,
            xl = 28.dp,
            xxl = 36.dp,
            xxxl = 48.dp,
            huge = 64.dp,
        )
    },
    elevation = AurafeedElevation().let {
        it.copy(
            level1 = 2.dp,
            level2 = 4.dp,
            level3 = 8.dp,
        )
    },
    corners = AurafeedCorners().let {
        it.copy(
            xs = 6.dp,
            sm = 10.dp,
            md = 14.dp,
            lg = 20.dp,
            xl = 28.dp,
            xxl = 36.dp,
        )
    },
    sizes = AurafeedSizes().let {
        it.copy(
            touchTargetMin = 40.dp,   // desktop mouse precision
            topBarHeight = 64.dp,
            bottomBarHeight = 0.dp,    // desktop uses sidebar, not bottom bar
            avatarMd = 36.dp,
            avatarLg = 44.dp,
        )
    },
    contentPadding = AurafeedContentPadding().let {
        it.copy(
            screenHorizontalMobile = 24.dp,
            screenHorizontalTablet = 48.dp,
            screenHorizontalDesktop = 64.dp,
        )
    },
    formFactor = AurafeedFormFactor.Desktop,
)

// ─────────────────────────────────────────────────────────────────────────────
// 10. HELPERS — Dp/Int/Float conversions used by shimmer + animation code
// ─────────────────────────────────────────────────────────────────────────────

@Stable
object AurafeedMetrics {
    /** Convert dp to px for canvas drawing (rounds to avoid sub-pixel seams). */
    fun dpToPx(dp: Float, density: Float): Int = (dp * density).roundToInt()

    fun pxToDp(px: Int, density: Float): Float = if (density == 0f) 0f else px / density

    /** Convert sp to px for text-bound measurements. */
    fun spToPx(sp: TextUnit, density: Float): Int =
        (sp.value * density).roundToInt()

    val DefaultDpi = 160f
}