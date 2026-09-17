package com.nidoham.aurafeed.core.navigation

import kotlin.text.iterator

/**
 * Aurafeed — Type-Safe Route Taxonomy
 * ───────────────────────────────────────────────────────────
 *  Every navigable destination in the app is encoded as a sealed-class
 *  instance. This gives us:
 *
 *   • Compile-time exhaustiveness when [when] blocks are used
 *   • Arguments encoded as fields (no fragile string templates)
 *   • Easy deep-link parsing / generation (see [NavigationManager.handleDeepLink])
 *   • Unit-testable without any Compose / UI dependency
 *
 *  Split across three hierarchies:
 *
 *   1. [Route]           — stack-pushed destinations (each one is a full screen)
 *   2. [ModalRoute]      — modal overlays (sheets, dialogs, fullscreen overlays)
 *   3. [AurafeedTab]     — bottom-nav / sidebar roots (each tab owns its own back stack)
 *
 *  Per project rule: Mobile uses bottom-nav, Desktop uses sidebar — both pull
 *  from the same [AurafeedTab] enum, so the navigation graph is form-factor-agnostic.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. STACK ROUTES — pushed onto the back stack
//    Convention: object for parameterless routes, data class for parameterized ones
// ─────────────────────────────────────────────────────────────────────────────

sealed class Route {

    // ── Onboarding & Auth ──────────────────────────────────────────────
    object Onboarding : Route()
    data class Auth(val mode: AuthMode = AuthMode.SignIn) : Route()
    data class ForgotPassword(val email: String? = null) : Route()
    object GoogleCallback : Route()   // OAuth deep-link target

    // ── Top-level tabs ──────────────────────────────────────────────────
    object Feed : Route()              // Home tab root
    object Explore : Route()           // Discover tab root
    object Create : Route()             // Composer entry (opens modal)
    object Notifications : Route()     // Activity tab root
    object Profile : Route()           // Own profile (current user)

    // ── Feed sub-tabs (persistent within Feed tab) ─────────────────────
    object FollowingFeed : Route()
    object ForYouFeed : Route()
    object FavoritesFeed : Route()

    // ── Content detail ─────────────────────────────────────────────────
    data class PostDetail(val postId: String) : Route()
    data class Reels(val initialReelId: String? = null) : Route()
    data class Comments(
        val postId: String,
        val focusCommentId: String? = null,
        val replyToCommentId: String? = null,
    ) : Route()
    data class StoryViewer(
        val userId: String,
        val storyId: String? = null,
    ) : Route()
    data class Highlights(
        val userId: String,
        val highlightId: String? = null,
    ) : Route()

    // ── User-scoped routes ──────────────────────────────────────────────
    data class ProfileView(val userId: String) : Route()
    object EditProfile : Route()
    data class Saved(val userId: String) : Route()
    data class Tagged(val userId: String) : Route()
    data class Followers(val userId: String) : Route()
    data class Following(val userId: String) : Route()

    // ── Search ──────────────────────────────────────────────────────────
    data class Search(
        val query: String = "",
        val filter: SearchFilter = SearchFilter.All,
    ) : Route()
    object RecentSearches : Route()
    object TrendingNow : Route()
    object NearbyPlaces : Route()

    // ── Messaging ──────────────────────────────────────────────────────
    object DmList : Route()
    data class DmThread(val threadId: String) : Route()
    data class DmNew(val userIds: List<String> = emptyList()) : Route()

    // ── Activity detail ────────────────────────────────────────────────
    data class NotificationDetail(val notificationId: String) : Route()
    object FollowRequests : Route()

    // ── Settings & Security (per project rule: device tracking) ───────
    object Settings : Route()
    object AccountSettings : Route()
    object PrivacySettings : Route()
    object NotificationSettings : Route()
    object AppearanceSettings : Route()
    object StorageSettings : Route()
    object DataSettings : Route()
    object AboutSettings : Route()

    object SecurityCenter : Route()
    object DeviceTracking : Route()
    object ActiveSessions : Route()
    object SecurityAlerts : Route()
    data class SessionReview(val sessionId: String) : Route()

    // ── Moderation ─────────────────────────────────────────────────────
    object BlockedAccounts : Route()
    object RestrictedAccounts : Route()
    object MutedAccounts : Route()
    object HiddenAccounts : Route()
    data class ReportCenter(val target: ReportTarget? = null) : Route()

    // ── Misc ───────────────────────────────────────────────────────────
    data class ExternalUrl(val url: String) : Route()
    object QrScanner : Route()
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. MODAL ROUTES — overlay sheets, dialogs, fullscreen composers
// ─────────────────────────────────────────────────────────────────────────────

sealed class ModalRoute {

    // ── Composers ──────────────────────────────────────────────────────
    object PostComposer : ModalRoute()
    data class StoryComposer(val source: StorySource) : ModalRoute()
    data class ReelComposer(val draftId: String? = null) : ModalRoute()
    data class CommentReply(
        val postId: String,
        val parentCommentId: String,
    ) : ModalRoute()

    // ── Media pickers / editors ────────────────────────────────────────
    data class ImagePicker(
        val maxSelection: Int = 1,
        val allowVideo: Boolean = false,
        val source: PickerSource = PickerSource.Both,
    ) : ModalRoute()
    data class ImageEditor(val uris: List<String>) : ModalRoute()
    object Camera : ModalRoute()

    // ── Sharing ────────────────────────────────────────────────────────
    data class ForwardMessage(val messageId: String, val postType: String) : ModalRoute()
    data class SharePost(val postId: String) : ModalRoute()
    data class ShareProfile(val userId: String) : ModalRoute()
    object QrShare : ModalRoute()

    // ── Moderation dialogs ─────────────────────────────────────────────
    data class BlockUser(val userId: String) : ModalRoute()
    data class RestrictUser(val userId: String) : ModalRoute()
    data class MuteUser(val userId: String, val muteDuration: MuteDuration) : ModalRoute()
    data class ReportContent(val target: ReportTarget) : ModalRoute()
    object LogoutConfirm : ModalRoute()

    // ── Security (per project rule: device tracking, session closure) ─
    object NewDeviceAlert : ModalRoute()
    data class SessionCloseConfirm(val sessionId: String) : ModalRoute()
    object TwoFactorSetup : ModalRoute()

    // ── Settings sheets ────────────────────────────────────────────────
    data class SettingsSheet(val section: SettingsSection? = null) : ModalRoute()
    object LanguagePicker : ModalRoute()
    object ThemePicker : ModalRoute()

    // ── Misc ───────────────────────────────────────────────────────────
    data class ImageViewer(val urls: List<String>, val startIndex: Int = 0) : ModalRoute()
    data class ConfirmDialog(
        val title: String,
        val message: String,
        val confirmAction: String,
    ) : ModalRoute()
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. TAB ROOTS — bottom nav (mobile) / sidebar (desktop)
// ─────────────────────────────────────────────────────────────────────────────

enum class AurafeedTab(val route: Route, val labelKey: String) {
    Feed(Route.Feed, "tab.feed"),
    Explore(Route.Explore, "tab.explore"),
    Create(Route.Create, "tab.create"),
    Notifications(Route.Notifications, "tab.notifications"),
    Profile(Route.Profile, "tab.profile");

    companion object {
        fun fromRoute(route: Route): AurafeedTab? = when (route) {
            is Route.FollowingFeed,
            is Route.ForYouFeed,
            is Route.FavoritesFeed,
            is Route.Feed -> Feed

            is Route.Explore,
            is Route.Search,
            is Route.RecentSearches,
            is Route.TrendingNow,
            is Route.NearbyPlaces -> Explore

            is Route.Notifications,
            is Route.NotificationDetail,
            is Route.FollowRequests -> Notifications

            is Route.Profile,
            is Route.EditProfile,
            is Route.Saved,
            is Route.Tagged,
            is Route.Followers,
            is Route.Following -> Profile

            else -> null
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. SUPPORTING ENUMS — argument types encoded inside routes
// ─────────────────────────────────────────────────────────────────────────────

enum class AuthMode { SignIn, SignUp, ForgotPassword, VerifyEmail }

enum class SearchFilter { All, Users, Hashtags, Places, Posts, Reels, Audio }

enum class PickerSource { Camera, Gallery, Both }

enum class MuteDuration { ThirtyDays, SixtyDays, NinetyDays, Forever }

enum class SettingsSection {
    Account, Privacy, Notifications, Appearance, Storage, Data, Security, About
}

enum class StorySource { Camera, Gallery, Draft }

// ─────────────────────────────────────────────────────────────────────────────
// 5. REPORT TARGET — moderation scope
// ─────────────────────────────────────────────────────────────────────────────

sealed class ReportTarget {
    data class Post(val postId: String) : ReportTarget()
    data class Comment(val commentId: String, val postId: String) : ReportTarget()
    data class Story(val storyId: String, val userId: String) : ReportTarget()
    data class Reel(val reelId: String) : ReportTarget()
    data class Message(val messageId: String, val threadId: String) : ReportTarget()
    data class User(val userId: String) : ReportTarget()
    data class DmThread(val threadId: String) : ReportTarget()
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. DEEP-LINK PATH ENCODING — for shareable URLs + external intents
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Convert a [Route] to its canonical deep-link path (no host).
 * Inverse of [NavigationManager.handleDeepLink].
 *
 * Examples:
 *   PostDetail("abc")          -> /p/abc
 *   ProfileView("u_123")       -> /u/u_123
 *   DmThread("t_456")          -> /dm/t_456
 *   Comments("p_1", null, null) -> /p/p_1/comments
 *   StoryViewer("u_2", null)   -> /story/u_2
 *   Search("kittens", All)     -> /search?q=kittens&f=all
 */
fun Route.toPath(): String = when (this) {
    Route.Onboarding -> "/onboarding"

    is Route.Auth -> when (mode) {
        AuthMode.SignIn -> "/auth/signin"
        AuthMode.SignUp -> "/auth/signup"
        AuthMode.ForgotPassword -> "/auth/forgot"
        AuthMode.VerifyEmail -> "/auth/verify"
    }

    is Route.ForgotPassword -> "/auth/forgot?email=${encodeURIComponent(email.orEmpty())}"
    Route.GoogleCallback -> "/auth/google/callback"

    Route.Feed -> "/feed"
    Route.FollowingFeed -> "/feed/following"
    Route.ForYouFeed -> "/feed/foryou"
    Route.FavoritesFeed -> "/feed/favorites"

    Route.Explore -> "/explore"
    Route.RecentSearches -> "/explore/recent"
    Route.TrendingNow -> "/explore/trending"
    Route.NearbyPlaces -> "/explore/nearby"

    Route.Create -> "/create"
    Route.Notifications -> "/notifications"
    Route.FollowRequests -> "/notifications/follow-requests"

    Route.Profile -> "/me"
    is Route.ProfileView -> "/u/$userId"
    Route.EditProfile -> "/me/edit"
    is Route.Saved -> "/u/$userId/saved"
    is Route.Tagged -> "/u/$userId/tagged"
    is Route.Followers -> "/u/$userId/followers"
    is Route.Following -> "/u/$userId/following"

    is Route.PostDetail -> "/p/$postId"
    is Route.Comments -> "/p/$postId/comments"
    is Route.Reels -> if (initialReelId != null) "/reels/$initialReelId" else "/reels"
    is Route.StoryViewer -> "/story/$userId"
    is Route.Highlights -> "/u/$userId/highlights" + (highlightId?.let { "/$it" } ?: "")

    is Route.Search -> {
        val q = if (query.isNotBlank()) "?q=${encodeURIComponent(query)}" else ""
        val prefix = if (q.isEmpty()) "?" else "&"
        val f = if (filter != SearchFilter.All) "${prefix}f=${filter.name.lowercase()}" else ""
        "/search$q$f"
    }

    Route.DmList -> "/dm"
    is Route.DmThread -> "/dm/$threadId"
    is Route.DmNew -> if (userIds.isEmpty()) "/dm/new" else "/dm/new?u=${userIds.joinToString(",")}"

    is Route.NotificationDetail -> "/notifications/$notificationId"

    Route.Settings -> "/settings"
    Route.AccountSettings -> "/settings/account"
    Route.PrivacySettings -> "/settings/privacy"
    Route.NotificationSettings -> "/settings/notifications"
    Route.AppearanceSettings -> "/settings/appearance"
    Route.StorageSettings -> "/settings/storage"
    Route.DataSettings -> "/settings/data"
    Route.AboutSettings -> "/settings/about"

    Route.SecurityCenter -> "/security"
    Route.DeviceTracking -> "/security/devices"
    Route.ActiveSessions -> "/security/sessions"
    Route.SecurityAlerts -> "/security/alerts"
    is Route.SessionReview -> "/security/sessions/$sessionId"

    Route.BlockedAccounts -> "/moderation/blocked"
    Route.RestrictedAccounts -> "/moderation/restricted"
    Route.MutedAccounts -> "/moderation/muted"
    Route.HiddenAccounts -> "/moderation/hidden"
    is Route.ReportCenter -> "/moderation/report"

    is Route.ExternalUrl -> url
    Route.QrScanner -> "/scan"
}

/**
 * URL-encode a path segment / query value.
 * Pure-Kotlin RFC-3986 implementation — no platform-specific dependencies,
 * so it works identically on Android, iOS, and Desktop JVM.
 */
private fun encodeURIComponent(s: String): String = buildString {
    for (c in s) {
        val v = c.code
        val safe = (v in '0'.code..'9'.code) ||
                (v in 'A'.code..'Z'.code) ||
                (v in 'a'.code..'z'.code) ||
                v == '-'.code || v == '_'.code || v == '.'.code || v == '~'.code
        if (safe) {
            append(c)
        } else {
            // Encode each UTF-16 char as percent-hex (covers ASCII + BMP)
            val hex = v.toString(16).padStart(2, '0').uppercase()
            append("%$hex")
        }
    }
}