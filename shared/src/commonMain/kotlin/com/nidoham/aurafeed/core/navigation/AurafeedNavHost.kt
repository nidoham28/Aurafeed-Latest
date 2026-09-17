package com.nidoham.aurafeed.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.nidoham.aurafeed.ui.platform.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.shared.component.shimmer.ShimmerBlock
import com.nidoham.aurafeed.shared.component.shimmer.ShimmerCard
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import com.nidoham.aurafeed.ui.theme.LocalFormFactor
import com.nidoham.aurafeed.ui.theme.ScrimMedium
import kotlin.reflect.KClass

/**
 * Aurafeed — Navigation Host
 * ───────────────────────────────────────────────────────────
 *  Compose composable that wires [NavigationManager] state to UI.
 *
 *  Responsibilities:
 *   • Subscribe to currentRoute / currentTab / activeModal / canGoBack
 *   • Render the active screen via [ScreenRegistry]
 *   • Wrap content in Mobile (bottom-nav) or Desktop (sidebar) layout,
 *     picked from [LocalFormFactor] (per project rule: separate variants)
 *   • Render modal overlays on top of content
 *   • Wire back-button (Android HW back / Desktop ESC / iOS swipe via platform plugin)
 *
 *  Loading UX:
 *   • Routes not yet registered in the registry render [UnregisteredRouteShimmer]
 *     (per project rule: shimmer, no plain spinners)
 *   • Modal routes not yet wired render [GenericModalShimmer]
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. SCREEN REGISTRY
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A screen is a Composable factory. Routes map 1:1 to screens via their
 * runtime [KClass]. The screen receives the resolved Route and renders it.
 */
@Stable
fun interface Screen {
    @Composable
    fun Content(route: Route)
}

interface ScreenRegistry {
    fun resolve(route: Route): Screen?
}

/**
 * Mutable registry for registering screens at app startup.
 *
 * Usage:
 * ```
 * val registry = MutableScreenRegistry().apply {
 *     register<Route.Feed> { FeedScreen() }
 *     register<Route.ProfileView> { route -> ProfileScreen((route as Route.ProfileView).userId) }
 * }
 * AurafeedNavHost(registry = registry)
 * ```
 */
class MutableScreenRegistry : ScreenRegistry {
    @PublishedApi internal val screens: MutableMap<KClass<out Route>, Screen> = mutableMapOf()

    inline fun <reified T : Route> register(noinline content: @Composable (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val screen: Screen = Screen { route ->
            @Suppress("UNCHECKED_CAST")
            content(route as T)
        }
        screens[T::class] = screen
    }

    fun register(routeType: KClass<out Route>, screen: Screen) {
        screens[routeType] = screen
    }

    override fun resolve(route: Route): Screen? {
        return screens[route::class]
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. ROOT NAV HOST
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AurafeedNavHost(
    navManager: NavigationManager = remember { AurafeedNavigationManager() },
    registry: ScreenRegistry = remember { MutableScreenRegistry() },
) {
    val currentRoute: Route? by navManager.currentRoute.collectAsState()
    val currentTab: AurafeedTab by navManager.currentTab.collectAsState()
    val activeModal: ModalRoute? by navManager.activeModal.collectAsState()
    val canGoBack: Boolean by navManager.canGoBack.collectAsState()

    val formFactor: AurafeedFormFactor = LocalFormFactor.current
    val isDesktop: Boolean = formFactor == AurafeedFormFactor.Desktop

    // Back-button wiring: works on Android HW back, Desktop ESC (Compose Multiplatform 1.7+).
    // On older Compose versions, replace with expect/actual in ui/platform.
    BackHandler(enabled = canGoBack) {
        navManager.goBack()
    }

    val content: @Composable () -> Unit = {
        ScreenContainer(
            currentRoute = currentRoute,
            registry = registry,
        )
    }

    if (isDesktop) {
        DesktopRootLayout(
            currentTab = currentTab,
            onTabSelected = { tab -> navManager.switchTab(tab) },
            onCreateClick = { navManager.openModal(ModalRoute.PostComposer) },
            content = content,
        )
    } else {
        MobileRootLayout(
            currentTab = currentTab,
            onTabSelected = { tab -> navManager.switchTab(tab) },
            onCreateClick = { navManager.openModal(ModalRoute.PostComposer) },
            content = content,
        )
    }

    // Modal overlays render on top of the scaffold
    activeModal?.let { modal: ModalRoute ->
        ModalOverlayHost(
            modal = modal,
            navManager = navManager,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. SCREEN CONTAINER — route → screen lookup, shimmer fallback
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ScreenContainer(
    currentRoute: Route?,
    registry: ScreenRegistry,
) {
    if (currentRoute == null) {
        ShimmerSplashScreen()
        return
    }
    val screen: Screen? = remember(currentRoute) { registry.resolve(currentRoute) }
    if (screen != null) {
        screen.Content(currentRoute)
    } else {
        UnregisteredRouteShimmer(route = currentRoute)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. MOBILE ROOT — Scaffold + bottom navigation bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MobileRootLayout(
    currentTab: AurafeedTab,
    onTabSelected: (AurafeedTab) -> Unit,
    onCreateClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        bottomBar = {
            AurafeedBottomBar(
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                onCreateClick = onCreateClick,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            content()
        }
    }
}

@Composable
private fun AurafeedBottomBar(
    currentTab: AurafeedTab,
    onTabSelected: (AurafeedTab) -> Unit,
    onCreateClick: () -> Unit,
) {
    val tokens = AurafeedTheme.tokens
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = tokens.elevation.level1,
        modifier = Modifier.height(tokens.sizes.bottomBarHeight),
    ) {
        for (tab: AurafeedTab in AurafeedTab.entries) {
            val selected: Boolean = currentTab == tab
            if (tab == AurafeedTab.Create) {
                NavigationBarItem(
                    selected = false,
                    onClick = onCreateClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create",
                            modifier = Modifier.size(tokens.sizes.iconLg),
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            } else {
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = tab.icon(),
                            contentDescription = tab.labelKey,
                            modifier = Modifier.size(tokens.sizes.iconLg),
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. DESKTOP ROOT — Row + sidebar + content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DesktopRootLayout(
    currentTab: AurafeedTab,
    onTabSelected: (AurafeedTab) -> Unit,
    onCreateClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val tokens = AurafeedTheme.tokens
    Row(modifier = Modifier.fillMaxSize()) {
        DesktopSidebar(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            onCreateClick = onCreateClick,
            modifier = Modifier
                .fillMaxHeight()
                .width(tokens.sizes.desktopSidebarWidth),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            content()
        }
    }
}

@Composable
private fun DesktopSidebar(
    currentTab: AurafeedTab,
    onTabSelected: (AurafeedTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = AurafeedTheme.tokens
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = tokens.spacing.lg, horizontal = tokens.spacing.md),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
    ) {
        // Brand wordmark
        Text(
            text = "Aurafeed",
            style = typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = tokens.spacing.md,
                vertical = tokens.spacing.md,
            ),
        )

        Spacer(modifier = Modifier.height(tokens.spacing.lg))

        // Create button — accent pill
        SidebarCreateButton(onClick = onCreateClick)

        Spacer(modifier = Modifier.height(tokens.spacing.lg))

        // Tab items
        for (tab: AurafeedTab in AurafeedTab.entries) {
            if (tab == AurafeedTab.Create) continue
            SidebarNavItem(
                tab = tab,
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun SidebarCreateButton(onClick: () -> Unit) {
    val tokens = AurafeedTheme.tokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.corners.lg))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .padding(
                horizontal = tokens.spacing.md,
                vertical = tokens.spacing.sm + tokens.spacing.xs,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Create",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(tokens.sizes.iconLg),
        )
        Text(
            text = "Create",
            style = typography().labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SidebarNavItem(
    tab: AurafeedTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tokens = AurafeedTheme.tokens
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.corners.md))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = tokens.spacing.md,
                vertical = tokens.spacing.sm + tokens.spacing.xs,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md),
    ) {
        Icon(
            imageVector = tab.icon(),
            contentDescription = tab.labelKey,
            tint = contentColor,
            modifier = Modifier.size(tokens.sizes.iconLg),
        )
        Text(
            text = tab.labelKey.removePrefix("tab.").replaceFirstChar { it.uppercase() },
            style = typography().labelLarge,
            color = contentColor,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. MODAL OVERLAY HOST — routes modal state to sheet/dialog composables
//    Unwired modal types fall back to [GenericModalShimmer]
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ModalOverlayHost(
    modal: ModalRoute,
    navManager: NavigationManager,
) {
    val onDismiss: () -> Unit = { navManager.closeModal() }
    when (modal) {
        is ModalRoute.PostComposer,
        is ModalRoute.StoryComposer,
        is ModalRoute.ReelComposer,
        is ModalRoute.CommentReply,
        is ModalRoute.ImagePicker,
        is ModalRoute.ImageEditor,
        is ModalRoute.Camera,
        is ModalRoute.ForwardMessage,
        is ModalRoute.SharePost,
        is ModalRoute.ShareProfile,
        is ModalRoute.QrShare,
        is ModalRoute.BlockUser,
        is ModalRoute.RestrictUser,
        is ModalRoute.MuteUser,
        is ModalRoute.ReportContent,
        is ModalRoute.LogoutConfirm,
        is ModalRoute.NewDeviceAlert,
        is ModalRoute.SessionCloseConfirm,
        is ModalRoute.TwoFactorSetup,
        is ModalRoute.SettingsSheet,
        is ModalRoute.LanguagePicker,
        is ModalRoute.ThemePicker,
        is ModalRoute.ImageViewer,
        is ModalRoute.ConfirmDialog -> GenericModalShimmer(
            modal = modal,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun GenericModalShimmer(
    modal: ModalRoute,
    onDismiss: () -> Unit,
) {
    val tokens = AurafeedTheme.tokens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScrimMedium)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .padding(tokens.spacing.lg)
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(tokens.corners.xl),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = tokens.elevation.level3,
        ) {
            Column(
                modifier = Modifier.padding(tokens.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(tokens.spacing.md),
            ) {
                Text(
                    text = modal::class.simpleName ?: "Modal",
                    style = typography().titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                ShimmerBlock(aspectRatio = 4f)
                ShimmerCard()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. SHIMMER FALLBACKS — shown when no screen is registered yet
//    Per project rule: shimmer, never plain spinners
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ShimmerSplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ShimmerCard()
        ShimmerBlock(aspectRatio = 1f)
        ShimmerCard()
        ShimmerBlock(aspectRatio = 1f)
    }
}

@Composable
private fun UnregisteredRouteShimmer(route: Route) {
    val tokens = AurafeedTheme.tokens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(tokens.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.md),
    ) {
        ShimmerCard()
        repeat(3) {
            ShimmerBlock(aspectRatio = 1f)
            ShimmerCard()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. HELPERS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun typography() = MaterialTheme.typography

private fun AurafeedTab.icon(): ImageVector = when (this) {
    AurafeedTab.Feed -> Icons.Filled.Home
    AurafeedTab.Explore -> Icons.Filled.Explore
    AurafeedTab.Create -> Icons.Filled.Add
    AurafeedTab.Notifications -> Icons.Filled.Notifications
    AurafeedTab.Profile -> Icons.Filled.Person
}