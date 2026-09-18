package com.nidoham.aurafeed.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Aurafeed — Shape System
 * ───────────────────────────────────────────────────────────
 *  Material 3 shape scale for cards, buttons, chips, sheets, modals.
 *
 *  Per project rule: Mobile and Desktop are separate variants — two
 *  [Shapes] instances are exposed ([AuraMobileShapes] / [AuraDesktopShapes]),
 *  and [AurafeedTheme] injects the correct one based on [LocalFormFactor].
 *
 *  All shapes are [RoundedCornerShape]; cut-corner shapes are kept available
 *  for special branding contexts (e.g. business badges) but not used by
 *  default in any Material role.
 *
 *  Platform target: Kotlin Compose Multiplatform — Android, iOS, Desktop (JVM)
 *  only. Pure `commonMain`, built only on the stable `androidx.compose.material3.Shapes`
 *  / `androidx.compose.foundation.shape` APIs — no deprecated or platform-specific calls.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. MOBILE SHAPES — Android / iOS
//    Slightly tighter radii for a denser vertical feed
// ─────────────────────────────────────────────────────────────────────────────

@Stable
fun mobileShapes(): Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // chips, small buttons
    small = RoundedCornerShape(8.dp),       // buttons, text fields
    medium = RoundedCornerShape(12.dp),     // cards, list items
    large = RoundedCornerShape(16.dp),      // sheets, large cards
    extraLarge = RoundedCornerShape(28.dp), // bottom sheets, modals
)

// ─────────────────────────────────────────────────────────────────────────────
// 2. DESKTOP SHAPES — Windows / macOS / Linux
//    Slightly larger radii for a softer, paper-like feel
// ─────────────────────────────────────────────────────────────────────────────

@Stable
fun desktopShapes(): Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// ─────────────────────────────────────────────────────────────────────────────
// 3. COMPONENT-SPECIFIC SHAPES — beyond Material 3 roles
//    Single source of truth so cards / pills / dialogs stay consistent
// ─────────────────────────────────────────────────────────────────────────────

@Stable
data class AurafeedComponentShapes(
    // Avatars — fully circular
    val avatarXs: Shape = RoundedCornerShape(50),
    val avatarSm: Shape = RoundedCornerShape(50),
    val avatarMd: Shape = RoundedCornerShape(50),
    val avatarLg: Shape = RoundedCornerShape(50),
    val avatarXl: Shape = RoundedCornerShape(50),
    val avatarStory: Shape = RoundedCornerShape(50),

    // Story ring (the outer ring shape itself)
    val storyRing: Shape = RoundedCornerShape(50),

    // Post media — sharp edges; the content is the focal point
    val postMedia: Shape = RoundedCornerShape(0.dp),
    val postMediaCompact: Shape = RoundedCornerShape(8.dp),  // grid tile
    val postMediaWide: Shape = RoundedCornerShape(12.dp),    // desktop center column

    // Carousel dots container
    val carouselDot: Shape = RoundedCornerShape(50),
    val carouselDotActive: Shape = RoundedCornerShape(50),

    // Chips — pill on both platforms
    val chip: Shape = RoundedCornerShape(50),
    val chipSquare: Shape = RoundedCornerShape(6.dp),

    // Buttons
    val buttonFilled: Shape = RoundedCornerShape(10.dp),
    val buttonOutlined: Shape = RoundedCornerShape(10.dp),
    val buttonText: Shape = RoundedCornerShape(8.dp),
    val buttonPill: Shape = RoundedCornerShape(50),
    val buttonFab: Shape = RoundedCornerShape(16.dp),
    val buttonFabExtended: Shape = RoundedCornerShape(20.dp),

    // Cards
    val cardFeed: Shape = RoundedCornerShape(0.dp),       // edge-to-edge feed card
    val cardSurface: Shape = RoundedCornerShape(12.dp),
    val cardElevated: Shape = RoundedCornerShape(12.dp),
    val cardOutlined: Shape = RoundedCornerShape(12.dp),
    val cardProfile: Shape = RoundedCornerShape(20.dp),

    // Sheets + Modals
    val bottomSheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val dialog: Shape = RoundedCornerShape(28.dp),
    val fullscreenSheet: Shape = RoundedCornerShape(0.dp),

    // Inputs
    val textFieldFilled: Shape = RoundedCornerShape(8.dp),
    val textFieldOutlined: Shape = RoundedCornerShape(8.dp),

    // Banners / Snackbars
    val snackbar: Shape = RoundedCornerShape(8.dp),
    val banner: Shape = RoundedCornerShape(12.dp),
    val securityAlert: Shape = RoundedCornerShape(10.dp),

    // Reels overlay
    val reelControl: Shape = RoundedCornerShape(50),
    val reelProgressSegment: Shape = RoundedCornerShape(2.dp),

    // Story stickers
    val stickerPoll: Shape = RoundedCornerShape(20.dp),
    val stickerQuestion: Shape = RoundedCornerShape(24.dp),
    val stickerMention: Shape = RoundedCornerShape(50),
    val stickerHashtag: Shape = RoundedCornerShape(50),
)

@Stable
fun mobileComponentShapes(): AurafeedComponentShapes = AurafeedComponentShapes()

@Stable
fun desktopComponentShapes(): AurafeedComponentShapes = AurafeedComponentShapes(
    postMediaWide = RoundedCornerShape(14.dp),
    cardSurface = RoundedCornerShape(14.dp),
    cardElevated = RoundedCornerShape(14.dp),
    cardOutlined = RoundedCornerShape(14.dp),
    cardProfile = RoundedCornerShape(24.dp),
    bottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    dialog = RoundedCornerShape(32.dp),
    buttonFilled = RoundedCornerShape(12.dp),
    buttonOutlined = RoundedCornerShape(12.dp),
    snackbar = RoundedCornerShape(10.dp),
    banner = RoundedCornerShape(14.dp),
    securityAlert = RoundedCornerShape(12.dp),
    stickerPoll = RoundedCornerShape(24.dp),
    stickerQuestion = RoundedCornerShape(28.dp),
    textFieldFilled = RoundedCornerShape(10.dp),
    textFieldOutlined = RoundedCornerShape(10.dp),
)

val LocalAurafeedShapes = staticCompositionLocalOf { AurafeedComponentShapes() }

// ─────────────────────────────────────────────────────────────────────────────
// 4. CUT-CORNER PRESETS — for branded business / premium badges
//    Not used in default Material 3 roles, but available on demand
// ─────────────────────────────────────────────────────────────────────────────

object AurafeedCutShapes {
    val badgeSquare = CutCornerShape(4)
    val badgeCut8 = CutCornerShape(8)
    val badgeCut12 = CutCornerShape(12)
}