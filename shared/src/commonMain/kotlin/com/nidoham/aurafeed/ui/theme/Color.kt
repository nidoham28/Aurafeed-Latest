package com.nidoham.aurafeed.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Aurafeed — Core Color System
 * ───────────────────────────────────────────────────────────
 *  Package: com.nidoham.aurafeed
 *  Target:  Android / iOS / Desktop (single source of truth)
 *  Theme:   "Aura" — a soft violet core with warm coral and teal accents,
 *           designed to feel personal, glowing, and calm — appropriate for a
 *           social + lifestyle product that emphasizes user well-being.
 *
 *  Naming convention:
 *   - `Aura<Role><Variant>` for brand tokens (e.g. `AuraPrimary80`)
 *   - `<Role>` tonal steps use Material 3 0–100 scale (0 = darkest, 100 = lightest)
 *   - Semantic / interaction colors use plain descriptive names (e.g. `LikeRed`)
 *
 *  Platform target: Kotlin Compose Multiplatform — Android, iOS, Desktop (JVM)
 *  only. Pure `commonMain`; built only on `androidx.compose.ui.graphics.Color`.
 *
 *  Rules:
 *   - All colors are [Color] vals — immutable, [androidx.compose.runtime.Stable] by default
 *   - No demo / placeholder colors — every token here is referenced by UI
 *   - Light + Dark variants are paired explicitly
 *   - Includes the full Material 3 role set used by current M3 components,
 *     including `surfaceContainer*` tonal roles (sections 12 & 13) and
 *     `*Fixed*` roles (section 13b), so no component silently falls back
 *     to a library default color.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. BRAND CORE — "Aura Violet" tonal ramp (Material 3 style, 0..100)
// ─────────────────────────────────────────────────────────────────────────────

val AuraPrimary0   = Color(0xFF000000)
val AuraPrimary10  = Color(0xFF1A0A2E)
val AuraPrimary20  = Color(0xFF2B1245)
val AuraPrimary30  = Color(0xFF3D1B5C)
val AuraPrimary40  = Color(0xFF5B2A8C)
val AuraPrimary50  = Color(0xFF7C3AED)
val AuraPrimary60  = Color(0xFF8B5CF6)
val AuraPrimary70  = Color(0xFFA678F7)
val AuraPrimary80  = Color(0xFFC4A6F9)
val AuraPrimary90  = Color(0xFFE3D4FC)
val AuraPrimary95  = Color(0xFFF2EAFF)
val AuraPrimary99  = Color(0xFFFDFBFF)

/** Primary brand color used for buttons, active states, focused fields. */
val AuraBrand       = AuraPrimary50
val AuraBrandHover  = AuraPrimary60
val AuraBrandActive = AuraPrimary40

// ─────────────────────────────────────────────────────────────────────────────
// 2. SECONDARY — "Coral" (warm accent for highlights, badges, story rings)
// ─────────────────────────────────────────────────────────────────────────────

val AuraSecondary10  = Color(0xFF2A0A14)
val AuraSecondary20  = Color(0xFF4A0F1F)
val AuraSecondary30  = Color(0xFF6E162E)
val AuraSecondary40  = Color(0xFF9C1F40)
val AuraSecondary50  = Color(0xFFD63A6B)
val AuraSecondary60  = Color(0xFFFF6B9D)
val AuraSecondary70  = Color(0xFFFF8FB5)
val AuraSecondary80  = Color(0xFFFFB3CE)
val AuraSecondary90  = Color(0xFFFFD9E5)
val AuraSecondary95  = Color(0xFFFFEDF3)

// ─────────────────────────────────────────────────────────────────────────────
// 3. TERTIARY — "Aqua" (cool accent for info, links, system highlights)
// ─────────────────────────────────────────────────────────────────────────────

val AuraTertiary10  = Color(0xFF00211F)
val AuraTertiary20  = Color(0xFF003836)
val AuraTertiary30  = Color(0xFF00504D)
val AuraTertiary40  = Color(0xFF006B66)
val AuraTertiary50  = Color(0xFF1F8B85)
val AuraTertiary60  = Color(0xFF4ECDC4)
val AuraTertiary70  = Color(0xFF7BDDD5)
val AuraTertiary80  = Color(0xFFADECE7)
val AuraTertiary90  = Color(0xFFD5F5F2)
val AuraTertiary95  = Color(0xFFEAFBF9)

// ─────────────────────────────────────────────────────────────────────────────
// 4. NEUTRAL RAMP — for backgrounds, surfaces, text, dividers
//    Uses a slight cool tint to harmonize with the violet brand
// ─────────────────────────────────────────────────────────────────────────────

val AuraNeutral0    = Color(0xFF000000)
val AuraNeutral4    = Color(0xFF08080D)
val AuraNeutral5    = Color(0xFF0B0B12)
val AuraNeutral6    = Color(0xFF0D0D15)
val AuraNeutral10   = Color(0xFF111118)
val AuraNeutral12   = Color(0xFF14141C)
val AuraNeutral15   = Color(0xFF16161F)
val AuraNeutral17   = Color(0xFF191922)
val AuraNeutral20   = Color(0xFF1C1C26)
val AuraNeutral22   = Color(0xFF1F1F29)
val AuraNeutral24   = Color(0xFF212230)
val AuraNeutral25   = Color(0xFF22222E)
val AuraNeutral30   = Color(0xFF2A2A36)
val AuraNeutral40   = Color(0xFF3D3D4C)
val AuraNeutral50   = Color(0xFF565668)
val AuraNeutral60   = Color(0xFF717180)
val AuraNeutral70   = Color(0xFF8E8E9B)
val AuraNeutral80   = Color(0xFFB0B0BB)
val AuraNeutral85   = Color(0xFFC6C6CE)
val AuraNeutral87   = Color(0xFFD2D2D9)
val AuraNeutral90   = Color(0xFFDFDFE4)
val AuraNeutral92   = Color(0xFFE5E5E9)
val AuraNeutral94   = Color(0xFFEAEAEE)
val AuraNeutral95   = Color(0xFFEFEFF2)
val AuraNeutral96   = Color(0xFFF2F2F5)
val AuraNeutral98   = Color(0xFFF8F8FA)
val AuraNeutral99   = Color(0xFFFDFBFF)
val AuraNeutral100  = Color(0xFFFFFFFF)

val AuraNeutralVariant20 = Color(0xFF1E1A24)
val AuraNeutralVariant30 = Color(0xFF2A2531)
val AuraNeutralVariant50 = Color(0xFF605A6B)
val AuraNeutralVariant60 = Color(0xFF7B7489)
val AuraNeutralVariant80 = Color(0xFFCFC5D4)
val AuraNeutralVariant90 = Color(0xFFECE1F0)

// ─────────────────────────────────────────────────────────────────────────────
// 5. ERROR / WARNING / SUCCESS / INFO — semantic tokens
// ─────────────────────────────────────────────────────────────────────────────

// Error — "Signal Red" (destructive actions, validation failures)
val ErrorDark10   = Color(0xFF2C0004)
val ErrorDark20   = Color(0xFF5C0009)
val ErrorDark30   = Color(0xFF93000A)
val ErrorDark40   = Color(0xFFBA1A1A)
val ErrorDark60   = Color(0xFFFF8980)
val ErrorDark80   = Color(0xFFFFB4AB)
val ErrorDark90   = Color(0xFFFFDAD6)
val ErrorLight40  = Color(0xFFBA1A1A)
val ErrorLight60  = Color(0xFFC41818)
val ErrorLight80  = Color(0xFFFFB4AB)
val ErrorLight90  = Color(0xFFFFDAD6)

val ErrorDefault     = ErrorDark40
val ErrorOnDefault   = Color(0xFFFFFFFF)
val ErrorContainer   = ErrorDark90
val ErrorOnContainer = ErrorDark20

/** Dark-theme error container pair — kept as named tokens (never raw hex in Theme.kt). */
val ErrorContainerDark   = ErrorDark30
val ErrorOnContainerDark = ErrorDark90

// Warning — "Amber" (incomplete state, rate-limit, quota)
val WarningDark40  = Color(0xFFB45C00)
val WarningDark60  = Color(0xFFFFA554)
val WarningDark80  = Color(0xFFFFD180)
val WarningDark90  = Color(0xFFFFE5C2)
val WarningDefault    = WarningDark40
val WarningContainer  = WarningDark90
val WarningOnContainer = Color(0xFF4A2600)

// Success — "Green" (confirmed, posted, saved)
val SuccessDark40  = Color(0xFF1F7A33)
val SuccessDark60  = Color(0xFF58C977)
val SuccessDark80  = Color(0xFFA5E5B3)
val SuccessDark90  = Color(0xFFD2F2D8)
val SuccessDefault    = SuccessDark40
val SuccessContainer  = SuccessDark90
val SuccessOnContainer = Color(0xFF003D12)

// Info — "Sky" (system messages, neutral alerts)
val InfoDark40  = Color(0xFF0A66B4)
val InfoDark60  = Color(0xFF62A6F5)
val InfoDark80  = Color(0xFFB2D8FF)
val InfoDark90  = Color(0xFFD7EBFF)
val InfoDefault    = InfoDark40
val InfoContainer  = InfoDark90
val InfoOnContainer = Color(0xFF001D36)

// ─────────────────────────────────────────────────────────────────────────────
// 6. SOCIAL INTERACTION COLORS — like / love / comment / share / save / follow
//    These are intentionally distinct from the brand so they read at a glance
// ─────────────────────────────────────────────────────────────────────────────

val LikeRed          = Color(0xFFFF3040)   // "liked" heart, animated state
val LikeRedBg        = Color(0xFFFFE5E8)
val LovePink         = Color(0xFFE0245E)   // double-tap love burst
val CommentBlue      = Color(0xFF1B91DB)   // comment bubble accent
val ShareTeal        = Color(0xFF12B5A0)   // share / forward accent
val SaveGold         = Color(0xFFFFB400)   // saved bookmark active
val SaveGoldBg       = Color(0xFFFFF1CC)
val FollowGreen      = Color(0xFF1F9D55)   // follow button active
val FollowGreenBg    = Color(0xFFD2F5DF)
val MentionPurple    = AuraPrimary50
val HashtagBlue      = Color(0xFF1B6FE5)
val LinkBlue         = Color(0xFF0E63C9)

// ─────────────────────────────────────────────────────────────────────────────
// 7. STORY RING + AURA GRADIENTS — multi-stop pairs used in Brush.gradient*
//    (kept as raw Color lists; Theme.kt assembles the Brushes)
// ─────────────────────────────────────────────────────────────────────────────

val StoryRingColors = listOf(
    Color(0xFFFF6B9D),
    Color(0xFFFF7E5F),
    Color(0xFFC4A6F9),
    AuraPrimary50,
)

val AuraGradientStart  = Color(0xFF7C3AED)
val AuraGradientMid    = Color(0xFFD63A6B)
val AuraGradientEnd    = Color(0xFFFFB400)

val AuraGlowSoft       = Color(0x337C3AED)  // 20% alpha aura halo
val AuraGlowStrong     = Color(0x667C3AED)  // 40% alpha aura halo

// Dusk gradient — used on empty / onboarding screens
val DuskGradientStart = Color(0xFF1A0A2E)
val DuskGradientEnd   = Color(0xFF3D1B5C)

// ─────────────────────────────────────────────────────────────────────────────
// 8. STATUS / PRESENCE — chat, online dot, typing
// ─────────────────────────────────────────────────────────────────────────────

val OnlineGreen      = Color(0xFF34D058)
val OnlineGreenRing  = Color(0xFF0E7A1E)
val AwayAmber        = Color(0xFFFFB400)
val OfflineGrey      = Color(0xFF8E8E9B)
val TypingDots       = AuraPrimary60

// ─────────────────────────────────────────────────────────────────────────────
// 9. VERIFICATION & BADGES
// ─────────────────────────────────────────────────────────────────────────────

val VerifiedBlue       = Color(0xFF1D9BF0)
val VerifiedBlueRing   = Color(0xFF0E71B8)
val BusinessAmber      = Color(0xFFFFB400)
val PremiumGold        = Color(0xFFFFC857)
val PremiumGoldRing    = Color(0xFFB88500)

// ─────────────────────────────────────────────────────────────────────────────
// 10. SHIMMER PLACEHOLDER COLORS — used by ShimmerBox / loading skeletons
//     (project rule: no plain spinners — shimmer placeholders everywhere)
// ─────────────────────────────────────────────────────────────────────────────

val ShimmerLightBase    = Color(0xFFE7E7EC)
val ShimmerLightHigh    = Color(0xFFF6F6F9)
val ShimmerDarkBase     = Color(0xFF1C1C26)
val ShimmerDarkHigh     = Color(0xFF2A2A36)
val ShimmerAuraBase     = Color(0xFF2B1245)
val ShimmerAuraHigh     = Color(0xFF3D1B5C)

// ─────────────────────────────────────────────────────────────────────────────
// 11. OVERLAYS / SCRIMS — modals, image viewer, video controls
// ─────────────────────────────────────────────────────────────────────────────

/** Material 3 `ColorScheme.scrim` role — modal barrier behind sheets/dialogs. */
val ScrimBase      = Color(0xFF000000)
val ScrimStrong    = Color(0xE6111118)  // 90% dark scrim
val ScrimMedium    = Color(0xB3111118)  // 70% dark scrim
val ScrimSoft      = Color(0x66111118)  // 40% dark scrim
val ScrimLight     = Color(0xFFFFFFFF)
val ScrimGradientTop    = Color(0xCC000000)
val ScrimGradientBottom = Color(0x00000000)
val ScrimAuraTop        = Color(0xCC2B1245)
val ScrimAuraBottom      = Color(0x002B1245)

// ─────────────────────────────────────────────────────────────────────────────
// 12. LIGHT THEME ROLE COLORS — paired with [AuraLightColors] in Theme.kt
// ─────────────────────────────────────────────────────────────────────────────

val LightPrimary            = AuraPrimary40
val LightOnPrimary          = Color(0xFFFFFFFF)
val LightPrimaryContainer   = AuraPrimary90
val LightOnPrimaryContainer = AuraPrimary10

val LightSecondary            = AuraSecondary40
val LightOnSecondary          = Color(0xFFFFFFFF)
val LightSecondaryContainer   = AuraSecondary90
val LightOnSecondaryContainer = AuraSecondary10

val LightTertiary            = AuraTertiary40
val LightOnTertiary          = Color(0xFFFFFFFF)
val LightTertiaryContainer   = AuraTertiary90
val LightOnTertiaryContainer = AuraTertiary10

val LightBackground       = AuraNeutral99
val LightOnBackground    = AuraNeutral10
val LightSurface         = AuraNeutral99
val LightOnSurface       = AuraNeutral10
val LightSurfaceVariant  = AuraNeutralVariant90
val LightOnSurfaceVariant = AuraNeutralVariant30

val LightSurfaceTint      = AuraPrimary40
val LightInverseSurface   = AuraNeutral20
val LightInverseOnSurface = AuraNeutral95
val LightInversePrimary   = AuraPrimary80
val LightOutline          = AuraNeutralVariant50
val LightOutlineVariant   = AuraNeutralVariant80

// Material 3 "surface container" tonal roles (cards, nav bars, sheets, app bars)
val LightSurfaceDim              = AuraNeutral87
val LightSurfaceBright           = AuraNeutral98
val LightSurfaceContainerLowest  = AuraNeutral100
val LightSurfaceContainerLow     = AuraNeutral96
val LightSurfaceContainer        = AuraNeutral94
val LightSurfaceContainerHigh    = AuraNeutral92
val LightSurfaceContainerHighest = AuraNeutral90

// ─────────────────────────────────────────────────────────────────────────────
// 13. DARK THEME ROLE COLORS — paired with [AuraDarkColors] in Theme.kt
// ─────────────────────────────────────────────────────────────────────────────

val DarkPrimary            = AuraPrimary80
val DarkOnPrimary          = AuraPrimary20
val DarkPrimaryContainer   = AuraPrimary30
val DarkOnPrimaryContainer = AuraPrimary90

val DarkSecondary            = AuraSecondary80
val DarkOnSecondary          = AuraSecondary20
val DarkSecondaryContainer   = AuraSecondary30
val DarkOnSecondaryContainer = AuraSecondary90

val DarkTertiary            = AuraTertiary80
val DarkOnTertiary          = AuraTertiary20
val DarkTertiaryContainer   = AuraTertiary30
val DarkOnTertiaryContainer = AuraTertiary90

val DarkBackground       = AuraNeutral5
val DarkOnBackground    = AuraNeutral90
val DarkSurface         = AuraNeutral10
val DarkOnSurface      = AuraNeutral90
val DarkSurfaceVariant  = AuraNeutralVariant30
val DarkOnSurfaceVariant = AuraNeutralVariant80

val DarkSurfaceTint      = AuraPrimary80
val DarkInverseSurface   = AuraNeutral90
val DarkInverseOnSurface = AuraNeutral20
val DarkInversePrimary   = AuraPrimary40
val DarkOutline          = AuraNeutralVariant60
val DarkOutlineVariant   = AuraNeutralVariant30

// Material 3 "surface container" tonal roles (cards, nav bars, sheets, app bars)
val DarkSurfaceDim              = AuraNeutral6
val DarkSurfaceBright           = AuraNeutral24
val DarkSurfaceContainerLowest  = AuraNeutral4
val DarkSurfaceContainerLow     = AuraNeutral10
val DarkSurfaceContainer        = AuraNeutral12
val DarkSurfaceContainerHigh    = AuraNeutral17
val DarkSurfaceContainerHighest = AuraNeutral22

// ─────────────────────────────────────────────────────────────────────────────
// 13b. FIXED COLOR ROLES — identical across light/dark (M3 "fixed" roles),
//      used for elements that must keep brand color regardless of theme
//      (e.g. story-ring "seen" badge, onboarding illustrations).
// ─────────────────────────────────────────────────────────────────────────────

val PrimaryFixed            = AuraPrimary90
val PrimaryFixedDim         = AuraPrimary80
val OnPrimaryFixed          = AuraPrimary10
val OnPrimaryFixedVariant   = AuraPrimary30

val SecondaryFixed          = AuraSecondary90
val SecondaryFixedDim       = AuraSecondary80
val OnSecondaryFixed        = AuraSecondary10
val OnSecondaryFixedVariant = AuraSecondary30

val TertiaryFixed           = AuraTertiary90
val TertiaryFixedDim        = AuraTertiary80
val OnTertiaryFixed         = AuraTertiary10
val OnTertiaryFixedVariant  = AuraTertiary30

// ─────────────────────────────────────────────────────────────────────────────
// 14. PLATFORM-SPECIFIC TINTS — distinct for Mobile vs Desktop variants
//     (per project rule: separate variants per form factor)
// ─────────────────────────────────────────────────────────────────────────────

// Mobile — slightly more saturated, smaller surface; relies on darker bg to save battery (OLED)
val MobileBgExtraDark  = Color(0xFF050507)
val MobileSurfaceRaised = Color(0xFF18181F)
val MobileSurfaceOverlay = Color(0xFF1F1F2A)

// Desktop — softer contrast, larger surface area, paper-like
val DesktopBgLight     = Color(0xFFFBFAFE)
val DesktopSurfaceCard = Color(0xFFFFFFFF)
val DesktopSurfaceSidebar = Color(0xFFF4F3F8)
val DesktopDivider     = Color(0xFFE5E5EB)

// ─────────────────────────────────────────────────────────────────────────────
// 15. ACCESSIBILITY CONTRAST HELPERS — minimal contrast guarantees
// ─────────────────────────────────────────────────────────────────────────────

/** Always-white text color guaranteed to pass WCAG AA on any brand surface. */
val TextOnBrandMin = Color(0xFFFFFFFF)

/** Always-dark text color guaranteed to pass WCAG AA on light containers. */
val TextOnContainerMin = Color(0xFF1A0A2E)

/** Disabled state tint applied to buttons / inputs. */
val DisabledTint      = Color(0xFFB0B0BB)
val DisabledContainer = Color(0xFFEFEFF2)
val OnDisabled        = Color(0xFF8E8E9B)