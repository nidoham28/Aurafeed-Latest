package com.nidoham.aurafeed.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Aurafeed — Navigation Manager
 * ───────────────────────────────────────────────────────────
 *  Central, observable navigation state for the whole app.
 *
 *  Pure Kotlin — no Compose / Android / iOS / Desktop dependency.
 *  This makes the navigation graph unit-testable and lets the UI layer
 *  (composeNavHost on each platform) subscribe via `collectAsState()`.
 *
 *  Architecture:
 *   • [backStack]      — stack of full-screen [Route]s (LIFO)
 *   • [currentTab]      — the active bottom-nav / sidebar tab
 *   • [activeModal]     — currently open [ModalRoute], or null
 *   • Per-tab back stacks preserved when switching tabs
 *
 *  Per project rules:
 *   • "Never trust the client" — auth-gated routes are checked here, not in UI.
 *   • "No demo / deprecated APIs" — uses StateFlow, no deprecated navigation helpers.
 *   • Mobile + Desktop share the same graph — the layout composable picks bottom-nav vs sidebar.
 */

interface NavigationManager {
    val backStack: StateFlow<List<Route>>
    val currentTab: StateFlow<AurafeedTab>
    val activeModal: StateFlow<ModalRoute?>
    val currentRoute: StateFlow<Route?>
    val canGoBack: StateFlow<Boolean>

    fun navigate(route: Route)
    fun navigateClearingStack(route: Route)
    fun replace(route: Route)
    fun goBack(): Boolean
    fun popTo(route: Route): Boolean
    fun popToRoot()

    fun switchTab(tab: AurafeedTab)
    fun selectTabForRoute(route: Route)

    fun openModal(modal: ModalRoute)
    fun closeModal(): Boolean
    fun dismissModal(modal: ModalRoute? = null)

    fun handleDeepLink(uri: String): Boolean

    fun setAuthGate(predicate: (Route) -> Boolean)

    fun snapshot(): NavSnapshot
}

data class NavSnapshot(
    val backStack: List<Route>,
    val currentTab: AurafeedTab,
    val activeModal: ModalRoute?,
    val currentRoute: Route?,
    val canGoBack: Boolean,
)

// ─────────────────────────────────────────────────────────────────────────────
// 1. DEFAULT IMPLEMENTATION
// ─────────────────────────────────────────────────────────────────────────────

class AurafeedNavigationManager : NavigationManager {

    private val tabStacks: MutableMap<AurafeedTab, MutableList<Route>> = mutableMapOf()

    private val _backStack: MutableStateFlow<List<Route>> = MutableStateFlow(emptyList())
    private val _currentTab: MutableStateFlow<AurafeedTab> = MutableStateFlow(AurafeedTab.Feed)
    private val _activeModal: MutableStateFlow<ModalRoute?> = MutableStateFlow(null)
    private val _currentRoute: MutableStateFlow<Route?> = MutableStateFlow(null)
    private val _canGoBack: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val backStack: StateFlow<List<Route>> = _backStack.asStateFlow()
    override val currentTab: StateFlow<AurafeedTab> = _currentTab.asStateFlow()
    override val activeModal: StateFlow<ModalRoute?> = _activeModal.asStateFlow()
    override val currentRoute: StateFlow<Route?> = _currentRoute.asStateFlow()
    override val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private var authGate: (Route) -> Boolean = { true }

    init {
        switchTab(AurafeedTab.Feed)
    }

    // ── Stack operations ─────────────────────────────────────────────────

    override fun navigate(route: Route) {
        if (!checkAuth(route)) {
            return
        }
        val targetTab: AurafeedTab? = AurafeedTab.fromRoute(route)
        if (targetTab != null && targetTab != _currentTab.value && route === targetTab.route) {
            switchTab(targetTab)
            return
        }
        if (route is Route.Create) {
            openModal(ModalRoute.PostComposer)
            return
        }
        val stack: MutableList<Route> = currentTabStack()
        stack.add(route)
        refresh(stack)
    }

    override fun navigateClearingStack(route: Route) {
        if (!checkAuth(route)) {
            return
        }
        val tab: AurafeedTab = AurafeedTab.fromRoute(route) ?: AurafeedTab.Feed
        val tabRoute: Route = tab.route
        val newRoot: Route = if (route === tabRoute || route is Route.Feed) {
            tabRoute
        } else {
            route
        }
        val newStack: MutableList<Route> = mutableListOf(newRoot)
        tabStacks[tab] = newStack
        if (tab != _currentTab.value) {
            _currentTab.value = tab
        }
        refresh(newStack)
    }

    override fun replace(route: Route) {
        if (!checkAuth(route)) {
            return
        }
        val stack: MutableList<Route> = currentTabStack()
        if (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex)
        }
        stack.add(route)
        refresh(stack)
    }

    override fun goBack(): Boolean {
        if (_activeModal.value != null) {
            _activeModal.value = null
            refreshBackState()
            return true
        }
        val stack: MutableList<Route> = currentTabStack()
        if (stack.size <= 1) {
            return false
        }
        stack.removeAt(stack.lastIndex)
        refresh(stack)
        return true
    }

    override fun popTo(route: Route): Boolean {
        val stack: MutableList<Route> = currentTabStack()
        val idx: Int = stack.indexOfLast { it == route }
        if (idx < 0) {
            return false
        }
        while (stack.size > idx + 1) {
            stack.removeAt(stack.lastIndex)
        }
        refresh(stack)
        return true
    }

    override fun popToRoot() {
        val stack: MutableList<Route> = currentTabStack()
        if (stack.size > 1) {
            stack.subList(1, stack.size).clear()
            refresh(stack)
        }
    }

    // ── Tab operations ───────────────────────────────────────────────────

    override fun switchTab(tab: AurafeedTab) {
        if (tab == _currentTab.value) {
            val existing: MutableList<Route>? = tabStacks[tab]
            if (!existing.isNullOrEmpty()) {
                popToRoot()
                return
            }
        }
        _currentTab.value = tab
        val stack: MutableList<Route> = tabStacks.getOrPut(tab) {
            val newStack: MutableList<Route> = mutableListOf(tab.route)
            newStack
        }
        refresh(stack)
    }

    override fun selectTabForRoute(route: Route) {
        val tab: AurafeedTab = AurafeedTab.fromRoute(route) ?: return
        switchTab(tab)
        if (route !== tab.route) {
            navigate(route)
        }
    }

    // ── Modal operations ─────────────────────────────────────────────────

    override fun openModal(modal: ModalRoute) {
        _activeModal.value = modal
        refreshBackState()
    }

    override fun closeModal(): Boolean {
        if (_activeModal.value == null) {
            return false
        }
        _activeModal.value = null
        refreshBackState()
        return true
    }

    override fun dismissModal(modal: ModalRoute?) {
        if (modal == null) {
            _activeModal.value = null
            refreshBackState()
            return
        }
        if (_activeModal.value == modal) {
            _activeModal.value = null
            refreshBackState()
        }
    }

    // ── Deep links ──────────────────────────────────────────────────────

    override fun handleDeepLink(uri: String): Boolean {
        val route: Route = DeepLinkParser.parse(uri) ?: return false
        selectTabForRoute(route)
        return true
    }

    // ── Auth gating ─────────────────────────────────────────────────────

    override fun setAuthGate(predicate: (Route) -> Boolean) {
        authGate = predicate
    }

    private fun checkAuth(route: Route): Boolean {
        val allowed: Boolean = authGate(route)
        if (allowed) {
            return true
        }
        // Redirect to auth screen if not allowed
        val authStack: MutableList<Route> = mutableListOf()
        authStack.add(Route.Auth(AuthMode.SignIn))
        tabStacks[AurafeedTab.Profile] = authStack
        _currentTab.value = AurafeedTab.Profile
        refresh(authStack)
        return false
    }

    override fun snapshot(): NavSnapshot {
        return NavSnapshot(
            backStack = _backStack.value,
            currentTab = _currentTab.value,
            activeModal = _activeModal.value,
            currentRoute = _currentRoute.value,
            canGoBack = _canGoBack.value,
        )
    }

    // ── Internals ───────────────────────────────────────────────────────

    private fun currentTabStack(): MutableList<Route> {
        val tab: AurafeedTab = _currentTab.value
        val existing: MutableList<Route>? = tabStacks[tab]
        if (existing != null) {
            return existing
        }
        val newStack: MutableList<Route> = mutableListOf(tab.route)
        tabStacks[tab] = newStack
        return newStack
    }

    private fun refresh(stack: List<Route>) {
        _backStack.value = stack.toList()
        _currentRoute.value = stack.lastOrNull()
        refreshBackState()
    }

    private fun refreshBackState() {
        val modalOpen: Boolean = _activeModal.value != null
        val stack: MutableList<Route> = currentTabStack()
        val stackSize: Int = stack.size
        _canGoBack.value = modalOpen || stackSize > 1
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. DEEP LINK PARSER — pure-Kotlin URI parser
// ─────────────────────────────────────────────────────────────────────────────

object DeepLinkParser {

    fun parse(uri: String): Route? {
        val path: String = extractPath(uri)
        if (path.isBlank()) {
            return null
        }
        return parsePath(path)
    }

    private fun extractPath(uri: String): String {
        val noScheme: String = when {
            uri.startsWith("https://") -> uri.substringAfter("https://")
            uri.startsWith("http://") -> uri.substringAfter("http://")
            uri.startsWith("aurafeed://") -> uri.substringAfter("aurafeed://")
            uri.startsWith("intent://") -> uri.substringAfter("intent://")
            else -> uri
        }
        val pathStart: Int = noScheme.indexOf('/')
        if (pathStart < 0) {
            return ""
        }
        return noScheme.substring(pathStart)
    }

    private fun parsePath(pathWithQuery: String): Route? {
        val qIdx: Int = pathWithQuery.indexOf('?')
        val path: String = if (qIdx < 0) pathWithQuery else pathWithQuery.substring(0, qIdx)
        val query: String = if (qIdx < 0) "" else pathWithQuery.substring(qIdx + 1)
        val segments: List<String> = path.split('/').filter { it.isNotBlank() }
        if (segments.isEmpty()) {
            return Route.Feed
        }

        val head: String = segments[0]
        val seg1: String? = segments.getOrNull(1)
        val seg2: String? = segments.getOrNull(2)
        val seg3: String? = segments.getOrNull(3)

        return when (head) {
            "onboarding" -> Route.Onboarding

            "auth" -> when (seg1) {
                "signin" -> Route.Auth(AuthMode.SignIn)
                "signup" -> Route.Auth(AuthMode.SignUp)
                "forgot" -> {
                    val email: String? = parseQuery(query)["email"]
                    if (email != null) {
                        Route.ForgotPassword(decode(email))
                    } else {
                        Route.ForgotPassword(null)
                    }
                }
                "verify" -> Route.Auth(AuthMode.VerifyEmail)
                "google", "callback" -> Route.GoogleCallback
                else -> null
            }

            "feed" -> when (seg1) {
                null -> Route.Feed
                "following" -> Route.FollowingFeed
                "foryou" -> Route.ForYouFeed
                "favorites" -> Route.FavoritesFeed
                else -> null
            }

            "explore" -> when (seg1) {
                null -> Route.Explore
                "recent" -> Route.RecentSearches
                "trending" -> Route.TrendingNow
                "nearby" -> Route.NearbyPlaces
                else -> null
            }

            "create" -> Route.Create

            "notifications" -> when (seg1) {
                null -> Route.Notifications
                "follow-requests" -> Route.FollowRequests
                else -> {
                    if (seg1 != null) Route.NotificationDetail(decode(seg1)) else null
                }
            }

            "me" -> when (seg1) {
                null -> Route.Profile
                "edit" -> Route.EditProfile
                else -> null
            }

            "u" -> {
                if (seg1 == null) {
                    return null
                }
                val userId: String = decode(seg1)
                when (seg2) {
                    null -> Route.ProfileView(userId)
                    "saved" -> Route.Saved(userId)
                    "tagged" -> Route.Tagged(userId)
                    "followers" -> Route.Followers(userId)
                    "following" -> Route.Following(userId)
                    "highlights" -> {
                        val highlightId: String? = seg3?.let { decode(it) }
                        Route.Highlights(userId, highlightId)
                    }
                    else -> null
                }
            }

            "p" -> {
                if (seg1 == null) {
                    return null
                }
                val postId: String = decode(seg1)
                when (seg2) {
                    null -> Route.PostDetail(postId)
                    "comments" -> {
                        val q: Map<String, String> = parseQuery(query)
                        val focus: String? = q["c"]?.let { decode(it) }
                        val reply: String? = q["r"]?.let { decode(it) }
                        Route.Comments(postId, focus, reply)
                    }
                    else -> null
                }
            }

            "reels" -> {
                val reelId: String? = seg1?.let { decode(it) }
                Route.Reels(reelId)
            }

            "story" -> {
                if (seg1 == null) {
                    return null
                }
                Route.StoryViewer(decode(seg1))
            }

            "search" -> {
                val q: Map<String, String> = parseQuery(query)
                val queryStr: String = q["q"]?.let { decode(it) } ?: ""
                val filterStr: String? = q["f"]
                val filter: SearchFilter = if (filterStr != null) {
                    val capitalized: String = filterStr.replaceFirstChar { c -> c.uppercase() }
                    val parsed: SearchFilter? = runCatching { SearchFilter.valueOf(capitalized) }.getOrNull()
                    parsed ?: SearchFilter.All
                } else {
                    SearchFilter.All
                }
                Route.Search(queryStr, filter)
            }

            "dm" -> when (seg1) {
                null -> Route.DmList
                "new" -> {
                    val userIdsCsv: String? = parseQuery(query)["u"]
                    val userIds: List<String> = userIdsCsv?.split(",")?.map { decode(it) }
                        ?: emptyList()
                    Route.DmNew(userIds)
                }
                else -> {
                    val threadId: String = decode(seg1)
                    Route.DmThread(threadId)
                }
            }

            "scan" -> Route.QrScanner

            "settings" -> when (seg1) {
                null -> Route.Settings
                "account" -> Route.AccountSettings
                "privacy" -> Route.PrivacySettings
                "notifications" -> Route.NotificationSettings
                "appearance" -> Route.AppearanceSettings
                "storage" -> Route.StorageSettings
                "data" -> Route.DataSettings
                "about" -> Route.AboutSettings
                else -> null
            }

            "security" -> when (seg1) {
                null -> Route.SecurityCenter
                "devices" -> Route.DeviceTracking
                "sessions" -> when (seg2) {
                    null -> Route.ActiveSessions
                    else -> {
                        val sessionId: String = decode(seg2)
                        Route.SessionReview(sessionId)
                    }
                }
                "alerts" -> Route.SecurityAlerts
                else -> null
            }

            "moderation" -> when (seg1) {
                "blocked" -> Route.BlockedAccounts
                "restricted" -> Route.RestrictedAccounts
                "muted" -> Route.MutedAccounts
                "hidden" -> Route.HiddenAccounts
                "report" -> Route.ReportCenter(null)
                else -> null
            }

            else -> {
                if (head.startsWith("http://") || head.startsWith("https://")) {
                    Route.ExternalUrl(pathWithQuery)
                } else {
                    null
                }
            }
        }
    }

    private fun parseQuery(query: String): Map<String, String> {
        if (query.isBlank()) {
            return emptyMap()
        }
        val pairs: List<String> = query.split('&')
        val result: MutableMap<String, String> = mutableMapOf()
        for (pair in pairs) {
            val eq: Int = pair.indexOf('=')
            val k: String
            val v: String
            if (eq < 0) {
                k = pair
                v = ""
            } else {
                k = pair.substring(0, eq)
                v = pair.substring(eq + 1)
            }
            result[decode(k)] = decode(v)
        }
        return result
    }

    private fun decode(s: String): String {
        val sb: StringBuilder = StringBuilder(s.length)
        var i: Int = 0
        while (i < s.length) {
            val c: Char = s[i]
            if (c == '%' && i + 2 < s.length) {
                val hexStr: String = s.substring(i + 1, i + 3)
                val hex: Int? = hexStr.toIntOrNull(16)
                if (hex != null) {
                    sb.append(hex.toChar())
                    i += 3
                    continue
                }
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. GLOBAL ACCESSOR — convenience singleton
// ─────────────────────────────────────────────────────────────────────────────

object AurafeedNav : NavigationManager by AurafeedNavigationManager()
